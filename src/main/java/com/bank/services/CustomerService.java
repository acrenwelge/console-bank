package com.bank.services;

import java.io.IOException;

import com.bank.model.Customer;
import com.bank.model.CustomerStatus;
import com.bank.serialize.CustomerReaderWriter;

public class CustomerService {
	private CustomerService() {}

	public static void suspendCustomer(Customer cust) throws IOException {
		cust.setCustStatus(CustomerStatus.SUSPENDED);
		CustomerReaderWriter.saveCustomer(cust);
		System.out.println("Customer suspended");
	}
	
	public static void reauthorizeCustomer(Customer cust) throws IOException {
		cust.setCustStatus(CustomerStatus.ACTIVE);
		CustomerReaderWriter.saveCustomer(cust);
		System.out.println("Customer reauthorized");
	}
}
