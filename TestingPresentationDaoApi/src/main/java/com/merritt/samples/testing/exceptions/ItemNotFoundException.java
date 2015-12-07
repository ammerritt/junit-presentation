package com.merritt.samples.testing.exceptions;

public class ItemNotFoundException extends Exception
{
    private static final long serialVersionUID = 9025072124573103126L;
    
    public ItemNotFoundException( final String message )
    {
        super( message );
    }
}
