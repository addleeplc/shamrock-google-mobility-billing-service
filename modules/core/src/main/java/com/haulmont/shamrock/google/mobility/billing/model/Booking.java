/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.haulmont.shamrock.google.mobility.billing.model;

import org.joda.time.LocalDateTime;

import java.util.*;

@SuppressWarnings("unused")
public class Booking {

    private Long id;
    private UUID uuid;
    private Long number;
    private LocalDateTime jobDate;
    private LocalDateTime completedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public LocalDateTime getJobDate() {
        return jobDate;
    }

    public void setJobDate(LocalDateTime jobDate) {
        this.jobDate = jobDate;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + uuid +
                ", date=" + jobDate +
                ", completedDate=" + completedDate +
                '}';
    }
}
