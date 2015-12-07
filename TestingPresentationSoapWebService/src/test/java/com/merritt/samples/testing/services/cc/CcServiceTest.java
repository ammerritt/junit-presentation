package com.merritt.samples.testing.services.cc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.merritt.samples.testing.services.cc.api.CcService;
import com.merritt.samples.testing.services.cc.api.CcService.CcResult;
import com.merritt.samples.testing.services.cc.config.GeneralCcServiceSpringConfig;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { GeneralCcServiceSpringConfig.class },
        loader = AnnotationConfigContextLoader.class )
public class CcServiceTest
{
    private static final double TOTAL_WITHIN_LIMITS = 35.00d;
    private static final double TOTAL_EXCEEDING_LIMIT = CcService.MAX_LIMIT + 0.01d;
    private static final String CC_NUMBER_TOO_SHORT = "1111111111111";
    private static final String CC_NUMBER_TOO_LONG = "11111111111111111";
    private static final String CC_NUMBER_LUHN_INVALID = "1111111111111111";
    private static final String CC_NUMBER_LUHN_VALID = "4417123456789113";
    
    @Inject
    private transient CcService ccService;
    
    @Test
    public void testProcessCard()
    {
        final CcResult result = ccService.processCard( CC_NUMBER_LUHN_VALID, TOTAL_WITHIN_LIMITS );
        assertNotNull( "The result should not be null.", result );
        assertEquals( "The result should have been success.", CcResult.SUCCESS, result );
    }
    
    @Test
    public void testProcessCard_ExceededLimit()
    {
        final CcResult result = ccService.processCard( CC_NUMBER_LUHN_VALID, TOTAL_EXCEEDING_LIMIT );
        assertNotNull( "The result should not be null.", result );
        assertEquals( "The result should have been limit exceeded.", CcResult.LIMIT_EXCEEDED, result );
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void testProcessCard_CcNumberTooShort()
    {
        ccService.processCard( CC_NUMBER_TOO_SHORT, TOTAL_WITHIN_LIMITS );
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void testProcessCard_CcNumberTooLong()
    {
        ccService.processCard( CC_NUMBER_TOO_LONG, TOTAL_WITHIN_LIMITS );
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void testProcessCard_NullCcNumber()
    {
        ccService.processCard( null, TOTAL_WITHIN_LIMITS );
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void testProcessCard_InvalidLuhnNumber()
    {
        ccService.processCard( CC_NUMBER_LUHN_INVALID, TOTAL_WITHIN_LIMITS );
    }
}
