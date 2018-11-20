package com.bank.services;

import org.apache.logging.log4j.Logger;

import com.bank.util.Util;
import com.bank.model.Customer;
import com.bank.model.CustomerStatus;
import com.bank.serialize.CustomerReaderWriter;

public class CustomerService {
	private static Logger log = Util.getFileLogger();
	private static Logger out = Util.getConsoleLogger();
	private CustomerReaderWriter crw;
	
	public CustomerService(CustomerReaderWriter crw) {
		this.crw = crw;
	}
	
	public Customer getCustomerByUsername(String username) {
		return Util.catchIOExceptionsReturnType(() -> crw.getCustomerByUsername(username));
	}
	
	public boolean checkIfUsernameExists(String username) {
		return Util.catchIOExceptionsReturnBool(() -> crw.checkNewUsername(username));
	}
	
	public void saveCustomer(Customer cust) {
		Util.catchIOExceptionsReturnVoid(() -> crw.saveCustomer(cust));
	}

	public void suspendCustomer(Customer cust) {
		cust.setCustStatus(CustomerStatus.SUSPENDED);
		Util.catchIOExceptionsReturnVoid(() -> crw.saveCustomer(cust));
		out.info("Customer suspended");
	}
	
	public void reauthorizeCustomer(Customer cust) {
		cust.setCustStatus(CustomerStatus.ACTIVE);
		Util.catchIOExceptionsReturnVoid(() -> crw.saveCustomer(cust));
		out.info("Customer reauthorized");
	}
	
	public void registerCustomer(Customer cust) {
		Util.catchIOExceptionsReturnVoid(() -> crw.registerNewCustomer(cust));
		log.info("Customer " + cust.getUsername() + " registered");
	}
}
