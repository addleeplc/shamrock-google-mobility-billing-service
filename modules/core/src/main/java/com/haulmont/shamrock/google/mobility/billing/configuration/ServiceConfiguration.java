/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.configuration;

import com.haulmont.monaco.config.annotations.Config;
import com.haulmont.monaco.config.annotations.Property;
import org.joda.time.Period;
import org.picocontainer.annotations.Component;

@Config
@Component
public interface ServiceConfiguration {

    String MOBILITY_BILLING_API_INCLUDE_DATES =  "includeDates";
    String MOBILITY_BILLING_API_EXCLUDE_DATES =  "excludeDates";

    String MOBILITY_BILLING_API_ALLOWED_ACCOUNT_NUMBERS = "allowedAccountNumbers";

    @Property(MOBILITY_BILLING_API_INCLUDE_DATES)
    String includeDates();

    @Property(MOBILITY_BILLING_API_EXCLUDE_DATES)
    String excludeDates();

    @Property("afterBookingDateDelay")
    Period afterBookingDateDelay();

    @Property("CoaEnabled")
    Boolean coaEnabled();

    @Property(MOBILITY_BILLING_API_ALLOWED_ACCOUNT_NUMBERS)
    String allowedAccountNumbers();

    @Property("retriesThreshold")
    Integer retriesThreshold();

    @Property("batchSize")
    Integer batchSize();

    @Property("batchCooldown")
    Period batchCooldown();

    @Property("mobilityBilling.api.key")
    String apiKey();

    @Property("redis.resourceName")
    String redisResourceName();

}
