package com.merritt.commons.testing.dbunit.utils;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.merritt.commons.testing.dbunit.databasetesters.LocalDataSourceDatabaseTester;
import com.merritt.commons.testing.dbunit.databasetesters.LocalJndiDatabaseTester;

/**
 * <pre>
 * This is a helper class for writing unit tests when interacting with a database.
 * 
 * If you need to use JNDI for testing purposes you can place a jndi.xml file in your src/test/resources folder and define datasources and properties like this:
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd"&gt;
 * &lt;beans&gt;
 *     &lt;bean id="jndi" class="org.apache.xbean.spring.jndi.DefaultContext"&gt;
 *         &lt;property name="entries"&gt;
 *             &lt;map&gt;
 *                 &lt;!-- You can put as many "entry" items in as you need. --&gt;
 *                 &lt;entry key="java:comp/env/jdbc/hsqlDataSource"&gt; 
 *                     &lt;bean class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close" singleton="false"&gt;
 *                         &lt;property name="driverClassName" value="org.hsqldb.jdbcDriver" /&gt;
 *                         &lt;property name="url" value="jdbc:hsqldb:mem:HSQL" /&gt;
 *                         &lt;property name="username" value="sa" /&gt;
 *                         &lt;property name="password" value="" /&gt;
 *                         &lt;property name="maxIdle" value="-1" /&gt;
 *                         &lt;property name="maxActive" value="1" /&gt;
 *                         &lt;property name="maxWait" value="100" /&gt;
 *                     &lt;/bean&gt;
 *                 &lt;/entry&gt;
 *                 &lt;entry key="java:comp/env/test" value="testValue" /&gt;
 *             &lt;/map&gt;
 *         &lt;/property&gt;
 *     &lt;/bean&gt;
 * &lt;/beans&gt;
 * </pre>
 */
public class DatabaseTestingUtils {
    public static enum DatabaseType {
        HSQLDB, ORACLE
    }

    private DatabaseTestingUtils() {
        //make private as all methods are static
    }

    /**
     * This will load the specified sqlxmlDataInputFileName from the classpath and execute the statement(s) against the provided datasource.
     * 
     * The statements in sqlxmlDataInputFileName should be delimited with a semicolon. If the statements are not delimited with a semicolon then there  
     * should be one statement per line. Statements are allowed to span lines only if they are delimited with a semicolon. 
     * 
     * @param datasource - The datasource to execute the SQL file against.
     * @param sqlxmlDataInputFileName - The name of a SQL file to be loaded from the classpath containing the SQL statement(s) you would like 
     *                      executed against the datasource. (Leading slash is NOT needed.)
     * @throws DataAccessException - Thrown if there is a problem executing a statement from the SQL script.
     */
    @SuppressWarnings("deprecation")
    //Once SimpleJdbcTestUtils updates executeSqlScript to work with the new JdbcTemplate that can be updated and this removed.
    public static void executeSqlScript(final DataSource datasource, final String sqlxmlDataInputFileName) throws DataAccessException {
        final ClassPathResource createTablesScript = new ClassPathResource(sqlxmlDataInputFileName);
        JdbcTestUtils.executeSqlScript(new JdbcTemplate(datasource), createTablesScript, false);
    }

    /**
     * This will load the datasource from JNDI using the specified jndiLookupName, load the specified sqlxmlDataInputFileName from the classpath, 
     * then execute the statement(s) against the that datasource.
     * 
     * The statements in sqlxmlDataInputFileName should be delimited with a semicolon. If the statements are not delimited with a semicolon then there  
     * should be one statement per line. Statements are allowed to span lines only if they are delimited with a semicolon. 
     * 
     * @param jndiLookupName - The JNDI lookup name for the datasource to be used to execute the SQL file against.
     * @param sqlxmlDataInputFileName - The name of a SQL file to be loaded from the classpath containing the SQL statement(s) you would like 
     *                      executed against the datasource. (Leading slash is NOT needed.)
     * @throws DataAccessException - Thrown if there is a problem executing a statement from the SQL script.
     * @throws NamingException - Thrown if it cannot find the datasource with the specified jndiLookupName.
     */
    @SuppressWarnings("deprecation")
    //Once SimpleJdbcTestUtils updates executeSqlScript to work with the new JdbcTemplate that can be updated and this removed.
    public static void executeSqlScript(final String jndiLookupName, final String sqlxmlDataInputFileName) throws DataAccessException, NamingException {
        final InitialContext ctx = new InitialContext();
        JdbcTestUtils.executeSqlScript(new JdbcTemplate((DataSource)ctx.lookup(jndiLookupName)), new ClassPathResource(sqlxmlDataInputFileName), false);
    }

    /**
     * Generates an IDatabaseTester for the provided datasource that will run a CLEAN_INSERT, which first does a DELETE_ALL (view DeleteAllOperation under "See Also:" below) 
     * then does an INSERT (view InsertOperation under "See Also:" below), with the data in xmlDataInputFileName, when onSetup() is called and a DELETE_ALL when onTearDown() 
     * is called.  It will also set the appropriate dataTypeFactory for the specified DatabaseType.
     * 
     * @see org.dbunit.operation.DeleteAllOperation
     * @see org.dbunit.operation.InsertOperation
     * 
     * @param datasource - The datasource the data specified in xmlDataInputFileName will be loaded to.
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getDataSourceCleanInsertDeleteAllDatabaseTester(final DataSource datasource, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        return getDataSourceDatabaseTester(datasource, null, DatabaseOperation.CLEAN_INSERT, DatabaseOperation.DELETE_ALL, xmlDataInputFileName, databaseType);
    }

    /**
     * Generates an IDatabaseTester for the provided datasource, using the specified schema, that will run a CLEAN_INSERT, which first does a DELETE_ALL (view DeleteAllOperation under "See Also:" below) 
     * then does an INSERT (view InsertOperation under "See Also:" below), with the data in xmlDataInputFileName, when onSetup() is called and a DELETE_ALL when onTearDown() 
     * is called.  It will also set the appropriate dataTypeFactory for the specified DatabaseType.
     * 
     * @see org.dbunit.operation.DeleteAllOperation
     * @see org.dbunit.operation.InsertOperation
     * 
     * @param datasource - The datasource the data specified in xmlDataInputFileName will be loaded to.
     * @param schema - The schema name to be used for new DBUnit connections. Can be null
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getDataSourceCleanInsertDeleteAllDatabaseTester(final DataSource datasource, final String schema, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        return getDataSourceDatabaseTester(datasource, schema, DatabaseOperation.CLEAN_INSERT, DatabaseOperation.DELETE_ALL, xmlDataInputFileName, databaseType);
    }

    /**
     * Generates an IDatabaseTester for the provided datasource that will run a REFRESH (view RefreshOperation under "See Also:" below), with the data in xmlDataInputFileName, 
     * when onSetup() is called and a DELETE (view DeleteOperation under "See Also:" below) when onTearDown() is called.  It will also set the appropriate dataTypeFactory for
     * the specified DatabaseType.
     * 
     * @see org.dbunit.operation.RefreshOperation
     * @see org.dbunit.operation.DeleteOperation
     * 
     * @param datasource - The datasource the data specified in xmlDataInputFileName will be loaded to.
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getDataSourceRefreshDeleteDatabaseTester(final DataSource datasource, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        return getDataSourceDatabaseTester(datasource, null, DatabaseOperation.REFRESH, DatabaseOperation.DELETE, xmlDataInputFileName, databaseType);
    }

    /**
     * Generates an IDatabaseTester for the provided datasource, using the specified schema, that will run a REFRESH (view RefreshOperation under "See Also:" below), with the 
     * data in xmlDataInputFileName, when onSetup() is called and a DELETE (view DeleteOperation under "See Also:" below) when onTearDown() is called.  It will also set the 
     * appropriate dataTypeFactory for the specified DatabaseType.
     * 
     * @see org.dbunit.operation.RefreshOperation
     * @see org.dbunit.operation.DeleteOperation
     * 
     * @param datasource - The datasource the data specified in xmlDataInputFileName will be loaded to.
     * @param schema - The schema name to be used for new DBUnit connections. Can be null
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getDataSourceRefreshDeleteDatabaseTester(final DataSource datasource, final String schema, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        return getDataSourceDatabaseTester(datasource, schema, DatabaseOperation.REFRESH, DatabaseOperation.DELETE, xmlDataInputFileName, databaseType);
    }

    /**
     * Generates an IDatabaseTester for the provided datasource, using the specified schema, that will run the specified setupOperation, with the 
     * data in xmlDataInputFileName, when onSetup() is called and the specified tearDownOperation when onTearDown() is called.  It will also set the 
     * appropriate dataTypeFactory for the specified DatabaseType.
     * 
     * @param datasource - The datasource the data specified in xmlDataInputFileName will be loaded to.
     * @param schema - The schema name to be used for new DBUnit connections. Can be null
     * @param setupOperation - The operation to be used when setting up the data. (view DatabaseOperation under "See Also:" below)
     * @param teardownOperation - The operation to be used when cleaning up the data. (view DatabaseOperation under "See Also:" below)
     * @see org.dbunit.operation.DatabaseOperation
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getDataSourceDatabaseTester(final DataSource datasource, final String schema, final DatabaseOperation setupOperation, final DatabaseOperation teardownOperation, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        final IDatabaseTester tester = new LocalDataSourceDatabaseTester(datasource, schema, getDataTypeFactory(databaseType));
        configureTester(setupOperation, teardownOperation, xmlDataInputFileName, tester, databaseType);
        return tester;
    }

    /**
     * Generates an IDatabaseTester for the datasource looked up from JNDI that will run a CLEAN_INSERT, which first does a DELETE_ALL (view DeleteAllOperation under "See Also:" below)  
     * then does an INSERT (view InsertOperation under "See Also:" below), with the data in xmlDataInputFileName, when onSetup() is called and a DELETE_ALL when onTearDown() 
     * is called.  It will also set the appropriate dataTypeFactory for the specified DatabaseType.
     * 
     * @see org.dbunit.operation.DeleteAllOperation
     * @see org.dbunit.operation.InsertOperation
     * 
     * @param jndiLookupName - The JNDI lookup name for the datasource to be used to load the data into.
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getJndiCleanInsertDeleteAllDatabaseTester(final String jndiLookupName, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        return getJndiDatabaseTester(jndiLookupName, null, DatabaseOperation.CLEAN_INSERT, DatabaseOperation.DELETE_ALL, xmlDataInputFileName, databaseType);
    }

    /**
     * Generates an IDatabaseTester for the datasource looked up from JNDI, using the specified schema, that will run a CLEAN_INSERT, which first does a DELETE_ALL (view DeleteAllOperation under "See Also:" below) 
     * then does an INSERT (view InsertOperation under "See Also:" below), with the data in xmlDataInputFileName, when onSetup() is called and a DELETE_ALL when onTearDown() 
     * is called.  It will also set the appropriate dataTypeFactory for the specified DatabaseType.
     * 
     * @see org.dbunit.operation.DeleteAllOperation
     * @see org.dbunit.operation.InsertOperation
     * 
     * @param jndiLookupName - The JNDI lookup name for the datasource to be used to load the data into.
     * @param schema - The schema name to be used for new DBUnit connections. Can be null
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getJndiCleanInsertDeleteAllDatabaseTester(final String jndiLookupName, final String schema, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        return getJndiDatabaseTester(jndiLookupName, schema, DatabaseOperation.CLEAN_INSERT, DatabaseOperation.DELETE_ALL, xmlDataInputFileName, databaseType);
    }

    /**
     * Generates an IDatabaseTester for the datasource looked up from JNDI that will run a REFRESH (view RefreshOperation under "See Also:" below), with the data in xmlDataInputFileName, 
     * when onSetup() is called and a DELETE (view DeleteOperation under "See Also:" below) when onTearDown() is called.  It will also set the appropriate dataTypeFactory for
     * the specified DatabaseType.
     * 
     * @see org.dbunit.operation.RefreshOperation
     * @see org.dbunit.operation.DeleteOperation
     * 
     * @param jndiLookupName - The JNDI lookup name for the datasource to be used to load the data into.
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getJndiRefreshDeleteDatabaseTester(final String jndiLookupName, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        return getJndiDatabaseTester(jndiLookupName, null, DatabaseOperation.REFRESH, DatabaseOperation.DELETE, xmlDataInputFileName, databaseType);
    }

    /**
     * Generates an IDatabaseTester for the datasource looked up from JNDI, using the specified schema, that will run a REFRESH (view RefreshOperation under "See Also:" below), 
     * with the data in xmlDataInputFileName, when onSetup() is called and a DELETE (view DeleteOperation under "See Also:" below) when onTearDown() is called.  It will also set the 
     * appropriate dataTypeFactory for the specified DatabaseType.
     * 
     * @see org.dbunit.operation.RefreshOperation
     * @see org.dbunit.operation.DeleteOperation
     * 
     * @param jndiLookupName - The JNDI lookup name for the datasource to be used to load the data into.
     * @param schema - The schema name to be used for new DBUnit connections. Can be null
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getJndiRefreshDeleteDatabaseTester(final String jndiLookupName, final String schema, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        return getJndiDatabaseTester(jndiLookupName, schema, DatabaseOperation.REFRESH, DatabaseOperation.DELETE, xmlDataInputFileName, databaseType);
    }

    /**
     * Generates an IDatabaseTester for the datasource looked up from JNDI, using the specified schema, that will run the specified setupOperation, with the 
     * data in xmlDataInputFileName, when onSetup() is called and the specified tearDownOperation when onTearDown() is called.  It will also set the 
     * appropriate dataTypeFactory for the specified DatabaseType.
     * 
     * @param jndiLookupName - The JNDI lookup name for the datasource to be used to load the data into.
     * @param schema - The schema name to be used for new DBUnit connections. Can be null
     * @param setupOperation - The operation to be used when setting up the data. (view DatabaseOperation under "See Also:" below)
     * @param teardownOperation - The operation to be used when cleaning up the data. (view DatabaseOperation under "See Also:" below)
     * @see org.dbunit.operation.DatabaseOperation
     * @param xmlDataInputFileName - DBUnit XML file containing the data to be populated into the datasource.
     * @param databaseType - The type of database your datasource is going against.
     * @return IDatabaseTester that is set up and ready to go.  All that is needed is for onSetup() and onTearDown() to be invoked 
     *                         when you want the data loaded or removed.
     * @throws IOException - Thrown if the xmlDataInputFileName cannot be found or opened.
     * @throws DataSetException - Thrown if there is a problem inserting data from the xmlDataInputFileName.
     */
    public static IDatabaseTester getJndiDatabaseTester(final String jndiLookupName, final String schema, final DatabaseOperation setupOperation, final DatabaseOperation teardownOperation, final String xmlDataInputFileName, final DatabaseType databaseType) throws DataSetException, IOException {
        final IDatabaseTester tester = new LocalJndiDatabaseTester(null, jndiLookupName, schema, getDataTypeFactory(databaseType));
        configureTester(setupOperation, teardownOperation, xmlDataInputFileName, tester, databaseType);
        return tester;
    }

    private static void configureTester(final DatabaseOperation setupOperation, final DatabaseOperation teardownOperation, final String xmlDataInputFileName, final IDatabaseTester tester, final DatabaseType databaseType) throws DataSetException, IOException {
        final FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
        flatXmlDataSetBuilder.setColumnSensing(true);
        tester.setDataSet(flatXmlDataSetBuilder.build(new ClassPathResource(xmlDataInputFileName).getInputStream()));
        tester.setSetUpOperation(setupOperation);
        tester.setTearDownOperation(teardownOperation);
    }

    private static DefaultDataTypeFactory getDataTypeFactory(final DatabaseType databaseType) {
        DefaultDataTypeFactory defaultDataTypeFactory;

        switch (databaseType) {
            case ORACLE:
                defaultDataTypeFactory = new Oracle10DataTypeFactory();
                break;
            default:
                defaultDataTypeFactory = new HsqldbDataTypeFactory();
                break;
        }

        return defaultDataTypeFactory;
    }
}
