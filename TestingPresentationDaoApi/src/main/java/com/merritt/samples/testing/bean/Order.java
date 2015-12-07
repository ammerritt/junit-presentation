package com.merritt.samples.testing.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@SequenceGenerator( sequenceName = "ORDERS_SEQ", name = "ORDERS_SEQ_GEN", allocationSize = 1 )
@Table( name = "orders" )
public class Order
{
    @Id
    @GeneratedValue( generator = "ORDERS_SEQ_GEN", strategy = GenerationType.AUTO )
    @Column( name = "order_id" )
    private long orderId = -1L;
    
    @Column( name = "customer_id" )
    private long customerId = -1L;
    
    @Column( name = "total" )
    private double total;
    
    @Column( name = "result" )
    private String result = "";
    
    public long getOrderId()
    {
        return orderId;
    }
    
    public void setOrderId( final long orderId )
    {
        this.orderId = orderId;
    }
    
    public long getCustomerId()
    {
        return customerId;
    }
    
    public void setCustomerId( final long customerId )
    {
        this.customerId = customerId;
    }
    
    public double getTotal()
    {
        return total;
    }
    
    public void setTotal( final double total )
    {
        this.total = total;
    }
    
    public String getResult()
    {
        return result;
    }
    
    public void setResult( final String result )
    {
        this.result = result;
    }
}
