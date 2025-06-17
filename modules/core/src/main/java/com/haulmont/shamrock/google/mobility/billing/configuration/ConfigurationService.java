/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.configuration;

import com.google.common.collect.RangeSet;
import com.haulmont.monaco.AppContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.haulmont.shamrock.google.mobility.billing.configuration.ServiceConfiguration.*;
import static java.util.Map.entry;

@Component
public class ConfigurationService {

    @Inject
    private ServiceConfiguration serviceConfiguration;

    private RangeSet<LocalDateTime> includeDates;
    private RangeSet<LocalDateTime> excludeDates;

    private Set<Long> allowedAccountNumbers;

    private final Map<String, Consumer<String>> property2update = Map.ofEntries(
            entry(MOBILITY_BILLING_API_INCLUDE_DATES, this::updateIncludeDates ),
            entry(MOBILITY_BILLING_API_EXCLUDE_DATES, this::updateIncludeDates),
            entry(MOBILITY_BILLING_API_ALLOWED_ACCOUNT_NUMBERS, this::updateAllowedAccountNumbers)
    );

    public void start() {
        AppContext.getConfig().registerListener(event ->
                Optional.ofNullable(property2update.get(event.getKey()))
                        .ifPresent(it -> it.accept(event.getValue())));

        updateIncludeDates(serviceConfiguration.includeDates());
        updateExcludeDates(serviceConfiguration.excludeDates());
        updateAllowedAccountNumbers(serviceConfiguration.allowedAccountNumbers());
    }

    public RangeSet<LocalDateTime> getIncludeDates() {
        return includeDates;
    }

    public RangeSet<LocalDateTime> getExcludeDates() {
        return excludeDates;
    }

    public Set<Long> getAllowedAccountNumbers() {
        return allowedAccountNumbers;
    }

    private void updateIncludeDates(String val) {
        this.includeDates = ConfigurationUtils.parseDateRanges(val);
    }

    private void updateExcludeDates(String val) {
        this.excludeDates = ConfigurationUtils.parseDateRanges(val);
    }

    private void updateAllowedAccountNumbers(String val) {
        if (StringUtils.isBlank(val)) {
            allowedAccountNumbers = Collections.emptySet();
            return;
        }

        this.allowedAccountNumbers = Arrays.stream(val.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

}
