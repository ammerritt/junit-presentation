package com.merritt.samples.testing.dao.impl;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.merritt.samples.testing.bean.Order;
import com.merritt.samples.testing.dao.api.OrdersDao;
import com.merritt.samples.testing.exceptions.ItemNotFoundException;

@Repository( "ordersDao" )
public class OrdersJpaDao extends AbstractJpaDao<Order, Long> implements OrdersDao
{
    
    @Override
    public Order selectById( final long orderId ) throws NamingException, SQLException, ItemNotFoundException
    {
        final Order order = findById( Long.valueOf( orderId ) );
        if( order == null )
        {
            throw new ItemNotFoundException( "Unable to find order with id: '" + orderId + "'" );
        }
        
        return order;
    }
    
    @Override
    @Transactional
    public long insert( final long customerId, final double total, final String result ) throws NamingException, SQLException, ItemNotFoundException
    {
        final Order order = new Order();
        order.setCustomerId( customerId );
        order.setResult( result );
        order.setTotal( total );
        save( order );
        return order.getOrderId();
    }
    
}
