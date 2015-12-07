package com.merritt.samples.testing.business;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.merritt.samples.testing.bean.Order;
import com.merritt.samples.testing.business.api.PaymentProcessor;
import com.merritt.samples.testing.business.config.GeneralBusinessSpringConfig;
import com.merritt.samples.testing.business.config.MockTestBusinessSpringConfig;
import com.merritt.samples.testing.dao.api.OrdersDao;
import com.merritt.samples.testing.exceptions.ItemNotFoundException;
import com.merritt.samples.testing.services.cc.client.CcResult;
import com.merritt.samples.testing.services.cc.client.CcService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GeneralBusinessSpringConfig.class, MockTestBusinessSpringConfig.class}, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles({"mock"})
public class PaymentProcessorTest {
    private static final String CC_NUMBER_LUHN_VALID = "4417123456789113";

    @Inject
    Order testOrder1;
    @Inject
    Order testOrder2;

    @Inject
    CcService mockCcService;

    @Inject
    OrdersDao mockOrdersDao;

    @Inject
    private PaymentProcessor paymentProcessor;

    @Test
    public void testProcessPayment_Success() throws Exception {
        when(mockOrdersDao.selectById(1)).thenReturn(testOrder1);
        when(mockCcService.processCard(CC_NUMBER_LUHN_VALID, testOrder1.getTotal())).thenReturn(CcResult.SUCCESS);
        final CcResult ccResult = paymentProcessor.processPayment(1, CC_NUMBER_LUHN_VALID);
        assertEquals(CcResult.SUCCESS, ccResult);
        verify(mockOrdersDao, times(1)).selectById(1);
        verify(mockCcService, times(1)).processCard(CC_NUMBER_LUHN_VALID, testOrder1.getTotal());
    }

    @Test(expected = ItemNotFoundException.class)
    public void testProcessPayment_ItemNotFoundException() throws Exception {
        when(mockOrdersDao.selectById(2)).thenThrow(new ItemNotFoundException("This is my test message."));
        paymentProcessor.processPayment(2, CC_NUMBER_LUHN_VALID);
    }
}
