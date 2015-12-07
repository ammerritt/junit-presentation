package com.merritt.samples.testing.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@Profile( "ddl" )
public class JpaHibernateVendorDdlSpringConfig
{
    @Bean
    public JpaVendorAdapter jpaAdapter()
    {
        final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql( true );
        hibernateJpaVendorAdapter.setGenerateDdl( true );
        return hibernateJpaVendorAdapter;
    }
}
