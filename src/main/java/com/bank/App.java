package com.bank;

import static com.bank.serialize.CustomerReaderWriter.getCustomerByUsername;
import static com.bank.serialize.CustomerReaderWriter.registerNewCustomer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.bank.model.Admin;
import com.bank.model.Customer;
import com.bank.model.CustomerStatus;
import com.bank.model.MessageHolder;
import com.bank.serialize.AdminReaderWriter;
import com.bank.serialize.CustomerReaderWriter;

public class App {
	private static boolean doNotExit = true;
	private static Scanner sc = Util.getScanner();
	
    public static void main( String[] args )
    {
        initialize();
    }
    
    public static void initialize() {
    	System.out.println("Hello and welcome to CONSOLE BANK!");
    	do {
    		System.out.println("Please choose to login, register, or exit the bank:");
        	System.out.println(" 1 - login as customer");
        	System.out.println(" 2 - login as admin");
        	System.out.println(" 3 - register as customer");
        	System.out.println(" 4 - register as admin");
        	System.out.println(" 5 - exit");
        	try {
        		int i = Integer.parseInt(sc.nextLine());
        		switch(i) {
        			case 1: customerLogin(); break;
        			case 2: adminLogin(); break;
        			case 3: customerRegister(); break;
        			case 4: adminRegister(); break;
        			case 5: exit(); break;
        		}
        	} catch (InputMismatchException ime) {
        		System.err.println("Sorry, please make a valid choice");
        	}
    	} while (doNotExit);
    }
    
    public static void customerLogin() {
    	Customer c = new Customer();
    	boolean exit = false;
    	while(!exit) {
    		exit = true;
			System.out.println();
	    	System.out.println("Please enter your username: ");
	    	String uname = sc.nextLine();
	    	System.out.println("Please enter your password: ");
	    	String pword = sc.nextLine();
			try {
				c = getCustomerByUsername(uname);
			} catch (FileNotFoundException e) {
				System.err.println(MessageHolder.invalidCredentials);
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (c.getPassword().equals(pword)) {
	    		System.out.println("Login successful!");
	    	} else {
	    		System.err.println(MessageHolder.invalidCredentials);
	    		exit = false;
	    	}
    	}
    	if (c.getCustStatus().equals(CustomerStatus.SUSPENDED))
    		System.out.println("Sorry, your user account has been suspended - please contact an administrator to reactivate your account");
    	else
    		CustomerHomepage.displayHomepage(c);
    }
    
    public static void adminLogin() {
    	System.out.println();
    	System.out.println("Please enter your username: ");
    	String uname = sc.nextLine();
    	System.out.println("Please enter your password: ");
    	String pword = sc.nextLine();
    	Admin a = null;
		try {
			a = AdminReaderWriter.getAdminByUsername(uname);
		} catch (FileNotFoundException e) {
			System.err.println(MessageHolder.invalidCredentials);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	while(true) {
    		if (a == null) {
    			adminLogin(); // THIS COULD BE SOURCE OF LOGOUT BUG
    		}
    		if (a.getPassword().equals(pword)) {
        		System.out.println("Login successful!");
        		break;
        	} else {
        		System.err.println(MessageHolder.invalidCredentials);
        		adminLogin();
        	}
    	}
    	AdminHomepage.displayHomepage(a);
    }
    
    public static void adminRegister() {
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
				e.printStackTrace();
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
    	System.out.println("Great! Now set a password for your account:");
    	String pword = sc.nextLine();
    	newAdmin.setPassword(pword);
    	try {
			AdminReaderWriter.registerNewAdmin(newAdmin);
		} catch (IOException e) {
			System.err.println("Something went wrong while registering... try again");
			e.printStackTrace();
		}
    }
    
    public static void customerRegister() {
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
				e.printStackTrace();
				System.out.println("Let's try again.. please re-enter the username");
				continue;
			}
        	if (alreadyExists)
        		System.err.println(MessageHolder.usernameExists);
        	else {
        		c.setUsername(uname);
        		break;
        	}
    	}
    	System.out.println("Great! Now set a password for your account:");
    	String pword = sc.nextLine();
    	c.setPassword(pword);
    	while (true) {
    		System.out.println("What's your date of birth (format: YYYY-MM-DD)?");
    		try {
        		LocalDate dob = LocalDate.parse(sc.nextLine());
            	c.setDob(dob);
            	break;
        	} catch (DateTimeParseException e) {
        		System.err.println("Invalid date format");
        	}
    	}
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
    	try {
			registerNewCustomer(c);
		} catch (IOException e) {
			System.err.println(MessageHolder.ioMessage);
			e.printStackTrace();
		}
    	System.out.println("Now, you can login with your username / password");
    	customerLogin();
    }
    
    public static void exit() {
    	System.out.println("Goodbye! Please come back and see us soon");
    	doNotExit = false;
    }
}
