/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing.mybatis;

import com.haulmont.monaco.mybatis.SqlSessionFactoryResource;
import org.picocontainer.annotations.Component;

@Component
public class SybaseSqlSessionFactory extends SqlSessionFactoryResource {
    public static final String MYBATIS_NAMESPACE_PREFIX = "com.haulmont.shamrock.google.mobility.billing.mybatis.mappers.";

    public SybaseSqlSessionFactory() {
        super("shamrock-mybatis.xml", "SHAMROCK");
    }
}
