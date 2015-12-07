package com.merritt.samples.testing.services.cc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.remoting.jaxws.SimpleJaxWsServiceExporter;

import com.merritt.samples.testing.services.cc.CcServiceImpl;

@Configuration
public class GeneralCcServiceSpringConfig
{
    @Configuration
    @PropertySource( "classpath:service.properties" )
    @Profile( "production" )
    static class JaxWsServiceExporterConfig
    {
        @Value( "${service.base.address}" )
        private transient String baseAddress;
        
        @Bean
        public SimpleJaxWsServiceExporter simpleJaxWsServiceExporter()
        {
            final SimpleJaxWsServiceExporter simpleJaxWsServiceExporter = new SimpleJaxWsServiceExporter();
            simpleJaxWsServiceExporter.setBaseAddress( baseAddress );
            return simpleJaxWsServiceExporter;
        }
        
        /**
        * This is the magic that enables variable replacements in @Value definitions
        */
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
        {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
    
    @Bean
    public CcServiceImpl ccServiceImpl()
    {
        return new CcServiceImpl();
    }
}
