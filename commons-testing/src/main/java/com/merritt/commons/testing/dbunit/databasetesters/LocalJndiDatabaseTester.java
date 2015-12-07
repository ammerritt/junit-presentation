package com.merritt.commons.testing.dbunit.databasetesters;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Needed to be able to set the DatabaseConfig.PROPERTY_DATATYPE_FACTORY property in the getConnection method so this is a copy of the dbunit JndiDatabaseTester
 * with that addition.  I would have just extended that but they made everything private...
 * </pre>
 * 
 * @see org.dbunit.JndiDatabaseTester
 */
public class LocalJndiDatabaseTester extends AbstractDatabaseTester
{
    private static final Logger logger = LoggerFactory.getLogger( LocalJndiDatabaseTester.class );
    
    private DataSource dataSource;
    private final Properties environment;
    private boolean initialized = false;
    private final String lookupName;
    private final DefaultDataTypeFactory defaultDataTypeFactory;
    
    /**
     * Creates a JndiDatabaseTester with default JNDI properties.
     *
     * @param lookupName the name of the resource in the JNDI context
     */
    public LocalJndiDatabaseTester( final String lookupName )
    {
        this( null, lookupName );
    }
    
    /**
     * Creates a JndiDatabaseTester with specific JNDI properties.
     *
     * @param environment A Properties object with JNDI properties. Can be null
     * @param lookupName the name of the resource in the JNDI context
     */
    public LocalJndiDatabaseTester( final Properties environment, final String lookupName )
    {
        this( environment, lookupName, null, new DefaultDataTypeFactory() );
    }
    
    /**
     * Creates a JndiDatabaseTester with specific JNDI properties.
     * 
     * @param environment A Properties object with JNDI properties. Can be <code>null</code>
     * @param lookupName the name of the resource in the JNDI context
     * @param schema The schema name to be used for new dbunit connections. Can be <code>null</code>
     * @since 2.4.5
     */
    public LocalJndiDatabaseTester( final Properties environment, final String lookupName, final String schema, final DefaultDataTypeFactory defaultDataTypeFactory )
    {
        super( schema );
        
        if( lookupName == null )
        {
            throw new NullPointerException(
                    "The parameter 'lookupName' must not be null" );
        }
        this.lookupName = lookupName;
        this.environment = environment;
        this.defaultDataTypeFactory = defaultDataTypeFactory;
    }
    
    public IDatabaseConnection getConnection() throws Exception
    {
        logger.trace( "getConnection() - start" );
        
        if( !initialized )
        {
            initialize();
        }
        
        final DatabaseConnection databaseConnection = new DatabaseConnection( dataSource.getConnection(), getSchema() );
        databaseConnection.getConfig().setProperty( DatabaseConfig.PROPERTY_DATATYPE_FACTORY, defaultDataTypeFactory );
        return databaseConnection;
    }
    
    /**
     * Verifies the configured properties and locates the Datasource through
     * JNDI.<br>
     * This method is called by {@link getConnection} if the tester has not been
     * initialized yet.
     */
    private void initialize() throws NamingException
    {
        logger.trace( "initialize() - start" );
        
        final Context context = new InitialContext( environment );
        assertNotNullNorEmpty( "lookupName", lookupName );
        final Object obj = context.lookup( lookupName );
        assertTrue( "JNDI object with [" + lookupName + "] not found", obj != null );
        assertTrue( "Object [" + obj + "] at JNDI location [" + lookupName
                + "] is not of type [" + DataSource.class.getName() + "]", obj instanceof DataSource );
        dataSource = (DataSource)obj;
        assertTrue( "DataSource is not set", dataSource != null );
        initialized = true;
    }
    
    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( getClass().getName() ).append( "[" );
        sb.append( "lookupName=" ).append( lookupName );
        sb.append( ", environment=" ).append( environment );
        sb.append( ", initialized=" ).append( initialized );
        sb.append( ", dataSource=" ).append( dataSource );
        sb.append( ", schema=" ).append( super.getSchema() );
        sb.append( "]" );
        return sb.toString();
    }
}
