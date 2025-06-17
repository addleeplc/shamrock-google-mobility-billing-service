/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.haulmont.monaco.scheduler.annotations.Schedule;
import com.haulmont.monaco.scheduler.annotations.Scheduled;
import com.haulmont.shamrock.google.mobility.billing.configuration.ServiceConfiguration;
import com.haulmont.shamrock.google.mobility.billing.model.BillingTransaction;
import com.haulmont.shamrock.google.mobility.billing.model.Booking;
import com.haulmont.shamrock.google.mobility.billing.service.MobilityBillingService;
import com.haulmont.shamrock.google.mobility.billing.service.mobilityBilling.BillableEventReportException;
import com.haulmont.shamrock.google.mobility.billing.storage.BillingTransactionStorage;
import com.haulmont.shamrock.google.mobility.billing.storage.BookingStorage;
import com.haulmont.shamrock.google.mobility.billing.storage.RedisStorage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.LocalDateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Component
@Scheduled
public class MobilityBillingTracker {

    @Inject
    private BookingStorage bookingStorage;

    @Inject
    private BillingTransactionStorage billingTransactionStorage;

    @Inject
    private RedisStorage redisStorage;

    @Inject
    private MobilityBillingService mobilityBillingService;

    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private Logger log;

    @Schedule(schedule = "mobilityBilling.tracker.tick", singleton = true)
    public void execute() {
        try {
            log.info("Start submitting billable transactions. retriesThreshold: {}, afterBookingDateDelay: {}", serviceConfiguration.retriesThreshold(), serviceConfiguration.afterBookingDateDelay());
            long start = System.currentTimeMillis();
            Triple<Integer, Integer, Integer> res = __execute();
            long ms = System.currentTimeMillis() - start;
            log.info("Finished submitting billable transactions in {}m {}s. Successful: {}. SuccessfulResubmitted: {}. Failed: {}.", ms / 60000, (ms / 1000) % 60, res.getLeft(), res.getMiddle(), res.getRight());
        } catch (Throwable throwable) {
            log.error("Failed to execute MobilityBillingTracker#execute", throwable);
        }
    }

    private Triple<Integer, Integer, Integer> __execute() {
        AtomicInteger successful = new AtomicInteger(0);
        AtomicInteger successfulResubmitted = new AtomicInteger(0);
        AtomicInteger failed = new  AtomicInteger(0);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastProcessedCompleteDate = redisStorage.getLastProcessedCompleteDate();

        List<BillingTransaction> failedTransactions = billingTransactionStorage.getTransactions();
        log.info("Loaded {} failed transactions", failedTransactions.size());

        batched(transaction -> {
            try {
                if (processFailed(transaction)) {
                    successfulResubmitted.incrementAndGet();
                }
            } catch (Throwable t) {
                log.error(String.format("Failed to process failed transaction. booking: %s", transaction.getBookingDetails()), t);
            }
        }, failedTransactions);

        List<Booking> bookings = bookingStorage.loadBookings(lastProcessedCompleteDate, now);

        batched(booking -> {
            try {
                if (process(booking)) {
                    successful.incrementAndGet();
                } else  {
                    failed.incrementAndGet();
                }
            } catch (Throwable t) {
                failed.incrementAndGet();
                log.error(String.format("Failed to process booking: %s", booking), t);
            }
        }, bookings);

        redisStorage.saveLastProcessedCompleteDate(now);
        return Triple.of(successful.get(), successfulResubmitted.get(), failed.get());
    }

    @VisibleForTesting
    protected boolean process(Booking booking) {
        if (billingTransactionStorage.isAlreadyProcessed(booking.getUuid())) {
            log.info("Skip already submitted billable event for booking: {}", booking);
            return true;
        }

        BillingTransaction transaction = new BillingTransaction(booking);
        try {
            mobilityBillingService.reportBillableEvent(booking.getUuid(), "GB");
            billingTransactionStorage.upsert(transaction);
            log.info("Successfully submitted billable event for booking: {}", transaction.getBookingDetails());
            return true;
        }
        catch (BillableEventReportException e) {
            billingTransactionStorage.upsert(transaction.updateFailure(e));
            log.info("Failed to submit billable event for booking: {}. code: {}, message: {}", transaction.getBookingDetails(), e.getErrorCode(), e.getErrorMessage());
            return true;
        }
    }

    @VisibleForTesting
    protected boolean processFailed(BillingTransaction transaction) {
        try {
            mobilityBillingService.reportBillableEvent(transaction.getBookingId(), "GB");
            billingTransactionStorage.upsert(transaction.clearError());
            log.info("Successfully re-submitted billable event for booking {}", transaction.getBookingDetails());
            return true;
        } catch (BillableEventReportException e) {
            billingTransactionStorage.upsert(transaction.updateFailure(e));
            log.info("Failed to re-submit billable event for booking: {}. code: {}, message: {}", transaction.getBookingDetails(), e.getErrorCode(), e.getErrorMessage());
            return false;
        }
    }

    private <T> void batched(Consumer<T> consumer,  List<T> items) {
        if (CollectionUtils.isEmpty(items)) return;

        List<List<T>> batches = Lists.partition(items, serviceConfiguration.batchSize());
        for (List<T> batch : batches) {
            for  (T item : batch) {
                consumer.accept(item);
            }
            try {
                Thread.sleep(serviceConfiguration.batchCooldown().toStandardDuration().getMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

}
