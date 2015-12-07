package com.merritt.samples.testing.business.api;

import java.sql.SQLException;

import javax.naming.NamingException;

import com.merritt.samples.testing.exceptions.ItemNotFoundException;
import com.merritt.samples.testing.services.cc.client.CcResult;

public interface PaymentProcessor
{
    public abstract CcResult processPayment( final long orderId, final String cardNumber ) throws NamingException, SQLException, ItemNotFoundException;
}