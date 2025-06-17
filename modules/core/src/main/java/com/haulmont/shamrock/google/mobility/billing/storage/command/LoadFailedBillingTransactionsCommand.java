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
import java.util.List;

import static com.haulmont.shamrock.google.mobility.billing.mybatis.SybaseSqlSessionFactory.MYBATIS_NAMESPACE_PREFIX;

public class LoadFailedBillingTransactionsCommand extends MyBatisCommand<List<BillingTransaction>> {

    private final int retriesThreshold;

    public LoadFailedBillingTransactionsCommand(SqlSessionFactoryResource sqlSessionFactory, int retriesThreshold) {
        super(sqlSessionFactory);
        this.retriesThreshold = retriesThreshold;
    }

    @Override
    protected List<BillingTransaction> __execute(SqlSession sqlSession) {
        return sqlSession.selectList(MYBATIS_NAMESPACE_PREFIX + getName(), Collections.singletonMap("retriesThreshold", retriesThreshold));
    }

    @Override
    protected String getName() {
        return "loadFailed";
    }
}
