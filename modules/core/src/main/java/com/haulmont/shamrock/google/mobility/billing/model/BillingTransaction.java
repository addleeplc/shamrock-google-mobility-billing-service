/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.model;

import com.haulmont.shamrock.google.mobility.billing.service.mobilityBilling.BillableEventReportException;
import org.joda.time.LocalDateTime;

import java.util.UUID;

@SuppressWarnings("unused")
public class BillingTransaction {

    private UUID bookingId;
    private LocalDateTime bookingDate;
    private LocalDateTime bookingCompletedDate;
    private LocalDateTime processedTs;
    private LocalDateTime createTs;
    // failed
    private Integer responseCode;
    private String responseMessage;
    private Integer retries;

    public BillingTransaction() {
    }

    public BillingTransaction(Booking booking) {
        this.bookingId = booking.getUuid();
        this.bookingDate = booking.getJobDate();
        this.bookingCompletedDate = booking.getCompletedDate();
        this.createTs = LocalDateTime.now();
        this.processedTs = LocalDateTime.now();
    }

    public BillingTransaction updateFailure(BillableEventReportException e) {
        this.responseMessage = e.getErrorMessage();
        this.responseCode = e.getErrorCode();
        this.processedTs = LocalDateTime.now();
        if (this.retries == null) {
            this.retries = 1;
        } else {
            this.retries++;
        }
        return this;
    }

    public BillingTransaction clearError() {
        this.responseCode = null;
        this.responseMessage = null;
        this.retries = null;

        this.processedTs = LocalDateTime.now();
        return this;
    }

    public void incRetries() {
        retries++;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public LocalDateTime getBookingCompletedDate() {
        return bookingCompletedDate;
    }

    public void setBookingCompletedDate(LocalDateTime bookingCompletedDate) {
        this.bookingCompletedDate = bookingCompletedDate;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public Integer getRetries() {
        return retries;
    }

    public LocalDateTime getProcessedTs() {
        return processedTs;
    }

    public LocalDateTime getCreateTs() {
        return createTs;
    }


    public String getBookingDetails() {
        return "{" +
                "id=" + bookingId +
                ", date=" + bookingDate +
                ", completedDate=" + bookingCompletedDate +
                '}';
    }
}
