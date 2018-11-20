package com.bank;

import com.bank.controller.AdminHomeController;
import com.bank.controller.CustomerHomeController;
import com.bank.controller.TransactionController;
import com.bank.model.Admin;
import com.bank.model.Customer;
import com.bank.serialize.AccountReaderWriter;
import com.bank.serialize.AdminReaderWriter;
import com.bank.serialize.CustomerReaderWriter;
import com.bank.serialize.TransactionReaderWriter;
import com.bank.services.AccountService;
import com.bank.services.AdminService;
import com.bank.services.CustomerService;
import com.bank.services.TransactionService;
import com.bank.util.InputUtil;
import com.bank.view.AdminHomeView;
import com.bank.view.CustomerHomeView;

public class AppFactory {
	private AppFactory() {}
	
	static App singletonApp;
	
	static App getApp() {
		if (singletonApp != null) return singletonApp;
		else {
			Customer cust = new Customer();
			Admin admin = new Admin();
			// reader/writers
			TransactionReaderWriter trw = new TransactionReaderWriter();
			CustomerReaderWriter crw = new CustomerReaderWriter();
			AdminReaderWriter adrw = new AdminReaderWriter();
			AccountReaderWriter arw = new AccountReaderWriter(crw);
			
			// services
			AccountService actService = new AccountService(arw);
			AdminService adService = new AdminService(adrw);
			CustomerService custService = new CustomerService(crw);
			TransactionService tService = new TransactionService(trw);
			
			// views
			CustomerHomeView chv = new CustomerHomeView();
			AdminHomeView ahv = new AdminHomeView();
			
			// InputUtil
			InputUtil iu = new InputUtil(actService, custService);
			
			// controllers
			TransactionController tc = new TransactionController(tService);
			CustomerHomeController chc = new CustomerHomeController(cust, actService, chv, tc, custService, iu, tService);
			AdminHomeController ahc = new AdminHomeController(admin, tc, chc, actService, custService, ahv, iu, tService);
			
			singletonApp = new App(chc, ahc, adService, custService);
			return singletonApp;
		}
	}
	
}
