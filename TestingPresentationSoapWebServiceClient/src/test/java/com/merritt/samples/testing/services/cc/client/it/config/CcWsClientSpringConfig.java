package com.merritt.samples.testing.services.cc.client.it.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.merritt.samples.testing.services.cc.client.CcService;

@Configuration
@Profile("production")
public class CcWsClientSpringConfig
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
