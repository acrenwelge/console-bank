package com.bank.services;

import org.apache.logging.log4j.Logger;

import com.bank.util.Util;
import com.bank.model.Customer;
import com.bank.model.CustomerStatus;
import com.bank.serialize.CustomerReaderWriter;

public class CustomerService {
	private static Logger log = Util.getLogger();
	private CustomerService() {}
	
	public static Customer getCustomerByUsername(String username) {
		return Util.catchIOExceptionsReturnType(() -> CustomerReaderWriter.getCustomerByUsername(username));
	}
	
	public static void saveCustomer(Customer cust) {
		Util.catchIOExceptionsVoid(() -> CustomerReaderWriter.saveCustomer(cust));
	}

	public static void suspendCustomer(Customer cust) {
		cust.setCustStatus(CustomerStatus.SUSPENDED);
		Util.catchIOExceptionsVoid(() -> CustomerReaderWriter.saveCustomer(cust));
		System.out.println("Customer suspended");
	}
	
	public static void reauthorizeCustomer(Customer cust) {
		cust.setCustStatus(CustomerStatus.ACTIVE);
		Util.catchIOExceptionsVoid(() -> CustomerReaderWriter.saveCustomer(cust));
		System.out.println("Customer reauthorized");
	}
	
	public static void registerCustomer(Customer cust) {
		Util.catchIOExceptionsVoid(() -> CustomerReaderWriter.registerNewCustomer(cust));
		log.info("Customer " + cust.getUsername() + " registered");
	}
}
