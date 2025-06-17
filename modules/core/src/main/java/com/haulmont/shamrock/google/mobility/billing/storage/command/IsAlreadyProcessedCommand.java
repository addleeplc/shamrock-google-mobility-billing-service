/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.storage.command;

import com.haulmont.monaco.mybatis.MyBatisCommand;
import com.haulmont.monaco.mybatis.SqlSessionFactoryResource;
import org.apache.ibatis.session.SqlSession;

import java.util.Collections;
import java.util.UUID;

import static com.haulmont.shamrock.google.mobility.billing.mybatis.SybaseSqlSessionFactory.MYBATIS_NAMESPACE_PREFIX;

public class IsAlreadyProcessedCommand extends MyBatisCommand<Boolean> {

    private final UUID bookingId;

    public IsAlreadyProcessedCommand(SqlSessionFactoryResource sqlSessionFactory, UUID bookingId) {
        super(sqlSessionFactory);
        this.bookingId = bookingId;
    }

    @Override
    protected Boolean __execute(SqlSession sqlSession) {
        return sqlSession.selectOne(MYBATIS_NAMESPACE_PREFIX + getName(), Collections.singletonMap("booking_id", bookingId));
    }

    @Override
    protected String getName() {
        return "isAlreadyProcessed";
    }
}
