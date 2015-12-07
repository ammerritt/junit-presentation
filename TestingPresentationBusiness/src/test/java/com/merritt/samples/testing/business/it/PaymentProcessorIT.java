package com.merritt.samples.testing.business.it;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.inject.Inject;

import org.dbunit.IDatabaseTester;
import org.dbunit.JndiDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.merritt.samples.testing.business.api.PaymentProcessor;
import com.merritt.samples.testing.business.config.GeneralBusinessSpringConfig;
import com.merritt.samples.testing.business.config.JpaHibernateVendorDdlSpringConfig;
import com.merritt.samples.testing.dao.config.GeneralJpaSpringConfig;
import com.merritt.samples.testing.services.cc.client.CcResult;

@RunWith( SpringJUnit4ClassRunner.class )
//NOTE: by this point you should really have a physical database configured in your jndi.xml at which case you wouldn't need JpaHibernateVendorDdlSpringConfig.class
@ContextConfiguration( classes = { GeneralBusinessSpringConfig.class,
        JpaHibernateVendorDdlSpringConfig.class },
        loader = AnnotationConfigContextLoader.class )
@ActiveProfiles( { "production", "ddl" } )
public class PaymentProcessorIT
{
    private static final String CC_NUMBER_LUHN_VALID = "4417123456789113";
    
    private static final String SCHEMA_NULL = null;
    private static IDatabaseTester databaseTester;
    
    @Inject
    private PaymentProcessor pp1;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        //NOTE: since you also have a physical database you may not want to load data in the database, but, you may as well
        final Properties environment = new Properties();
        databaseTester = new JndiDatabaseTester( environment, GeneralJpaSpringConfig.JNDI_DATASOURCE_LOOKUP_NAME, SCHEMA_NULL );
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build( PaymentProcessorIT.class.getResource( "/orders_data.xml" ) );
        databaseTester.setDataSet( dataSet );
        databaseTester.setSetUpOperation( DatabaseOperation.CLEAN_INSERT );
        databaseTester.setTearDownOperation( DatabaseOperation.DELETE_ALL );
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }
    
    @Before
    public void setUpBeforeTest() throws Exception
    {
        databaseTester.onSetup();
    }
    
    @After
    public void tearDownAfterTest() throws Exception
    {
        databaseTester.onTearDown();
    }
    
    @Test
    public void testProcessPayment() throws Exception
    {
        final CcResult ccResult = pp1.processPayment( 1, CC_NUMBER_LUHN_VALID );
        assertEquals( CcResult.SUCCESS, ccResult );
    }
}
