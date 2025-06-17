/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.configuration;

import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationUtils {

    private static final Pattern fullPattern = Pattern.compile(
            "^\\s*(\\[\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?\\s*/\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?\\s*])(\\s*;\\s*\\[\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?\\s*/\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?\\s*])*\\s*$"
    );
    private static final Pattern rangePattern = Pattern.compile(
            "\\[\\s*(?:(?<from>\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?)\\s*/\\s*(?:(?<to>\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?)\\s*\\]"
    );

    /**
     * Example: 2024-01-01:2024-01-10,2024-02-01:2024-02-05
     * @throws IllegalArgumentException if string malformed
     */
    public static RangeSet<LocalDateTime> parseDateRanges(@Nullable String val) {
        RangeSet<LocalDateTime> res = TreeRangeSet.create();

        if (StringUtils.isBlank(val)) return res;

        if (!fullPattern.matcher(val).matches()) {
            throw new IllegalArgumentException("Invalid format: " + val);
        }

        Matcher matcher = rangePattern.matcher(val);
        while (matcher.find()) {
            LocalDateTime from = Optional.ofNullable(matcher.group("from"))
                    .map(LocalDateTimeUtils::parseLocalDateTime)
                    .orElse(null);
            LocalDateTime to = Optional.ofNullable(matcher.group("to"))
                    .map(LocalDateTimeUtils::parseLocalDateTime)
                    .orElse(null);
            if (from != null && to != null) {
                res.add(Range.closed(from, to));
            } else  if (from != null) {
                res.add(Range.atLeast(from));
            } else  if (to != null) {
                res.add(Range.atMost(to));
            } else {
                throw new IllegalArgumentException("Invalid format: " + val);
            }
        }

        return res;
    }

}
