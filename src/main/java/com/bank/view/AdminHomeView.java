package com.bank.view;

import java.util.Collection;

import org.apache.logging.log4j.Logger;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.util.InputUtil;
import com.bank.util.Util;

public class AdminHomeView {
	private static Logger out = Util.getConsoleLogger();
	
	public int displayMenu() {
		out.info("");
		out.info("MENU:");
		out.info("1 - Approve / deny unnapproved accounts");
		out.info("2 - View all bank accounts");
		out.info("3 - View all transactions");
		out.info("4 - Look up customer");
		out.info("5 - Initiate funds transfer between accounts");
		out.info("6 - Logout");
		out.info("");
		return InputUtil.getInteger();
	}
	
	public void showPendingAccounts(Collection<Account> allPending) {
		if (allPending.isEmpty())
			out.info("There are no currently pending accounts");
		else {
			out.info("Here are all currently pending accounts:");
			allPending.forEach(out::info);
		}
	}
	
	public void viewAllAccounts(Collection<Account> accounts) {
		out.info("All accounts at CONSOLE BANK:");
		accounts.forEach(out::info);
	}
	
	public int displayCustomerDialogMenu(Customer cust) {
		out.info("Customer " + cust.getId() + " details:");
		out.info(cust);
		out.info("Now what would you like to do?");
		out.info("1 - Edit customer information");
		out.info("2 - Approve/deny all pending customer accounts");
		out.info("3 - Perform transaction on customer account");
		out.info("4 - Suspend customer");
		out.info("5 - Reauthorize customer");
		out.info("6 - View another customer");
		out.info("7 - Go back");
		return InputUtil.getInteger();
	}
}
