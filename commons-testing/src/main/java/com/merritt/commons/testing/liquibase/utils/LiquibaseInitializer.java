package com.merritt.commons.testing.liquibase.utils;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;

public class LiquibaseInitializer
{
    private String changelog;
    private String dataSource;
    
    public void setChangelog( final String changelog )
    {
        this.changelog = changelog;
    }
    
    public void setDataSource( final String dataSource )
    {
        this.dataSource = dataSource;
    }
    
    @PostConstruct
    public void DropCreateDatabase() throws Exception
    {
        final DatabaseConnection dc = new JdbcConnection( getJndiDataSource( dataSource ).getConnection() );
        final Liquibase liquibase = new Liquibase( changelog, new FileSystemResourceAccessor(), dc );
        
        liquibase.dropAll();
        liquibase.update( "" );
        
    }
    
    private DataSource getJndiDataSource( final String dataSourceName ) throws NamingException
    {
        DataSource dataSource;
        final Context ctx = new javax.naming.InitialContext();
        dataSource = (javax.sql.DataSource)ctx.lookup( "java:" + dataSourceName );
        return dataSource;
    }
}
