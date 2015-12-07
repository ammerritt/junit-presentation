package com.merritt.samples.testing.services.cc.api;

import javax.jws.WebService;

@WebService
public interface CcService
{
    double MAX_LIMIT = 100.00d;
    
    public enum CcResult
    {
        SUCCESS( "Card processed successfully." ),
        LIMIT_EXCEEDED( "Your total exceeded your limit." );
        
        private final String message;
        
        CcResult( final String message )
        {
            this.message = message;
        }
        
        public String getMessage()
        {
            return message;
        }
    }
    
    CcResult processCard( final String cardNumber, final double total ) throws IllegalArgumentException;
}