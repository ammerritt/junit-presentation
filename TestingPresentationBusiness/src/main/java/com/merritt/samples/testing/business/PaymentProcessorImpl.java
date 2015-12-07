package com.merritt.samples.testing.business;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.naming.NamingException;

import com.merritt.samples.testing.bean.Order;
import com.merritt.samples.testing.business.api.PaymentProcessor;
import com.merritt.samples.testing.dao.api.OrdersDao;
import com.merritt.samples.testing.exceptions.ItemNotFoundException;
import com.merritt.samples.testing.services.cc.client.CcResult;
import com.merritt.samples.testing.services.cc.client.CcService;

public class PaymentProcessorImpl implements PaymentProcessor
{
    @Inject
    CcService ccService;
    
    @Inject
    OrdersDao ordersDao;
    
    public CcResult processPayment( final long orderId, final String cardNumber ) throws NamingException, SQLException, ItemNotFoundException
    {
        final Order order = ordersDao.selectById( orderId );
        
        return ccService.processCard( cardNumber, order.getTotal() );
    }
}
