/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.storage.command;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.RangeSet;
import com.haulmont.monaco.mybatis.MyBatisCommand;
import com.haulmont.shamrock.google.mobility.billing.model.Booking;
import com.haulmont.shamrock.google.mobility.billing.mybatis.SybaseSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.LocalDateTime;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.haulmont.shamrock.google.mobility.billing.mybatis.SybaseSqlSessionFactory.MYBATIS_NAMESPACE_PREFIX;

public class LoadBookingsCommand extends MyBatisCommand<List<Booking>> {

    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean caoEnabled;
    private final Set<Long> allowedCustomerNumbers;
    private final Set<Map<String, LocalDateTime>> includeIntervals;
    private final Set<Map<String, LocalDateTime>> excludeIntervals;

    public LoadBookingsCommand(SybaseSqlSessionFactory sqlSessions,
                               LocalDateTime from,
                               LocalDateTime to,
                               boolean caoEnabled,
                               @Nonnull Set<Long> allowedCustomerNumbers,
                               @Nonnull RangeSet<LocalDateTime> includeIntervals,
                               @Nonnull RangeSet<LocalDateTime> excludeIntervals) {
        super(sqlSessions);
        this.from = from;
        this.to = to;
        this.caoEnabled = caoEnabled;
        this.allowedCustomerNumbers = allowedCustomerNumbers;
        this.includeIntervals = convert(includeIntervals);
        this.excludeIntervals = convert(excludeIntervals);
    }

    @Override
    protected List<Booking> __execute(SqlSession sqlSession) {
        return sqlSession.selectList(
                MYBATIS_NAMESPACE_PREFIX + getName(),
                ImmutableMap.of(
                        "from", from,
                        "to", to,
                        "coaEnabled", caoEnabled,
                        "allowedCustomerCodes", allowedCustomerNumbers,
                        "includeInterval", includeIntervals,
                        "excludeInterval", excludeIntervals
                )
        );
    }

    @Override
    protected String getName() {
        return "loadJobs";
    }

    private Set<Map<String, LocalDateTime>> convert(RangeSet<LocalDateTime> val) {
        return val.asRanges().stream()
                .map(it -> {
                    Map<String, LocalDateTime> res = null;
                    if (it.hasLowerBound() && it.hasUpperBound()) {
                        res = Map.of("from", it.lowerEndpoint(), "to", it.upperEndpoint());
                    } else  if (it.hasLowerBound()) {
                        res = Map.of("from", it.lowerEndpoint());
                    } else  if (it.hasUpperBound()) {
                        res = Map.of("to", it.upperEndpoint());
                    }

                    return res;
                })
                .collect(Collectors.toSet());
    }

}
