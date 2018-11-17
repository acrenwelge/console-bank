package com.bank.view;

import java.util.Collection;
import java.util.Scanner;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.util.InputUtil;
import com.bank.util.Util;

public class CustomerHomeView {
	public CustomerHomeView() {}
	private Scanner sc = Util.getScanner();
	
	public int displayRootMenu(Customer c) {
		while (true) {
			System.out.println();
			System.out.println("MENU:");
			System.out.println("1 - View / edit personal information");
			System.out.println("2 - View summary of accounts");
			System.out.println("3 - View account details");
			System.out.println("4 - Create new account");
			System.out.println("5 - View transactions");
			System.out.println("6 - Logout");
			System.out.println();
			try {
				return Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				System.err.println("Please enter a number");
			}
		}
	}
	
	public void listAccounts(Collection<Account> accounts) {
		accounts.forEach((Account a) -> {
			System.out.println(a);
		});
	}
	
	public void viewAccountDetails(Account acct) {
		System.out.println("Account details:");
		System.out.println("Name: \t\t" + acct.getName());
		System.out.println("Type: \t\t" + acct.getAcctType());
		System.out.println("Status: \t" + acct.getAcctStatus());
		System.out.println("Balance: \t"+acct.getCurrency().getSymbol()+acct.getBalance());
		System.out.println("Date created: \t"+acct.getCreationDate());
		System.out.println();
	}
	
	public int displayAccountMenu() {
		System.out.println("Now what would you like to do?");
		System.out.println("1 - DEPOSIT to this account");
		System.out.println("2 - WITHDRAW from this account");
		System.out.println("3 - TRANSFER funds");
		System.out.println("4 - View another account");
		System.out.println("5 - Go back");
		return InputUtil.getInteger();
	}
	
	public boolean displayPersonalInfo(Customer c) {
		System.out.println("Your profile:");
		System.out.println("Customer ID: "+c.getId());
		System.out.println("First name: " + c.getFirstName());
		System.out.println("Last name: " + c.getLastName());
		System.out.println("Email: "+c.getEmail());
		System.out.println("Phone Number: "+c.getPhoneNumber());
		System.out.println("Birth date:"+c.getDob());
		System.out.println("Address: "+c.getAddress());
		System.out.println();
		return InputUtil.getYesOrNo("Would you like to edit any of the above information? (y/n)");
	}
	
	public void setUserInfo(Customer c, String option) {
		while (true) {
			System.out.println("Enter new value");
			String newValue = sc.nextLine();
			switch (option) {
			case "first name": c.setFirstName(newValue); break; 
			case "last name": c.setLastName(newValue); break;
			case "address": c.setAddress(newValue); break;
			case "email": try {
					c.setEmail(newValue);
				} catch(IllegalArgumentException e) {
					System.err.println("Sorry, that's not a valid email"); continue; // if we don't put continue here, we'd exit the while loop
				} break;
			case "phone number": try {
					c.setPhoneNumber(newValue);
				} catch(IllegalArgumentException e) {
					System.err.println("Sorry, that's not a valid phone number"); continue;
				} break;
			default: break; // do nothing
			}
			break; // we've set the field, so we can break out of the while loop now
		}
	}
	
	public int displayNewAccountMenu() {
		System.out.println("Let's create a new account for you");
		System.out.println("What kind of account would you like to create?");
		System.out.println("1 - Checking");
		System.out.println("2 - Savings");
		return InputUtil.getInteger();
	}
	
	public int displayAccountCurrencyMenu() {
		System.out.println("What currency should this account be denominated in?");
		System.out.println("Please choose from CONSOLE BANK supported currencies: (default is USD)");
		System.out.println("1 - US Dollars");
		System.out.println("2 - Euros");
		return InputUtil.getInteger();
	}
	
}
