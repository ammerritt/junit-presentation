package com.merritt.samples.testing.dao.config;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.merritt.samples.testing.dao.impl")
@EnableTransactionManagement
public class GeneralJpaSpringConfig
{
    public static final String JNDI_DATASOURCE_LOOKUP_NAME = "java:comp/env/jdbc/hsqlDataSource";
    
    @Inject
    private transient JpaVendorAdapter jpaAdapter;
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() throws NamingException
    {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource( dataSource() );
        factoryBean.setPackagesToScan( new String[] { "com.merritt.samples.testing.bean" } );
        factoryBean.setJpaVendorAdapter( jpaAdapter );
        return factoryBean;
    }
    
    @Bean
    public DataSource dataSource() throws NamingException
    {
        final Context ctx = new InitialContext();
        return (DataSource)ctx.lookup( JNDI_DATASOURCE_LOOKUP_NAME );
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() throws NamingException
    {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( entityManagerFactoryBean().getObject() );
        
        return transactionManager;
    }
    
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation()
    {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
