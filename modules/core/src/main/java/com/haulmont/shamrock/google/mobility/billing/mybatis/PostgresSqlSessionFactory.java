/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.mybatis;

import com.haulmont.monaco.mybatis.SqlSessionFactoryResource;
import org.picocontainer.annotations.Component;

@Component
public class PostgresSqlSessionFactory extends SqlSessionFactoryResource {
    public static final String MYBATIS_NAMESPACE_PREFIX = "com.haulmont.shamrock.google.mobility.billing.cache.mybatis.mappers.";

    public PostgresSqlSessionFactory() {
        super("shamrock-mybatis.xml", "GOOGLE_MOBILITY_BILLING");
    }
}
