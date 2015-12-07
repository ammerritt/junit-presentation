package com.merritt.samples.testing.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;

import com.merritt.samples.testing.dao.config.GeneralSpringConfig;

public class GenericJdbcDao
{
    public static final String JNDI_DATASOURCE_LOOKUP_NAME = GeneralSpringConfig.JNDI_DATASOURCE_LOOKUP_NAME;
    
    protected Connection getConnection() throws NamingException, SQLException
    {
        final InitialContext initialContext = new InitialContext();
        final DataSource dataSource = (DataSource)initialContext.lookup( JNDI_DATASOURCE_LOOKUP_NAME );
        return dataSource.getConnection();
    }
    
    protected void close( final Connection connection, final Statement statement, final ResultSet resultSet )
    {
        DbUtils.closeQuietly( connection, statement, resultSet );
    }
    
}