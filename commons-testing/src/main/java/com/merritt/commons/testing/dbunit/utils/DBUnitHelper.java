package com.merritt.commons.testing.dbunit.utils;

import java.util.HashMap;
import java.util.Map;

public class DBUnitHelper
{
    public static Map<String, Boolean> populateDatabase = new HashMap<String, Boolean>();
    
    static
    {
        populateDatabase.put( "hi", true );
    }
}
