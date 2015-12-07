package com.merritt.commons.testing.jndi.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Test;

public class MockJndiTest
{
    private static final String DATABASE_JNDI_NAME = "java:comp/env/jdbc/hsqlDataSource";
    private static final String TEST_PROPERTY_JNDI_NAME = "java:comp/env/test";
    Connection c;
    
    @After
    public void closeConnectionIfStillOpen() throws Exception
    {
        if( c != null && !c.isClosed() )
        {
            c.close();
        }
    }
    
    @Test
    public void testLookupDatasource() throws Exception
    {
        final InitialContext ctx = new InitialContext();
        assertNotNull( ctx );
        
        final DataSource dataSource = (DataSource)ctx.lookup( DATABASE_JNDI_NAME );
        assertNotNull( dataSource );
    }
    
    @Test( expected = SQLException.class )
    public void testMaxActive() throws Exception
    {
        final InitialContext ctx = new InitialContext();
        assertNotNull( ctx );
        
        final DataSource dataSource = (DataSource)ctx.lookup( DATABASE_JNDI_NAME );
        assertNotNull( dataSource );
        
        c = dataSource.getConnection();
        assertNotNull( c );
        //this should throw an exception
        dataSource.getConnection();
    }
    
    @Test( expected = SQLException.class )
    public void testMaxActiveWithCloseInBetween() throws Exception
    {
        final InitialContext ctx = new InitialContext();
        assertNotNull( ctx );
        
        final DataSource dataSource = (DataSource)ctx.lookup( DATABASE_JNDI_NAME );
        assertNotNull( dataSource );
        
        c = dataSource.getConnection();
        assertNotNull( c );
        c.close();
        c = dataSource.getConnection();
        assertNotNull( c );
        //this should throw an exception
        dataSource.getConnection();
    }
    
    @Test
    public void testMockJndiProperty() throws Exception
    {
        final InitialContext ctx = new InitialContext();
        assertNotNull( ctx );
        
        final String testString = (String)ctx.lookup( TEST_PROPERTY_JNDI_NAME );
        assertNotNull( testString );
        assertEquals( "testValue", testString );
    }
    
}
