package com.merritt.samples.testing.business.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.merritt.samples.testing.business.PaymentProcessorImpl;
import com.merritt.samples.testing.business.api.PaymentProcessor;
import com.merritt.samples.testing.dao.config.GeneralJpaSpringConfig;
import com.merritt.samples.testing.dao.config.JpaHibernateVendorSpringConfig;
import com.merritt.samples.testing.services.cc.client.CcService;

@Configuration
public class GeneralBusinessSpringConfig
{
    @Bean
    public PaymentProcessor paymentProcessor()
    {
        return new PaymentProcessorImpl();
    }
    
    @Configuration
    @Profile( "production" )
    @Import( { GeneralJpaSpringConfig.class, JpaHibernateVendorSpringConfig.class } )
    static class LiveCcServiceConfig
    {
        @Bean
        public CcService ccService() throws MalformedURLException
        {
            final URL wsdlUrl = new URL( "http://localhost:8888/services/CcService?wsdl" );
            final QName qName = new QName( "http://cc.services.testing.samples.merritt.com/", "CcService" );
            final Service service = Service.create( wsdlUrl, qName );
            return service.getPort( CcService.class );
        }
    }
}
