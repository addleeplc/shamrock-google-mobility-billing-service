/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.service.mobilityBilling;

import com.haulmont.monaco.unirest.UnirestCommand;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;

import java.util.UUID;

public class ReportBillableEventCommand extends UnirestCommand<String> {

    private static final String SERVICE = "mobility-billing";

    private final UUID billableEventId;
    private final String regionCode;
    private final String apiKey;

    public ReportBillableEventCommand(UUID billableEventId, String regionCode, String apiKey) {
        super(SERVICE, String.class);
        this.billableEventId = billableEventId;
        this.regionCode = regionCode;
        this.apiKey = apiKey;
    }

    @Override
    protected String handleError(Path path, HttpResponse<String> response) {
        throw new BillableEventReportException(response.getStatus(), response.getBody());
    }

    @Override
    protected HttpRequest<?> createRequest(String url, Path path) {
        return post(url, path)
                .queryString("regionCode", regionCode)
                .queryString("billableEventId", billableEventId.toString())
                .queryString("key", apiKey);
    }

    @Override
    protected Path getPath() {
        return new  Path("/v1:reportBillableEvent");
    }
}
