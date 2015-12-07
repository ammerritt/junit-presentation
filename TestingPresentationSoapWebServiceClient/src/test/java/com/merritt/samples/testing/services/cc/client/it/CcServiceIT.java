package com.merritt.samples.testing.services.cc.client.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.merritt.samples.testing.services.cc.client.CcResult;
import com.merritt.samples.testing.services.cc.client.CcService;
import com.merritt.samples.testing.services.cc.client.it.config.CcWsClientSpringConfig;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { CcWsClientSpringConfig.class },
        loader = AnnotationConfigContextLoader.class )
@ActiveProfiles( { "production" } )
public class CcServiceIT
{
    private static final String CC_NUMBER_LUHN_INVALID = "1111111111111111";
    private static final String CC_NUMBER_LUHN_VALID = "4417123456789113";
    
    @Inject
    private transient CcService ccService;
    
    @Test
    public void testProcessCard()
    {
        final CcResult result = ccService.processCard( CC_NUMBER_LUHN_VALID, 25.25d );
        assertNotNull( "The result should not be null.", result );
        assertEquals( "The result should have been success.", CcResult.SUCCESS, result );
    }
    
    @Test( expected = SOAPFaultException.class )
    public void testProcessCard_InvalidCcLuhn()
    {
        ccService.processCard( CC_NUMBER_LUHN_INVALID, 25.25d );
    }
}
