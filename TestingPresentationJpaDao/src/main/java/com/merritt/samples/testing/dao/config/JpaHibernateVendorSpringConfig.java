package com.merritt.samples.testing.dao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@Profile( "production" )
public class JpaHibernateVendorSpringConfig
{
    @Bean
    public JpaVendorAdapter jpaAdapter()
    {
        final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql( false );
        return hibernateJpaVendorAdapter;
    }
}
