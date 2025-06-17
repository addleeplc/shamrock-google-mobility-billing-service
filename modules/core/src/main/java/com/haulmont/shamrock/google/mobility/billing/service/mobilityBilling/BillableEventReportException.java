/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.service.mobilityBilling;

public class BillableEventReportException extends RuntimeException {

    private final int errorCode;
    private final String errorMessage;

    public BillableEventReportException(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BillableEventReportException(Throwable t) {
        errorCode = -1;
        errorMessage = t.getMessage();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
