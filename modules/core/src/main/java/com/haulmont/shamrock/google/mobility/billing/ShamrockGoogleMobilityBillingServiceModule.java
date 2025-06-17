/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.google.mobility.billing;

import com.haulmont.monaco.annotations.Module;
import com.haulmont.monaco.container.ModuleLoader;

@Module(
        name = "shamrock-google-mobility-billing-service-module",
        depends = {
                "monaco-jetty",
                "monaco-core",
                "monaco-config",
                "monaco-graylog-reporter",
                "monaco-sentry-reporter",
                "monaco-ds",
                "monaco-ds-postgresql",
                "monaco-ds-sybase",
                "monaco-mybatis",
                "monaco-redis",
                "monaco-scheduler",
                "monaco-sql2o",
                "monaco-unirest"
        }
)
public class ShamrockGoogleMobilityBillingServiceModule extends ModuleLoader {

    public ShamrockGoogleMobilityBillingServiceModule () {
        super();
        packages(ShamrockGoogleMobilityBillingServiceModule.class.getPackageName());
    }
}
