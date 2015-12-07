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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.merritt.commons.testing.dbunit.utils.DatabaseTestingUtils;
import com.merritt.commons.testing.dbunit.utils.DatabaseTestingUtils.DatabaseType;
import com.merritt.samples.testing.bean.Order;
import com.merritt.samples.testing.dao.api.OrdersDao;
import com.merritt.samples.testing.dao.config.GeneralJpaSpringConfig;
import com.merritt.samples.testing.dao.config.JpaHibernateVendorSpringConfig;
import com.merritt.samples.testing.dao.config.JpaVendorDdlSpringConfig;
import com.merritt.samples.testing.exceptions.ItemNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GeneralJpaSpringConfig.class, JpaHibernateVendorSpringConfig.class, JpaVendorDdlSpringConfig.class}, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("testing")
public class OrdersJpaDaoTest {
    private static final String JNDI_DATASOURCE_LOOKUP_NAME = GeneralJpaSpringConfig.JNDI_DATASOURCE_LOOKUP_NAME;
    private static IDatabaseTester databaseTester;

    private static final long TEST_ORDER_ID_NOT_FOUND = 999l;

    private static final long TEST_ORDER_ID_1 = 1l;
    private static final long TEST_CUSTOMER_ID_1 = 1l;
    private static final double TEST_TOTAL_1 = 5.00d;
    private static final String TEST_RESULT_1 = "Success";

    /**
     * need to be set to greatest id in orders_data.xml + 1
     */
    private static final long TEST_ORDER_ID_NEW = 4l;
    private static final long TEST_CUSTOMER_ID_NEW = 10l;
    private static final double TEST_TOTAL_NEW = 25.00d;
    private static final String TEST_RESULT_NEW = "Successful";

    @Inject
    private OrdersDao ordersJpaDao;

    @BeforeClass
    public static void setUpBeforeClass() throws DataSetException, IOException {
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
        ordersJpaDao.selectById(TEST_ORDER_ID_NOT_FOUND);
    }

    @Test
    public void testSelectByIdFound() throws NamingException, SQLException, ItemNotFoundException {
        final Order order = ordersJpaDao.selectById(TEST_ORDER_ID_1);
        assertEquals("Order ids should match;", TEST_ORDER_ID_1, order.getOrderId());
        assertEquals("Customer ids should match;", TEST_CUSTOMER_ID_1, order.getCustomerId());
        assertEquals("Totals should match;", TEST_TOTAL_1, order.getTotal(), 0);
        assertEquals("Results should match;", TEST_RESULT_1, order.getResult());
    }

    @Test
    public void testInsert() throws NamingException, SQLException, ItemNotFoundException {
        final long orderId = ordersJpaDao.insert(TEST_CUSTOMER_ID_NEW, TEST_TOTAL_NEW, TEST_RESULT_NEW);
        assertEquals("Order ids should match;", TEST_ORDER_ID_NEW, orderId);

        final Order order = ordersJpaDao.selectById(TEST_ORDER_ID_NEW);
        assertEquals("Order ids should match;", TEST_ORDER_ID_NEW, order.getOrderId());
        assertEquals("Customer ids should match;", TEST_CUSTOMER_ID_NEW, order.getCustomerId());
        assertEquals("Totals should match;", TEST_TOTAL_NEW, order.getTotal(), 0);
        assertEquals("Results should match;", TEST_RESULT_NEW, order.getResult());
    }
}
