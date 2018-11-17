package com.bank.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

import com.bank.controller.CustomerHomeController;
import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.Admin;
import com.bank.model.Customer;
import com.bank.model.MessageHolder;
import com.bank.model.Transaction;
import com.bank.model.exception.BankException;
import com.bank.services.AccountService;
import com.bank.services.CustomerService;
import com.bank.services.TransactionService;
import com.bank.util.InputUtil;
import com.bank.util.Util;
import com.bank.view.AdminHomeView;

public class AdminHomeController {
	private Scanner sc = Util.getScanner();
	private Logger log = Util.getLogger();
	private boolean doNotLogout = true;
	
	private Admin admin;
	private AdminHomeView ahv = new AdminHomeView();
	private TransactionController tc = new TransactionController();
	
	public AdminHomeController(Admin admin) {
		this.admin = admin;
	}
	
	public void init() {
		System.out.println("Welcome, " + admin.getFirstName());
		viewPendingAccounts();
		do {
			doNotLogout = true;
			int choice = ahv.displayMenu(admin);
			switch(choice) {
			case 1: reviewAllPendingAccounts(); break;
			case 2: viewAllAccounts(); break;
			case 3: tc.showAllTransactions(); break;
			case 4: viewCustomerDialog(); break;
			case 5: initTransferDialog(); break;
			case 6: logout(); break;
			default: System.out.println("Please choose an option from the menu"); break;
			}
		} while (doNotLogout);
	}
	
	public void viewPendingAccounts() {
		List<Account> allPending = AccountService.getAllAccountsByStatus(AccountStatus.UNAPPROVED);
		ahv.showPendingAccounts(allPending);
	}
	
	public void viewAllAccounts() {
		ahv.viewAllAccounts(AccountService.getAllAccounts());
	}
	
	public void reviewAllPendingAccounts() {
		List<Account> allPending = AccountService.getAllAccountsByStatus(AccountStatus.UNAPPROVED);
		if (allPending.isEmpty()) System.out.println("No accounts currently pending approval");
		else {
			System.out.println("Would you like to mass-approve all pending, unapproved accounts? (y/n)");
			String choice = sc.nextLine();
			if (choice.equals("y") || choice.equals("yes")) {
				AccountService.approveAccounts(allPending);
			}
			else if (choice.equals("n") || choice.equals("no")) {
				reviewIndividualPendingAccounts(allPending);
			}
		}
	}
	
	public void reviewIndividualPendingAccounts(List<Account> allPending) {
		for (Account a : allPending) {
			System.out.println("Account #" + a.getId() + ": " + a);
			System.out.println("Approve this account? (y/n)");
			String s = "";
			boolean yes = false;
			boolean no = false;
			while (!(yes || no)) {
				s = sc.nextLine();
				yes = s.equals("yes") || s.equals("y");
				no  = s.equals("no")  || s.equals("n");
				if (yes) {
					AccountService.approveAccount(a);
				} else if (no) {
					String c = "";
					while (!(c.equals("no") || c.equals("n") || c.equals("y") || c.equals("yes"))) {
						System.out.println("Do you want to suspend the account?");
						c = sc.nextLine();
						if (c.equals("y") || c.equals("yes"))
							AccountService.suspendAccount(a);
					}
				} else {
					System.err.println("Please enter yes or no (y/n)");
				}
			}
		}
	}
	
	public void viewCustomerDialog() {
		boolean exit = false;
		while(!exit) {
			exit = true;
			Customer cust = InputUtil.getCustomerFromUser("Which customer would you like to view details for? Please enter customer username");
			CustomerHomeController chc = new CustomerHomeController(cust);
			int choice = ahv.displayCustomerDialogMenu(cust);
			switch(choice) {
			case 1: chc.editUserInfo(); break;
			case 2: approveCustomerAccounts(cust); break;
			case 3: chc.viewAccountDetailsDialog(); break;
			case 4: CustomerService.suspendCustomer(cust); break;
			case 5: CustomerService.reauthorizeCustomer(cust); break;
			case 6: exit = false; break;
			case 7: break;
			default: exit = false; break;
			}
		}
	}
	
	public void approveCustomerAccounts(Customer c) {
		List<Account> customerPendingAccounts = new ArrayList<>();
		c.getAccounts().forEach((Integer id) -> {
			Account acct = AccountService.getAccountById(id);
			if (acct.getAcctStatus().equals(AccountStatus.UNAPPROVED))
				customerPendingAccounts.add(acct);
		});
		if (customerPendingAccounts.isEmpty())
			System.out.println("Customer has no pending accounts");
		else
			reviewIndividualPendingAccounts(customerPendingAccounts);
	}
	
	public void initTransferDialog() {
		Account from = InputUtil.getAccountFromUser("Which account would you like to transfer funds TO?");
		Account to = InputUtil.getAccountFromUser("Which account would you like to transfer funds TO?");
		BigDecimal amt = InputUtil.getAmountFromUser("How much would you like to transfer?");
		try {
			AccountService.transferFunds(from, to, amt);
			Transaction tr = new Transaction(LocalDateTime.now(), amt, admin, from, to);
			TransactionService.saveTransaction(tr);
			log.info(tr);
		} catch (BankException e) {
			log.error(MessageHolder.exceptionLogMsg, e);
			System.err.println(e.getMessage());
		}
	}
	
	public void logout() {
		System.out.println("Logging out now...");
		log.info("Admin " + admin.getUsername() + " logged out");
		doNotLogout = false;
	}
	
}
