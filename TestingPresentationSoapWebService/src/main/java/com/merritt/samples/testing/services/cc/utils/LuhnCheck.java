package com.merritt.samples.testing.services.cc.utils;

import org.springframework.util.Assert;

public final class LuhnCheck
{
    private static final int MAX_NUMBER = 9;
    
    private LuhnCheck()
    {
        //no need to instantiate this, only a static method.
    }
    
    public static boolean isValid( final String cardNumber )
    {
        Assert.notNull( cardNumber, "The Credit Card Number cannot be null." );
        final String digitsOnly = getDigitsOnly( cardNumber );
        int sum = 0;
        int digit = 0;
        int addend = 0;
        boolean timesTwo = false;
        
        for( int i = digitsOnly.length() - 1; i >= 0; i-- )
        {
            digit = Integer.parseInt( digitsOnly.substring( i, i + 1 ) );
            if( timesTwo )
            {
                addend = digit * 2;
                if( addend > MAX_NUMBER )
                {
                    addend -= MAX_NUMBER;
                }
            }
            else
            {
                addend = digit;
            }
            sum += addend;
            timesTwo ^= true;
        }
        
        final int modulus = sum % 10;
        return modulus == 0;
    }
    
    /**
     * This would remove any spaces or dashes placed in the CC number.
     * @param string
     * @return
     */
    private static String getDigitsOnly( final String string )
    {
        final StringBuffer digitsOnly = new StringBuffer();
        char currentCharacter;
        for( int i = 0; i < string.length(); i++ )
        {
            currentCharacter = string.charAt( i );
            if( Character.isDigit( currentCharacter ) )
            {
                digitsOnly.append( currentCharacter );
            }
        }
        return digitsOnly.toString();
    }
}
