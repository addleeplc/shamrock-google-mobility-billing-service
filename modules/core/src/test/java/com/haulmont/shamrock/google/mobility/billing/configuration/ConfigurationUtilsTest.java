package com.haulmont.shamrock.google.mobility.billing.configuration;

import com.google.common.collect.RangeSet;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ConfigurationUtilsTest {

    @Test
    public void testSingleRange() {
        String input = "[2024-01-01T00:00:00/2024-01-10T23:59:00]";
        RangeSet<LocalDateTime> result = ConfigurationUtils.parseDateRanges(input);

        assertTrue(result.contains(LocalDateTime.parse("2024-01-01T00:00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2024-01-05T12:00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2024-01-10T23:59:00")));
        assertFalse(result.contains(LocalDateTime.parse("2024-01-11T00:00:00")));
    }

    @Test
    public void testMultipleRanges() {
        String input = "[2024-01-01T00:00:00/2024-01-10T23:59:00];[2024-02-01T00:00:00/2024-02-05T12:00:00]";
        RangeSet<LocalDateTime> result = ConfigurationUtils.parseDateRanges(input);

        assertTrue(result.contains(LocalDateTime.parse("2024-01-02T08:00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2024-02-03T11:59:59")));
        assertFalse(result.contains(LocalDateTime.parse("2024-01-11T00:00")));
        assertFalse(result.contains(LocalDateTime.parse("2024-03-01T00:00")));
    }

    @Test
    public void testSpacesAroundCommasAreAccepted() {
        String input = "[2024-01-01T00:00:00/2024-01-10T00:00:00];[2024-02-01T00:00:00/2024-02-05T00:00:00]";
        RangeSet<LocalDateTime> result = ConfigurationUtils.parseDateRanges(input);

        assertTrue(result.contains(LocalDateTime.parse("2024-01-03T00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2024-02-05T00:00")));
    }

    @Test
    public void testOpenEndedStart() {
        String input = "[/2024-01-10T12:00:00]";
        RangeSet<LocalDateTime> result = ConfigurationUtils.parseDateRanges(input);

        assertTrue(result.contains(LocalDateTime.parse("1900-01-01T00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2024-01-10T12:00")));
        assertFalse(result.contains(LocalDateTime.parse("2024-01-11T00:00")));
    }

    @Test
    public void testOpenEndedEnd() {
        String input = "[2024-01-01T00:00:00/]";
        RangeSet<LocalDateTime> result = ConfigurationUtils.parseDateRanges(input);

        assertTrue(result.contains(LocalDateTime.parse("2024-01-01T00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2100-12-31T23:59")));
        assertFalse(result.contains(LocalDateTime.parse("1900-01-01T00:00")));
    }

    @Test
    public void testMixedOpenAndClosedRanges() {
        String input = "[2024-01-01T00:00:00/2024-01-10T00:00:00];[/2024-01-01T00:00:00];[2024-01-12T00:00:00/]";
        RangeSet<LocalDateTime> result = ConfigurationUtils.parseDateRanges(input);

        assertTrue(result.contains(LocalDateTime.parse("2023-12-31T00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2024-01-01T00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2024-01-10T00:00")));
        assertTrue(result.contains(LocalDateTime.parse("2024-02-01T00:00")));
        assertFalse(result.contains(LocalDateTime.parse("2024-01-11T23:59")));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMalformedRangeShouldFail() {
        ConfigurationUtils.parseDateRanges("[2024-01-01-2024-01-10]");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGarbageInStringShouldFail() {
        ConfigurationUtils.parseDateRanges("[2024-01-01T00:00:00/2024-01-10T00:00:00] abc");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMalformedOpenRangeLeftColonOnly() {
        ConfigurationUtils.parseDateRanges("[/2024-01-01T00:00:00]/2024-01-10T00:00:00");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMalformedOpenRangeDoubleColon() {
        ConfigurationUtils.parseDateRanges("[2024-01-01T00:00:00//2024-01-10T00:00:00]");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFullyOpenRange() {
        ConfigurationUtils.parseDateRanges("[/]");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRandomTextShouldFail() {
        ConfigurationUtils.parseDateRanges("hello world");
    }

}
