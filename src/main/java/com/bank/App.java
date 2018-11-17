package com.bank;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

import com.bank.controller.AdminHomeController;
import com.bank.controller.CustomerHomeController;
import com.bank.model.Admin;
import com.bank.model.Customer;
import com.bank.model.CustomerStatus;
import com.bank.model.MessageHolder;
import com.bank.serialize.CustomerReaderWriter;
import com.bank.services.AdminService;
import com.bank.services.CustomerService;
import com.bank.util.InputUtil;
import com.bank.util.Util;

public class App {
	private boolean doNotExit = true;
	private Scanner sc = Util.getScanner();
	private Logger log = Util.getLogger();
	
    public static void main( String[] args )
    {
    	new App().initialize();
    }
    
    public void initialize() {
    	System.out.println("Hello and welcome to CONSOLE BANK!");
    	do {
    		System.out.println("Please choose to login, register, or exit the bank:");
        	System.out.println(" 1 - login as customer");
        	System.out.println(" 2 - login as admin");
        	System.out.println(" 3 - register as customer");
        	System.out.println(" 4 - register as admin");
        	System.out.println(" 5 - exit");
        	int i = InputUtil.getInteger();
    		switch(i) {
    			case 1: customerLogin(); break;
    			case 2: adminLogin(); break;
    			case 3: customerRegister(); break;
    			case 4: adminRegister(); break;
    			case 5: exit(); break;
    			default: continue;
    		}
    	} while (doNotExit);
    }
    
    public void customerLogin() {
    	Customer c = new Customer();
    	boolean exit = false;
    	while(!exit) {
    		exit = true;
			System.out.println();
	    	System.out.println("Please enter your username: ");
	    	String uname = sc.nextLine();
	    	System.out.println("Please enter your password: ");
	    	String pword = sc.nextLine();
	    	c = CustomerService.getCustomerByUsername(uname);
	    	if (c == null)
	    		System.err.println(MessageHolder.invalidCredentials);
	    	else if (c.getPassword().equals(pword)) {
	    		System.out.println("Login successful!");
	    		log.info("Customer {} logged in successfully", c.getUsername());
	    	} else {
	    		log.error("Invalid credentials for user: " + c.getUsername());
	    		System.err.println(MessageHolder.invalidCredentials);
	    		exit = false;
	    	}
    	}
    	if (c.getCustStatus() != null && c.getCustStatus().equals(CustomerStatus.SUSPENDED))
    		System.out.println("Sorry, your user account has been suspended - please contact an administrator to reactivate your account");
    	else
    		new CustomerHomeController(c).displayHomepage();
    }
    
    public void adminLogin() {
    	Admin a = new Admin();
    	boolean exit = false;
    	while (!exit) {
    		System.out.println();
        	System.out.println("Please enter your username: ");
        	String uname = sc.nextLine();
        	System.out.println("Please enter your password: ");
        	String pword = sc.nextLine();
        	a = AdminService.getAdminByUsername(uname);
        	if (a == null)
    			System.err.println(MessageHolder.invalidCredentials);
        	else if (a.getPassword().equals(pword)) {
    			exit = true;
        		System.out.println("Login successful!");
        		log.info("Admin {} logged in successfully", a.getUsername());
        	} else {
        		System.err.println(MessageHolder.invalidCredentials);
        	}
    	}
    	new AdminHomeController(a).init();
    }
    
    public void adminRegister() {
    	Admin newAdmin = new Admin();
    	System.out.println("What's your first name?");
    	String fname = sc.nextLine();
    	newAdmin.setFirstName(fname);
    	System.out.println("What's your last name?");
    	String lname = sc.nextLine();
    	newAdmin.setLastName(lname);
    	System.out.println("Now choose a username:");
    	while (true) {
        	String uname = sc.nextLine();
        	boolean alreadyExists = true;
			try {
				alreadyExists = CustomerReaderWriter.checkNewUsername(uname);
			} catch (IOException e) {
				System.err.println(MessageHolder.ioMessage);
				log.error(MessageHolder.exceptionLogMsg, e);
				System.out.println("Let's try again.. please re-enter the username");
				continue;
			}
        	if (alreadyExists)
        		System.err.println("Sorry, that username already exists. Choose a different one:");
        	else {
        		newAdmin.setUsername(uname);
        		break;
        	}
    	}
    	String pword = getPasswordForRegistration();
    	newAdmin.setPassword(pword);
		AdminService.registerAdmin(newAdmin);
    }
    
    public void customerRegister() {
    	Customer c = new Customer();
    	c.setCustomerSince(LocalDate.now());
    	System.out.println("Thanks for choosing CONSOLE BANK! Let's get some information to get started:");
    	System.out.println("What's your first name?");
    	String fname = sc.nextLine();
    	c.setFirstName(fname);
    	System.out.println("What's your last name?");
    	String lname = sc.nextLine();
    	c.setLastName(lname);
    	System.out.println("Now choose a username:");
    	while (true) {
        	String uname = sc.nextLine();
        	boolean alreadyExists = true;
			try {
				alreadyExists = CustomerReaderWriter.checkNewUsername(uname);
			} catch (IOException e) {
				System.err.println(MessageHolder.ioMessage);
				log.error("Exception thrown: ",e);
				System.out.println("Let's try again.. please re-enter the username");
				continue;
			}
        	if (alreadyExists) {
        		System.err.println(MessageHolder.usernameExists);
        		log.error("Username {} already exists", uname);
        	}
        	else {
        		c.setUsername(uname);
        		break;
        	}
    	}
    	String pword = getPasswordForRegistration();
    	c.setPassword(pword);
		System.out.println("What's your date of birth (format: YYYY-MM-DD)?");
		LocalDate dob = InputUtil.getDateFromUser();
		c.setDob(dob);
    	System.out.println("What's your mailing address?");
    	String address = sc.nextLine();
    	c.setAddress(address);
    	System.out.println("What's your email address?");
    	String email = sc.nextLine();
    	c.setEmail(email);
    	System.out.println("What's your phone number?");
    	String num = sc.nextLine();
    	c.setPhoneNumber(num);
    	System.out.println("Thanks! Let's get you registered...");
    	c.setCustStatus(CustomerStatus.ACTIVE);
    	CustomerService.registerCustomer(c);
    	System.out.println("Now, you can login with your username / password");
    	customerLogin();
    }
    
    public String getPasswordForRegistration() {
    	System.out.println("Great! Now set a password for your account:");
    	while (true) {
    		String pword = sc.nextLine();
        	System.out.println("Confirm the password by entering it again:");
        	String pword2 = sc.nextLine();
        	if (pword.equals(pword2)) {
        		return pword;
        	} else {
        		System.err.println("Passwords don't match - try again");
        	}
    	}
    }
    
    public void exit() {
    	System.out.println("Goodbye! Please come back and see us soon");
    	doNotExit = false;
    }
}
