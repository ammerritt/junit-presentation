package com.merritt.samples.testing.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.springframework.stereotype.Repository;

import com.merritt.samples.testing.bean.Order;
import com.merritt.samples.testing.dao.api.OrdersDao;
import com.merritt.samples.testing.exceptions.ItemNotFoundException;

@Repository( "ordersDao" )
public class OrdersJdbcDao extends GenericJdbcDao implements OrdersDao
{
    protected static final String SELECT_SQL_ORDERS_BY_ID = "select order_id, customer_id, total, result from orders where order_id = ?";
    protected static final String INSERT_SQL = "insert into orders ( order_id, customer_id, total, result ) values ( orders_seq.nextval, ?, ?, ? )";
    
    public Order selectById( final long orderId ) throws NamingException, SQLException, ItemNotFoundException
    {
        final Order order = new Order();
        Connection connection = null;
        PreparedStatement preparedStatment = null;
        ResultSet resultSet = null;
        try
        {
            connection = getConnection();
            preparedStatment = connection.prepareStatement( SELECT_SQL_ORDERS_BY_ID );
            preparedStatment.setLong( 1, orderId );
            resultSet = preparedStatment.executeQuery();
            if( resultSet.next() )
            {
                order.setOrderId( resultSet.getLong( "order_id" ) );
                order.setCustomerId( resultSet.getLong( "customer_id" ) );
                order.setTotal( resultSet.getDouble( "total" ) );
                order.setResult( resultSet.getString( "result" ) );
            }
            else
            {
                throw new ItemNotFoundException( "Unable to find order with id: '" + orderId + "'" );
            }
        }
        finally
        {
            close( connection, preparedStatment, resultSet );
        }
        return order;
    }
    
    public long insert( final long customerId, final double total, final String result ) throws NamingException, SQLException, ItemNotFoundException
    {
        long orderId;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            connection = getConnection();
            preparedStatement = connection.prepareStatement( INSERT_SQL, new int[] { 1 } );
            preparedStatement.setLong( 1, customerId );
            preparedStatement.setDouble( 2, total );
            preparedStatement.setString( 3, result );
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            orderId = resultSet.getLong( 1 );
        }
        finally
        {
            close( connection, preparedStatement, resultSet );
        }
        return orderId;
    }
    
}
