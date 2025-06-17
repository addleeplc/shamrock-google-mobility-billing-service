/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.storage;

import com.haulmont.monaco.AppContext;
import com.haulmont.monaco.redis.Redis;
import com.haulmont.monaco.redis.cache.RedisCacheKeyCodec;
import com.haulmont.monaco.redis.cache.codec.PropertyObjectCodec;
import com.haulmont.shamrock.google.mobility.billing.configuration.ServiceConfiguration;
import org.joda.time.LocalDateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.Optional;

@Component
public class RedisStorage {

    @Inject
    private ServiceConfiguration serviceConfiguration;
    @Inject
    private Logger log;

    private Redis<String, String> redis;

    private final String SUBMIT_TRANSACTIONS_PROCESSED_TS = "mobilityBilling.tracker.submitTransactions";
    private final RedisCacheKeyCodec<String> lastProcessedCompleteDateKeyCodec = new PropertyObjectCodec<>(SUBMIT_TRANSACTIONS_PROCESSED_TS, String.class, null);

    public LocalDateTime getLastProcessedCompleteDate() {
        return Optional.ofNullable(getRedis().get(lastProcessedCompleteDateKeyCodec.encode(null)))
                .map(LocalDateTime::parse)
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    log.info("{} is null so initialize with current time: {}", SUBMIT_TRANSACTIONS_PROCESSED_TS, now);
                    return now;
                });
    }

    public void saveLastProcessedCompleteDate(LocalDateTime lastProcessedCompleteDate) {
        getRedis().set(lastProcessedCompleteDateKeyCodec.encode("processedTs"), lastProcessedCompleteDate.toString());
        log.info("Save {}. Value: {}.", SUBMIT_TRANSACTIONS_PROCESSED_TS, lastProcessedCompleteDate);
    }

    public Redis<String, String> getRedis() {
        if (redis == null) {
            //noinspection unchecked
            redis = AppContext.getResources().get(serviceConfiguration.redisResourceName(), Redis.class);
        }

        return redis;
    }
}
