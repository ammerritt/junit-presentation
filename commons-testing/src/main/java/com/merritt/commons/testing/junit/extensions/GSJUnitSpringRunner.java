package com.merritt.commons.testing.junit.extensions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.sql.DataSource;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;

import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.merritt.commons.testing.dbunit.utils.DBUnitHelper;

public class GSJUnitSpringRunner extends Suite
{
    private static boolean databaseCreated = false;
    
    private class TestClassRunnerForParameters extends SpringJUnit4ClassRunner
    {
        private final int fParameterSetNumber;
        
        private final Map<String, String> fParameterList;
        
        private final String fMethodName;
        
        TestClassRunnerForParameters( final Class<?> type, final Map<String, String> parameterList, final int i, final String methodName ) throws InitializationError
        {
            super( type );
            fParameterList = parameterList;
            fParameterSetNumber = i;
            fMethodName = methodName;
        }
        
        @Override
        public Object createTest() throws Exception
        {
            if( DBUnitHelper.populateDatabase.get( getTestClass().getName() ) == null )
            {
                populateDatabase();
                DBUnitHelper.populateDatabase.put( getTestClass().getName(), true );
            }
            final Object testInstance = getTestClass().getOnlyConstructor().newInstance();
            getTestContextManager().prepareTestInstance( testInstance );
            return testInstance;
        }
        
        @Override
        protected List<FrameworkMethod> computeTestMethods()
        {
            
            final List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods( Test.class );
            for( final FrameworkMethod method : methods )
            {
                if( method.getName() == fMethodName )
                {
                    return new ArrayList<FrameworkMethod>( Arrays.asList( new FrameworkMethod[] { method } ) );
                }
            }
            return getTestClass().getAnnotatedMethods( Test.class );
        }
        
        @Override
        protected String getName()
        {
            return String.format( "%s", fMethodName );
        }
        
        @Override
        protected String testName( final FrameworkMethod method )
        {
            //return UUID.randomUUID().toString();
            return String.format( "%s[%s]", method.getName(), fParameterSetNumber );
        }
        
        @Override
        protected void validateConstructor( final List<Throwable> errors )
        {
            validateOnlyOneConstructor( errors );
        }
        
        @Override
        protected Statement classBlock( final RunNotifier notifier )
        {
            return childrenInvoker( notifier );
        }
        
        @Override
        protected Statement methodInvoker( final FrameworkMethod method, final Object test )
        {
            Map<String, String> params = null;
            try
            {
                params = fParameterList;
            }
            catch( final Exception e )
            {
                e.printStackTrace();
            }
            return new InvokeMethodWithParameters( method, test, params );
        }
        
        @Override
        protected void validateTestMethods( final List<Throwable> errors )
        {
            
        }
        
        private void populateDatabase()
        {
            final DBUnitInsertClean dbUnitInsertClean = AnnotationUtils.findAnnotation( getTestClass().getJavaClass(), DBUnitInsertClean.class );
            if( dbUnitInsertClean != null )
            {
                try
                {
                    final FlatXmlDataSetBuilder fxdsb = new FlatXmlDataSetBuilder();
                    fxdsb.setColumnSensing( true );
                    if( dbUnitInsertClean.xmlFiles().length > 0 )
                    {
                        for( final String table : dbUnitInsertClean.xmlFiles() )
                        {
                            DatabaseOperation.CLEAN_INSERT.execute( new DatabaseDataSourceConnection( getJndiDataSource( dbUnitInsertClean.dataSource() ) ), fxdsb.build( new FileInputStream( table ) ) );
                        }
                    }
                    if( !"".equals( dbUnitInsertClean.xmlDirectory() ) )
                    {
                        final String directory = dbUnitInsertClean.xmlDirectory();
                        final File dir = new File( directory );
                        final File[] children = dir.listFiles();
                        for( final File table : children )
                        {
                            if( table.isFile() )
                            {
                                DatabaseOperation.CLEAN_INSERT.execute( new DatabaseDataSourceConnection( getJndiDataSource( dbUnitInsertClean.dataSource() ) ), fxdsb.build( new FileInputStream( table ) ) );
                            }
                        }
                    }
                }
                catch( final Exception e )
                {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    private final ArrayList<Runner> runners = new ArrayList<Runner>();
    
    /**
     * Only called reflectively. Do not use programmatically.
     */
    public GSJUnitSpringRunner( final Class<?> klass ) throws Throwable
    {
        super( klass, Collections.<Runner>emptyList() );
        final List<FrameworkMethod> methods = getTestMethods( getTestClass() );
        createDatabase();
        for( final FrameworkMethod method : methods )
        {
            List<Map<String, String>> parametersList = getExcelSheets( method );
            int testCase = 0;
            for( final Map<String, String> parameters : parametersList )
            {
                runners.add( new TestClassRunnerForParameters( getTestClass().getJavaClass(), parameters, testCase++, method.getName() ) );
            }
            parametersList = new ArrayList<Map<String, String>>();
        }
    }
    
    private void createDatabase()
    {
        final CreateDatabase createDatabase = AnnotationUtils.findAnnotation( getTestClass().getJavaClass(), CreateDatabase.class );
        if( createDatabase != null )
        {
            try
            {
                if( !databaseCreated )
                {
                    final DatabaseConnection dc = new JdbcConnection( getJndiDataSource( createDatabase.dataSource() ).getConnection() );
                    final Liquibase liquibase = new Liquibase( createDatabase.databaseFile(), new FileSystemResourceAccessor(), dc );
                    liquibase.update( "" );
                    databaseCreated = true;
                }
            }
            catch( final Exception e )
            {
                e.printStackTrace();
            }
        }
    }
    
    private DataSource getJndiDataSource( final String dataSourceName )
    {
        DataSource dataSource = null;
        try
        {
            final Context ctx = new javax.naming.InitialContext();
            dataSource = (javax.sql.DataSource)ctx.lookup( "java:" + dataSourceName );
        }
        catch( final Exception e )
        {
            e.printStackTrace();
        }
        return dataSource;
    }
    
    @Override
    protected List<Runner> getChildren()
    {
        return runners;
    }
    
    private List<Map<String, String>> getExcelSheets( final FrameworkMethod method ) throws Exception
    {
        
        final ExcelDataSource excelDataSource = method.getAnnotation( ExcelDataSource.class );
        if( excelDataSource != null )
        {
            final InputStream spreadsheet = new FileInputStream( excelDataSource.path() );
            return new SpreadsheetData( spreadsheet, excelDataSource.sheetName() ).getData();
        }
        else
        {
            return new ArrayList<Map<String, String>>();
        }
        
    }
    
    private List<FrameworkMethod> getTestMethods( final TestClass testClass ) throws Exception
    {
        return testClass.getAnnotatedMethods( Test.class );
    }
}
