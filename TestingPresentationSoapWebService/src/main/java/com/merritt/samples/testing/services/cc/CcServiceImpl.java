package com.merritt.samples.testing.services.cc;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.util.Assert;

import com.merritt.samples.testing.services.cc.api.CcService;
import com.merritt.samples.testing.services.cc.utils.LuhnCheck;

@WebService(endpointInterface = "com.merritt.samples.testing.services.cc.api.CcService", serviceName = "CcService")
public class CcServiceImpl implements CcService
{
    @WebMethod
    public CcResult processCard( final String cardNumber, final double total ) throws IllegalArgumentException
    {
        Assert.notNull( cardNumber, "You must provide a Credit Card Number." );
        final int creditCardLength = cardNumber.length();
        Assert.isTrue( creditCardLength >= 14 && creditCardLength <= 16, "Credit Card Number must be 14 - 16 digits." );
        Assert.isTrue( LuhnCheck.isValid( cardNumber ), "You must provide a valid Credit Card Number." );
        
        CcResult ccResult = CcResult.SUCCESS;
        if( total > MAX_LIMIT )
        {
            ccResult = CcResult.LIMIT_EXCEEDED;
        }
        return ccResult;
    }
    
}
