/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing;

import com.haulmont.shamrock.google.mobility.billing.configuration.ServiceConfiguration;
import com.haulmont.shamrock.google.mobility.billing.model.BillingTransaction;
import com.haulmont.shamrock.google.mobility.billing.model.Booking;
import com.haulmont.shamrock.google.mobility.billing.service.MobilityBillingService;
import com.haulmont.shamrock.google.mobility.billing.service.mobilityBilling.BillableEventReportException;
import com.haulmont.shamrock.google.mobility.billing.storage.BillingTransactionStorage;
import com.haulmont.shamrock.google.mobility.billing.storage.BookingStorage;
import com.haulmont.shamrock.google.mobility.billing.storage.RedisStorage;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class MobilityBillingTrackerTest {

    @Spy
    @InjectMocks
    private MobilityBillingTracker tracker;

    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private BillingTransactionStorage billingTransactionStorage;
    @Mock
    private RedisStorage redisStorage;
    @Mock
    private MobilityBillingService mobilityBillingService;
    @Mock
    private ServiceConfiguration serviceConfiguration;
    @Mock
    private Logger log;

    @SuppressWarnings("resource")
    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(tracker, "bookingStorage", bookingStorage);
        ReflectionTestUtils.setField(tracker, "billingTransactionStorage", billingTransactionStorage);
        ReflectionTestUtils.setField(tracker, "redisStorage", redisStorage);
        ReflectionTestUtils.setField(tracker, "mobilityBillingService", mobilityBillingService);
        ReflectionTestUtils.setField(tracker, "serviceConfiguration", serviceConfiguration);
        ReflectionTestUtils.setField(tracker, "log", log);

        when(serviceConfiguration.batchSize()).thenReturn(10);
        when(serviceConfiguration.batchCooldown()).thenReturn(Period.seconds(1));
    }

    @Test
    public void testExecute_withNoJobsAndNoFailures() {
        when(serviceConfiguration.retriesThreshold()).thenReturn(3);
        when(serviceConfiguration.afterBookingDateDelay()).thenReturn(Period.hours(1));
        when(billingTransactionStorage.getTransactions()).thenReturn(Collections.emptyList());
        when(redisStorage.getLastProcessedCompleteDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(bookingStorage.loadBookings(any(), any())).thenReturn(Collections.emptyList());

        tracker.execute();

        verify(redisStorage).saveLastProcessedCompleteDate(any());
    }

    @Test
    public void test__execute_processFailedTransaction_successfulRetry() {
        UUID bookingId = UUID.randomUUID();
        BillingTransaction tx = mock(BillingTransaction.class);
        when(tx.getBookingId()).thenReturn(bookingId);
        when(billingTransactionStorage.getTransactions()).thenReturn(Collections.singletonList(tx));
        doNothing().when(mobilityBillingService).reportBillableEvent(bookingId, "GB");
        when(redisStorage.getLastProcessedCompleteDate()).thenReturn(LocalDateTime.now());
        when(bookingStorage.loadBookings(any(), any())).thenReturn(Collections.emptyList());

        tracker.execute();

        verify(mobilityBillingService).reportBillableEvent(bookingId, "GB");
        verify(tx).clearError();
        verify(billingTransactionStorage).upsert(any());
        verify(redisStorage).saveLastProcessedCompleteDate(any());
    }

    @Test
    public void test__execute_processFailedTransaction_unsuccessfulRetry() {
        BillableEventReportException failure = mock(BillableEventReportException.class);

        UUID bookingId = UUID.randomUUID();
        BillingTransaction tx = mock(BillingTransaction.class);
        when(tx.getBookingId()).thenReturn(bookingId);
        when(billingTransactionStorage.getTransactions()).thenReturn(Collections.singletonList(tx));
        doThrow(failure).when(mobilityBillingService).reportBillableEvent(bookingId, "GB");
        when(redisStorage.getLastProcessedCompleteDate()).thenReturn(LocalDateTime.now());
        when(bookingStorage.loadBookings(any(), any())).thenReturn(Collections.emptyList());

        tracker.execute();

        verify(tx).updateFailure(failure);
        verify(mobilityBillingService).reportBillableEvent(bookingId, "GB");
        verify(billingTransactionStorage).upsert(any());
        verify(redisStorage).saveLastProcessedCompleteDate(any());
    }

    @Test
    public void test__execute_jobNotFilteredByCOA() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = mock(Booking.class);
        when(booking.getUuid()).thenReturn(bookingId);
        when(booking.getCompletedDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(billingTransactionStorage.getTransactions()).thenReturn(Collections.emptyList());
        when(redisStorage.getLastProcessedCompleteDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(bookingStorage.loadBookings(any(), any())).thenReturn(Collections.singletonList(booking));
        when(serviceConfiguration.coaEnabled()).thenReturn(false);

        tracker.execute();

        verify(mobilityBillingService).reportBillableEvent(bookingId, "GB");
        verify(tracker, times(1)).process(any());
        verify(redisStorage).saveLastProcessedCompleteDate(any());
    }

    @Test
    public void test__execute_jobNotFilteredByDateExclusion() {
        Booking booking = mock(Booking.class);
        when(booking.getCompletedDate()).thenReturn(LocalDateTime.now());
        when(billingTransactionStorage.getTransactions()).thenReturn(Collections.emptyList());
        when(redisStorage.getLastProcessedCompleteDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(bookingStorage.loadBookings(any(), any())).thenReturn(Collections.singletonList(booking));
        when(serviceConfiguration.coaEnabled()).thenReturn(true);

        tracker.execute();

        verify(tracker, times(1)).process(booking);
        verify(redisStorage).saveLastProcessedCompleteDate(any());
    }

    @Test
    public void test__execute_jobReportedSuccessfully() {
        UUID jobId = UUID.randomUUID();
        Booking booking = mock(Booking.class);
        when(booking.getCompletedDate()).thenReturn(LocalDateTime.now());
        when(booking.getUuid()).thenReturn(jobId);
        when(billingTransactionStorage.getTransactions()).thenReturn(Collections.emptyList());
        when(redisStorage.getLastProcessedCompleteDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(bookingStorage.loadBookings(any(), any())).thenReturn(Collections.singletonList(booking));
        when(serviceConfiguration.coaEnabled()).thenReturn(true);
        doNothing().when(mobilityBillingService).reportBillableEvent(jobId, "GB");

        tracker.execute();

        verify(tracker, times(1)).process(booking);
        verify(redisStorage).saveLastProcessedCompleteDate(any());
    }

    @Test
    public void test__execute_jobReportedUnsuccessfully() {
        BillableEventReportException failure = mock(BillableEventReportException.class);

        UUID jobId = UUID.randomUUID();
        Booking booking = mock(Booking.class);
        when(booking.getCompletedDate()).thenReturn(LocalDateTime.now());
        when(booking.getUuid()).thenReturn(jobId);
        when(billingTransactionStorage.getTransactions()).thenReturn(Collections.emptyList());
        when(redisStorage.getLastProcessedCompleteDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(bookingStorage.loadBookings(any(), any())).thenReturn(Collections.singletonList(booking));
        when(serviceConfiguration.coaEnabled()).thenReturn(true);
        doThrow(failure).when(mobilityBillingService).reportBillableEvent(jobId, "GB");

        tracker.execute();

        verify(tracker, times(1)).process(booking);
        verify(billingTransactionStorage, times(1)).upsert(any());
        verify(redisStorage).saveLastProcessedCompleteDate(any());
    }

    @Test
    public void testExecute_withException() {
        when(serviceConfiguration.retriesThreshold()).thenThrow(new RuntimeException("boom"));

        tracker.execute();

        verify(log).error(contains("Failed to execute MobilityBillingTracker#execute"), any(Throwable.class));
    }
}