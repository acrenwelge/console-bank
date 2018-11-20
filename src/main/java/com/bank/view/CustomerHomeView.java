package com.bank.view;

import java.util.Collection;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.util.InputUtil;
import com.bank.util.Util;

public class CustomerHomeView {
	private static Scanner sc = Util.getScanner();
	private static Logger out = Util.getConsoleLogger();
	
	public int displayRootMenu() {
		while (true) {
			out.info("");
			out.info("MENU:");
			out.info("1 - View / edit personal information");
			out.info("2 - View summary of accounts");
			out.info("3 - View account details");
			out.info("4 - Create new account");
			out.info("5 - View transactions");
			out.info("6 - Logout");
			out.info("");
			return InputUtil.getInteger();
		}
	}
	
	public void listAccounts(Collection<Account> accounts) {
		accounts.forEach((Account a) -> out.info(a));
	}
	
	public void viewAccountDetails(Account acct) {
		out.info("Account details:");
		out.info("Name: \t\t" + acct.getName());
		out.info("Type: \t\t" + acct.getAcctType());
		out.info("Status: \t" + acct.getAcctStatus());
		out.info("Balance: \t"+acct.getCurrency().getSymbol()+acct.getBalance());
		out.info("Date created: \t"+acct.getCreationDate());
		out.info("");
	}
	
	public int displayAccountMenu() {
		out.info("Now what would you like to do?");
		out.info("1 - DEPOSIT to this account");
		out.info("2 - WITHDRAW from this account");
		out.info("3 - TRANSFER funds");
		out.info("4 - View another account");
		out.info("5 - Go back");
		return InputUtil.getInteger();
	}
	
	public boolean displayPersonalInfo(Customer c) {
		out.info("Your profile:");
		out.info("Customer ID: "+c.getId());
		out.info("First name: " + c.getFirstName());
		out.info("Last name: " + c.getLastName());
		out.info("Email: "+c.getEmail());
		out.info("Phone Number: "+c.getPhoneNumber());
		out.info("Birth date:"+c.getDob());
		out.info("Address: "+c.getAddress());
		out.info("");
		return InputUtil.getYesOrNo("Would you like to edit any of the above information? (y/n)");
	}
	
	public void setUserInfo(Customer c, String option) {
		while (true) {
			out.info("Enter new value");
			String newValue = sc.nextLine();
			switch (option) {
			case "first name": c.setFirstName(newValue); break; 
			case "last name": c.setLastName(newValue); break;
			case "address": c.setAddress(newValue); break;
			case "email": try {
					c.setEmail(newValue);
				} catch(IllegalArgumentException e) {
					out.error("Sorry, that's not a valid email"); continue; // if we don't put continue here, we'd exit the while loop
				} break;
			case "phone number": try {
					c.setPhoneNumber(newValue);
				} catch(IllegalArgumentException e) {
					out.error("Sorry, that's not a valid phone number"); continue;
				} break;
			default: break; // do nothing
			}
			break; // we've set the field, so we can break out of the while loop now
		}
	}
	
	public int displayNewAccountMenu() {
		out.info("Let's create a new account for you");
		out.info("What kind of account would you like to create?");
		out.info("1 - Checking");
		out.info("2 - Savings");
		return InputUtil.getInteger();
	}
	
	public int displayAccountCurrencyMenu() {
		out.info("What currency should this account be denominated in?");
		out.info("Please choose from CONSOLE BANK supported currencies: (default is USD)");
		out.info("1 - US Dollars");
		out.info("2 - Euros");
		return InputUtil.getInteger();
	}
	
}
