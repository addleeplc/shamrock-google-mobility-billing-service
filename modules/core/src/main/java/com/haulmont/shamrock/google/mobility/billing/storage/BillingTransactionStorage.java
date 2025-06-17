/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.storage;

import com.haulmont.monaco.mybatis.MyBatisCommand;
import com.haulmont.shamrock.google.mobility.billing.configuration.ServiceConfiguration;
import com.haulmont.shamrock.google.mobility.billing.model.BillingTransaction;
import com.haulmont.shamrock.google.mobility.billing.mybatis.PostgresSqlSessionFactory;
import com.haulmont.shamrock.google.mobility.billing.storage.command.IsAlreadyProcessedCommand;
import com.haulmont.shamrock.google.mobility.billing.storage.command.LoadFailedBillingTransactionsCommand;
import com.haulmont.shamrock.google.mobility.billing.storage.command.UpsertBillingTransactionCommand;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BillingTransactionStorage {

    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private PostgresSqlSessionFactory sqlSessionFactory;

    public List<BillingTransaction> getTransactions() {
        return Optional.of(new LoadFailedBillingTransactionsCommand(sqlSessionFactory, serviceConfiguration.retriesThreshold()))
                .map(MyBatisCommand::execute)
                .orElse(Collections.emptyList());
    }

    public void upsert(BillingTransaction transaction) {
        new UpsertBillingTransactionCommand(sqlSessionFactory, transaction).execute();
    }

    public boolean isAlreadyProcessed(UUID bookingId) {
        return new IsAlreadyProcessedCommand(sqlSessionFactory, bookingId).execute();
    }

}
