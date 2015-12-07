package com.merritt.samples.testing.services.cc.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.merritt.samples.testing.services.cc.api.CcService;
import com.merritt.samples.testing.services.cc.api.CcService.CcResult;
import com.merritt.samples.testing.services.cc.config.GeneralCcServiceSpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { GeneralCcServiceSpringConfig.class }, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles({ "production" })
public class CcServiceIT
{
    private static final String CC_NUMBER_LUHN_INVALID = "1111111111111111";
    private static final String CC_NUMBER_LUHN_VALID = "4417123456789113";
    
    private static CcService ccService;
    
    @Before
    public void createClient() throws MalformedURLException
    {
        final URL wsdlUrl = new URL( "http://localhost:8888/services/CcService?wsdl" );
        final QName qName = new QName( "http://cc.services.testing.samples.merritt.com/", "CcService" );
        final Service service = Service.create( wsdlUrl, qName );
        ccService = service.getPort( CcService.class );
    }
    
    @Test
    public void testProcessCard()
    {
        final CcResult result = ccService.processCard( CC_NUMBER_LUHN_VALID, 25.25d );
        assertNotNull( "The result should not be null.", result );
        assertEquals( "The result should have been success.", CcResult.SUCCESS, result );
    }
    
    @Test(expected = SOAPFaultException.class)
    public void testProcessCard_InvalidCcLuhn() throws Exception
    {
        ccService.processCard( CC_NUMBER_LUHN_INVALID, 25.25d );
    }
}
