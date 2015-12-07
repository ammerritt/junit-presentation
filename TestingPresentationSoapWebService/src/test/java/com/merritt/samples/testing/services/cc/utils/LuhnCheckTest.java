package com.merritt.samples.testing.services.cc.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.merritt.samples.testing.services.cc.utils.LuhnCheck;

public class LuhnCheckTest
{
    private static final String CC_NUMBER_NULL = null;
    private static final String CC_NUMBER_VALID_WITH_DASHES = "4408-0412-3456-7893";
    private static final String CC_NUMBER_VALID_WITHOUT_SPACES = "4417123456789113";
    private static final String CC_NUMBER_INVALID_WITH_SPACES = "4408 0412 3456 7890";
    private static final String CC_NUMBER_INVALID_WITHOUT_SPACES = "4417123456789112";
    
    @Test
    public void testIsValid()
    {
        assertFalse( "Should have been false as it is invalid.", LuhnCheck.isValid( CC_NUMBER_INVALID_WITH_SPACES ) );
        
        assertTrue( "Should have been true as it is valid.", LuhnCheck.isValid( CC_NUMBER_VALID_WITH_DASHES ) );
        
        assertFalse( "Should have been false as it is invalid.", LuhnCheck.isValid( CC_NUMBER_INVALID_WITHOUT_SPACES ) );
        
        assertTrue( "Should have been true as it is valid.", LuhnCheck.isValid( CC_NUMBER_VALID_WITHOUT_SPACES ) );
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void testIsValid_NullCardNumber()
    {
        assertTrue( "Should have been true as it is valid.", LuhnCheck.isValid( CC_NUMBER_NULL ) );
    }
}
