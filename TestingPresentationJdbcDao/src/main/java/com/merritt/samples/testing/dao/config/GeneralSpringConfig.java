package com.merritt.samples.testing.dao.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.merritt.samples.testing.dao.impl")
public class GeneralSpringConfig
{
    public static final String JNDI_DATASOURCE_LOOKUP_NAME = "java:comp/env/jdbc/hsqlDataSource";
}
