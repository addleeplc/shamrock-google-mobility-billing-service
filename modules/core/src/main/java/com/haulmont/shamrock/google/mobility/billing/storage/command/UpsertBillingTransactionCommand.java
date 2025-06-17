/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.storage.command;

import com.haulmont.monaco.mybatis.MyBatisCommand;
import com.haulmont.monaco.mybatis.SqlSessionFactoryResource;
import com.haulmont.shamrock.google.mobility.billing.model.BillingTransaction;
import org.apache.ibatis.session.SqlSession;

import java.util.Collections;

import static com.haulmont.shamrock.google.mobility.billing.mybatis.SybaseSqlSessionFactory.MYBATIS_NAMESPACE_PREFIX;

public class UpsertBillingTransactionCommand extends MyBatisCommand<Void> {

    private final BillingTransaction transaction;

    public UpsertBillingTransactionCommand(SqlSessionFactoryResource sqlSessionFactory, BillingTransaction transaction) {
        super(sqlSessionFactory);
        this.transaction = transaction;
    }

    @Override
    protected Void __execute(SqlSession sqlSession) {
        sqlSession.insert(MYBATIS_NAMESPACE_PREFIX + getName(), Collections.singletonMap("transaction", transaction));
        return null;
    }

    @Override
    protected String getName() {
        return "upsert";
    }
}
