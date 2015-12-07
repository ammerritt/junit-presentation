package com.merritt.samples.testing.business.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.merritt.samples.testing.bean.Order;
import com.merritt.samples.testing.dao.api.OrdersDao;
import com.merritt.samples.testing.services.cc.client.CcService;

@Configuration
@Profile( "mock" )
public class MockTestBusinessSpringConfig
{
    @Bean
    public CcService ccServiceImpl()
    {
        return Mockito.mock( CcService.class );
    }
    
    @Bean
    public Order testOrder1()
    {
        final Order testOrder = new Order();
        testOrder.setCustomerId( 1 );
        testOrder.setOrderId( 1 );
        testOrder.setResult( "Success" );
        testOrder.setTotal( 5.00d );
        return testOrder;
    }
    
    @Bean
    public Order testOrder2()
    {
        final Order testOrder = new Order();
        testOrder.setCustomerId( 2 );
        testOrder.setOrderId( 2 );
        testOrder.setResult( "Success" );
        testOrder.setTotal( 10.00d );
        return testOrder;
    }
    
    @Bean
    public OrdersDao ordersDao()
    {
        return Mockito.mock( OrdersDao.class );
    }
}
