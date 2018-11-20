package com.bank;

import java.time.LocalDate;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

import com.bank.controller.AdminHomeController;
import com.bank.controller.CustomerHomeController;
import com.bank.model.Admin;
import com.bank.model.Customer;
import com.bank.model.CustomerStatus;
import com.bank.model.MessageHolder;
import com.bank.services.AdminService;
import com.bank.services.CustomerService;
import com.bank.util.InputUtil;
import com.bank.util.Util;

public class App {
	private boolean exit = false;
	private static Scanner sc = Util.getScanner();
	private static Logger log = Util.getFileLogger();
	private static Logger out = Util.getConsoleLogger();
	private Customer cust;
	private Admin admin;
	private CustomerHomeController chc;
	private AdminHomeController ahc;
	private AdminService aService;
	private CustomerService custService;
	
	public App(CustomerHomeController chc, AdminHomeController ahc, AdminService aService, CustomerService custService) {
		this.chc = chc;
		this.ahc = ahc;
		this.aService = aService;
		this.custService = custService;
	}
	
    public static void main( String[] args ) {
    	out.info("Hello and welcome to CONSOLE BANK");
    	App a = AppFactory.getApp();
    	while (!a.exit) {
    		a.showMainMenu();
    	}
    }
    
    public void showMainMenu() {
		out.info("Please choose to login, register, or exit the bank:");
    	out.info(" 1 - login as customer");
    	out.info(" 2 - login as admin");
    	out.info(" 3 - register as customer");
    	out.info(" 4 - register as admin");
    	out.info(" 5 - exit");
    	int i = InputUtil.getInteger();
		switch(i) {
			case 1: customerLogin(); break;
			case 2: adminLogin(); break;
			case 3: customerRegister(); break;
			case 4: adminRegister(); break;
			case 5: exit(); break;
			default: break;
		}
    }
    
    public void customerLogin() {
    	boolean localExit = false;
    	while(!localExit) {
    		localExit = true;
			out.info("");
	    	out.info("Please enter your username: ");
	    	String uname = sc.nextLine();
	    	out.info("Please enter your password: ");
	    	String pword = sc.nextLine();
	    	cust = custService.getCustomerByUsername(uname);
	    	if (cust == null)
	    		out.error(MessageHolder.invalidCredentials);
	    	else if (cust.getPassword().equals(pword)) {
	    		out.info("Login successful!");
	    		log.info("Customer {} logged in successfully", cust.getUsername());
	    	} else {
	    		log.error("Invalid credentials for user: " + cust.getUsername());
	    		out.error(MessageHolder.invalidCredentials);
	    		localExit = false;
	    	}
    	}
    	if (cust.getCustStatus() != null && cust.getCustStatus().equals(CustomerStatus.SUSPENDED))
    		out.info("Sorry, your user account has been suspended - please contact an administrator to reactivate your account");
    	else {
    		chc.setCustomer(cust);
    		chc.displayHomepage();
    	}
    }
    
    public void adminLogin() {
    	boolean localExit = false;
    	while (!localExit) {
    		out.info("");
        	out.info("Please enter your username: ");
        	String uname = sc.nextLine();
        	out.info("Please enter your password: ");
        	String pword = sc.nextLine();
        	admin = aService.getAdminByUsername(uname);
        	if (admin == null)
    		    out.error(MessageHolder.invalidCredentials);
        	else if (admin.getPassword().equals(pword)) {
    			localExit = true;
        		out.info("Login successful!");
        		log.info("Admin {} logged in successfully", admin.getUsername());
        	} else {
        		out.error(MessageHolder.invalidCredentials);
        	}
    	}
    	ahc.setAdmin(admin);
    	ahc.init();
    }
    
    public void adminRegister() {
    	Admin newAdmin = new Admin();
    	out.info("What's your first name?");
    	String fname = sc.nextLine();
    	newAdmin.setFirstName(fname);
    	out.info("What's your last name?");
    	String lname = sc.nextLine();
    	newAdmin.setLastName(lname);
    	out.info("Now choose a username:");
    	while (true) {
        	String uname = sc.nextLine();
        	boolean alreadyExists = true;
			alreadyExists = custService.checkIfUsernameExists(uname);
        	if (alreadyExists)
        		out.error("Sorry, that username already exists. Choose a different one:");
        	else {
        		newAdmin.setUsername(uname);
        		break;
        	}
    	}
    	String pword = getPasswordForRegistration();
    	newAdmin.setPassword(pword);
		aService.registerAdmin(newAdmin);
    }
    
    public void customerRegister() {
    	cust.setCustomerSince(LocalDate.now());
    	out.info("Thanks for choosing CONSOLE BANK! Let's get some information to get started:");
    	out.info("What's your first name?");
    	String fname = sc.nextLine();
    	cust.setFirstName(fname);
    	out.info("What's your last name?");
    	String lname = sc.nextLine();
    	cust.setLastName(lname);
    	out.info("Now choose a username:");
    	while (true) {
        	String uname = sc.nextLine();
        	boolean alreadyExists = true;
			alreadyExists = custService.checkIfUsernameExists(uname);
        	if (alreadyExists) {
        		out.error(MessageHolder.usernameExists);
        		log.error("Username {} already exists", uname);
        	}
        	else {
        		cust.setUsername(uname);
        		break;
        	}
    	}
    	String pword = getPasswordForRegistration();
    	cust.setPassword(pword);
		out.info("What's your date of birth (format: YYYY-MM-DD)?");
		LocalDate dob = InputUtil.getDateFromUser();
		cust.setDob(dob);
    	out.info("What's your mailing address?");
    	String address = sc.nextLine();
    	cust.setAddress(address);
    	out.info("What's your email address?");
    	String email = sc.nextLine();
    	cust.setEmail(email);
    	out.info("What's your phone number?");
    	String num = sc.nextLine();
    	cust.setPhoneNumber(num);
    	out.info("Thanks! Let's get you registered...");
    	cust.setCustStatus(CustomerStatus.ACTIVE);
    	custService.registerCustomer(cust);
    	out.info("Now, you can login with your username / password");
    	customerLogin();
    }
    
    public String getPasswordForRegistration() {
    	out.info("Great! Now set a password for your account:");
    	while (true) {
    		String pword = sc.nextLine();
        	out.info("Confirm the password by entering it again:");
        	String pword2 = sc.nextLine();
        	if (pword.equals(pword2)) {
        		return pword;
        	} else {
        		out.error("Passwords don't match - try again");
        	}
    	}
    }
    
    public void exit() {
    	out.info("Goodbye! Please come back and see us soon");
    	exit = true;
    }
}
