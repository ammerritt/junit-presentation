package com.merritt.samples.testing.dao.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.naming.NamingException;

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.merritt.commons.testing.dbunit.utils.DatabaseTestingUtils;
import com.merritt.commons.testing.dbunit.utils.DatabaseTestingUtils.DatabaseType;
import com.merritt.samples.testing.bean.Order;
import com.merritt.samples.testing.dao.api.OrdersDao;
import com.merritt.samples.testing.dao.config.GeneralSpringConfig;
import com.merritt.samples.testing.exceptions.ItemNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GeneralSpringConfig.class}, loader = AnnotationConfigContextLoader.class)
public class OrdersJdbcDaoTest {
    private static final String JNDI_DATASOURCE_LOOKUP_NAME = GeneralSpringConfig.JNDI_DATASOURCE_LOOKUP_NAME;
    private static IDatabaseTester databaseTester;
    private static final long TEST_ORDER_ID_NOT_FOUND = 999l;

    private static final long TEST_ORDER_ID_1 = 1l;
    private static final long TEST_CUSTOMER_ID_1 = 1l;
    private static final double TEST_TOTAL_1 = 5.00d;
    private static final String TEST_RESULT_1 = "Success";

    /**
     * create_tables.sql defines the start with for the sequence as 1000
     */
    private static final long TEST_ORDER_ID_NEW = 1000l;
    private static final long TEST_CUSTOMER_ID_NEW = 10l;
    private static final double TEST_TOTAL_NEW = 25.00d;
    private static final String TEST_RESULT_NEW = "Successful";

    @Inject
    private OrdersDao ordersDao;

    @BeforeClass
    public static void setUpBeforeClass() throws DataAccessException, NamingException, DataSetException, IOException {
        DatabaseTestingUtils.executeSqlScript(JNDI_DATASOURCE_LOOKUP_NAME, "create_tables.sql");
        databaseTester = DatabaseTestingUtils.getJndiCleanInsertDeleteAllDatabaseTester(JNDI_DATASOURCE_LOOKUP_NAME, "orders_data.xml", DatabaseType.HSQLDB);
    }

    @Before
    public void setUpBeforeTest() throws Exception {
        databaseTester.onSetup();
    }

    @After
    public void tearDownAfterTest() throws Exception {
        databaseTester.onTearDown();
    }

    @Test(expected = ItemNotFoundException.class)
    public void testSelectByIdNotFound() throws Exception {
        ordersDao.selectById(TEST_ORDER_ID_NOT_FOUND);
    }

    @Test
    public void testSelectByIdFound() throws NamingException, SQLException, ItemNotFoundException {
        final Order order = ordersDao.selectById(TEST_ORDER_ID_1);
        assertEquals("Order ids should match;", TEST_ORDER_ID_1, order.getOrderId());
        assertEquals("Customer ids should match;", TEST_CUSTOMER_ID_1, order.getCustomerId());
        assertEquals("Totals should match;", TEST_TOTAL_1, order.getTotal(), 0);
        assertEquals("Results should match;", TEST_RESULT_1, order.getResult());
    }

    @Test
    public void testInsert() throws NamingException, SQLException, ItemNotFoundException {
        final long orderId = ordersDao.insert(TEST_CUSTOMER_ID_NEW, TEST_TOTAL_NEW, TEST_RESULT_NEW);
        assertEquals("Order ids should match;", TEST_ORDER_ID_NEW, orderId);

        final Order order = ordersDao.selectById(TEST_ORDER_ID_NEW);
        assertEquals("Order ids should match;", TEST_ORDER_ID_NEW, order.getOrderId());
        assertEquals("Customer ids should match;", TEST_CUSTOMER_ID_NEW, order.getCustomerId());
        assertEquals("Totals should match;", TEST_TOTAL_NEW, order.getTotal(), 0);
        assertEquals("Results should match;", TEST_RESULT_NEW, order.getResult());
    }
}
