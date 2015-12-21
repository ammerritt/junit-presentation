package com.merritt.commons.testing.dbunit.databasetesters;

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
 * Needed to be able to set the DatabaseConfig.PROPERTY_DATATYPE_FACTORY property in the getConnection method so this is a copy of the dbunit DataSourceDatabaseTester
 * with that addition.  I would have just extended that but they made everything private...
 * </pre>
 * 
 * @see org.dbunit.DataSourceDatabaseTester
 */
public class LocalDataSourceDatabaseTester extends AbstractDatabaseTester {
    private static final Logger logger = LoggerFactory.getLogger(LocalDataSourceDatabaseTester.class);

    private final DataSource dataSource;
    private final DefaultDataTypeFactory defaultDataTypeFactory;

    /**
     * Creates a new DataSourceDatabaseTester with the specified DataSource.
     *
     * @param dataSource the DataSource to pull connections from
     */
    public LocalDataSourceDatabaseTester(final DataSource dataSource) {
        this(dataSource, null, new DefaultDataTypeFactory());
    }

    /**
     * Creates a new DataSourceDatabaseTester with the specified DataSource and schema name.
     * @param dataSource the DataSource to pull connections from
     * @param schema The schema name to be used for new dbunit connections
     * @param defaultDataTypeFactory
     * @since 2.4.5
     */
    public LocalDataSourceDatabaseTester(final DataSource dataSource, final String schema, final DefaultDataTypeFactory defaultDataTypeFactory) {
        super(schema);

        if (dataSource == null) {
            throw new NullPointerException("The parameter 'dataSource' must not be null");
        }
        this.dataSource = dataSource;
        this.defaultDataTypeFactory = defaultDataTypeFactory;
    }

    public IDatabaseConnection getConnection() throws Exception {
        logger.debug("getConnection() - start");

        assertTrue("DataSource is not set", dataSource != null);
        final DatabaseConnection databaseConnection = new DatabaseConnection(dataSource.getConnection(), getSchema());
        databaseConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, defaultDataTypeFactory);
        return databaseConnection;
    }
}