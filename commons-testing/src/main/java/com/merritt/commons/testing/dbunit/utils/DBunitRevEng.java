/**
 * 
 */
package com.merritt.commons.testing.dbunit.utils;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * The sole purpose of this file is generate a dbunit dataset file for use in testing.  
 * This class should be called only once when you are generating data to use.
 * @author myoung2
 *
 */
public class DBunitRevEng
{
    
    /**
     * 
     */
    public DBunitRevEng()
    {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Takes a connection and selects the whole table and exports it to the filename 
     * provided.  This method is public and can be called from your own main if you so choose.
     * If you include a schema in your table name (ie. SCHEMA.table_name), you will have to 
     * manually edit the resulting file and remove it.
     * @param conn
     * @param tableName
     * @return
     */
    public static void writeFlatXmlDataSet( final Connection jdbcConnection, final String tableName, final String pathToOutFile ) throws Exception
    {
        
        final IDatabaseConnection connection = new DatabaseConnection( jdbcConnection );
        try
        {
            
            // partial database export
            final QueryDataSet partialDataSet = new QueryDataSet( connection );
            partialDataSet.addTable( tableName, "SELECT * FROM " + tableName );
            FlatXmlDataSet.write( partialDataSet, new FileOutputStream( pathToOutFile ) );
            connection.close();
        }
        catch( final Exception e )
        {
            System.out.println( "Unable to create Dataset" );
            
        }
    }
    
    /**
     * @param args 
     *      Arg0: tableName (including schema if desired)
     *      Arg1: Fully qullified path for output file 
     */
    public static void main( final String[] args )
    {
        if( args.length == 2 )
        {
            try
            {
                
                Class.forName( "oracle.jdbc.driver.OracleDriver" );
                final Connection jdbcConnection = DriverManager.getConnection( "jdbc:oracle:thin:@<url>:<port>:<db>", "<user>", "<password>" );
                writeFlatXmlDataSet( jdbcConnection, args[0], args[1] );
            }
            catch( final Exception e )
            {
                System.out.println( "Could not create JDBC Connection" );
            }
        }
        
    }
    
}
