package com.bank.view;

import java.util.Collection;

import com.bank.model.Account;
import com.bank.model.Admin;
import com.bank.model.Customer;
import com.bank.util.InputUtil;

public class AdminHomeView {
	
	public int displayMenu(Admin a) {
		System.out.println();
		System.out.println("MENU:");
		System.out.println("1 - Approve / deny unnapproved accounts");
		System.out.println("2 - View all bank accounts");
		System.out.println("3 - View all transactions");
		System.out.println("4 - Look up customer");
		System.out.println("5 - Initiate funds transfer between accounts");
		System.out.println("6 - Logout");
		System.out.println();
		return InputUtil.getInteger();
	}
	
	public void showPendingAccounts(Collection<Account> allPending) {
		if (allPending.isEmpty())
			System.out.println("There are no currently pending accounts");
		else {
			System.out.println("Here are all currently pending accounts:");
			allPending.forEach(System.out::println);
		}
	}
	
	public void viewAllAccounts(Collection<Account> accounts) {
		System.out.println("All accounts at CONSOLE BANK:");
		accounts.forEach(System.out::println);
	}
	
	public int displayCustomerDialogMenu(Customer cust) {
		System.out.println("Customer " + cust.getId() + " details:");
		System.out.println(cust);
		System.out.println("Now what would you like to do?");
		System.out.println("1 - Edit customer information");
		System.out.println("2 - Approve/deny all pending customer accounts");
		System.out.println("3 - Perform transaction on customer account");
		System.out.println("4 - Suspend customer");
		System.out.println("5 - Reauthorize customer");
		System.out.println("6 - View another customer");
		System.out.println("7 - Go back");
		return InputUtil.getInteger();
	}
}
