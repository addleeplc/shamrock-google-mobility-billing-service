/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.service;

import com.haulmont.shamrock.google.mobility.billing.configuration.ServiceConfiguration;
import com.haulmont.shamrock.google.mobility.billing.model.Booking;
import com.haulmont.shamrock.google.mobility.billing.service.mobilityBilling.BillableEventReportException;
import com.haulmont.shamrock.google.mobility.billing.service.mobilityBilling.ReportBillableEventCommand;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.UUID;

@Component
public class MobilityBillingService {

    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private Logger log;

    /**
     * @param billableEventId For now, always {@link Booking#uuid}
     */
    public void reportBillableEvent(UUID billableEventId, String regionCode) {
        try {
            new ReportBillableEventCommand(billableEventId, regionCode, serviceConfiguration.apiKey()).execute();
        } catch (Throwable t) {
            if (t.getCause() instanceof BillableEventReportException) {
                throw (RuntimeException)t.getCause();
            } else {
                log.error("Failed to report billable event", t);
                throw new BillableEventReportException(t);
            }
        }
    }

}
