/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.storage;

import com.haulmont.shamrock.google.mobility.billing.configuration.ConfigurationService;
import com.haulmont.shamrock.google.mobility.billing.configuration.ServiceConfiguration;
import com.haulmont.shamrock.google.mobility.billing.model.Booking;
import com.haulmont.shamrock.google.mobility.billing.mybatis.SybaseSqlSessionFactory;
import com.haulmont.shamrock.google.mobility.billing.storage.command.LoadBookingsCommand;
import org.joda.time.LocalDateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.List;

@Component
public class BookingStorage {

    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private SybaseSqlSessionFactory sqlSessionFactory;

    @Inject
    private Logger log;

    public List<Booking> loadBookings(LocalDateTime from, LocalDateTime to) {
        from = from.minus(serviceConfiguration.afterBookingDateDelay());
        to = to.minus(serviceConfiguration.afterBookingDateDelay());
        List<Booking> res = new LoadBookingsCommand(sqlSessionFactory,
                from,
                to,
                serviceConfiguration.coaEnabled(),
                configurationService.getAllowedAccountNumbers(),
                configurationService.getIncludeDates(),
                configurationService.getExcludeDates()).execute();
        log.info("Loaded {} bookings from: {} to: {}", res.size(), from, to);
        return res;
    }

}
