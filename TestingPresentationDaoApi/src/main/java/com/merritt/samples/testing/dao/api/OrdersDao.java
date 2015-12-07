package com.merritt.samples.testing.dao.api;

import java.sql.SQLException;

import javax.naming.NamingException;

import com.merritt.samples.testing.bean.Order;
import com.merritt.samples.testing.exceptions.ItemNotFoundException;

public interface OrdersDao
{
    Order selectById( final long orderId ) throws NamingException, SQLException, ItemNotFoundException;
    
    long insert( final long customerId, final double total, final String result ) throws NamingException, SQLException, ItemNotFoundException;
}