package com.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

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

public class AdminHomepage {
	private AdminHomepage() {}
	private static Scanner sc = Util.getScanner();
	private static Logger log = Util.getLogger();
	private static boolean doNotLogout = true;
	
	public static void displayHomepage(Admin a) {
		System.out.println("Welcome, " + a.getFirstName());
		showPendingAccounts();
		showMenu(a);
	}
	
	public static void showMenu(Admin a) {
		do {
			doNotLogout = true;
			System.out.println();
			System.out.println("MENU:");
			System.out.println("1 - Approve / deny unnapproved accounts");
			System.out.println("2 - View all bank accounts");
			System.out.println("3 - Look up customer");
			System.out.println("4 - Initiate funds transfer between accounts");
			System.out.println("5 - Logout");
			System.out.println();
			try {
				switch(Integer.parseInt(sc.nextLine())) {
				case 1: reviewAllPendingAccounts(); break;
				case 2: viewAllAccounts(); break;
				case 3: viewCustomerDialog(); break;
				case 4: initTransferDialog(a); break;
				case 5: logout(a); break;
				default: System.out.println("Please choose an option from the menu"); break;
				}
			} catch (NumberFormatException e) {
				System.err.println("Please enter a number");
			}
		} while (doNotLogout);
	}
	
	public static void showPendingAccounts() {
		List<Account> allPending = AccountService.getAllAccountsByStatus(AccountStatus.UNAPPROVED);
		if (allPending.isEmpty())
			System.out.println("There are no currently pending accounts");
		else {
			System.out.println("Here are all currently pending accounts:");
			allPending.forEach(System.out::println);
		}
	}
	
	public static void reviewAllPendingAccounts() {
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
	
	public static void reviewIndividualPendingAccounts(List<Account> allPending) {
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
	
	public static void viewAllAccounts() {
		List<Account> allAccounts = AccountService.getAllAccounts();
		System.out.println("All accounts at CONSOLE BANK:");
		allAccounts.forEach(System.out::println);
	}
	
	public static void viewCustomerDialog() {
		boolean exit = false;
		while(!exit) {
			exit = true;
			System.out.println("Which customer would you like to view details for? Please enter customer username");
			Customer cust = new Customer();
			while (true) {
				String custUsername = sc.nextLine();
				if (custUsername.equals("exit")) break;
				cust = CustomerService.getCustomerByUsername(custUsername);
				if (cust != null) break;
			}
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
			try {
				switch(Integer.parseInt(sc.nextLine())) {
				case 1: CustomerHomepage.editUserInfo(cust); break;
				case 2: approveCustomerAccounts(cust); break;
				case 3: CustomerHomepage.viewAccountDetailsDialog(cust); break;
				case 4: CustomerService.suspendCustomer(cust); break;
				case 5: CustomerService.reauthorizeCustomer(cust); break;
				case 6: exit = false; break;
				case 7: break;
				default: exit = false; break;
				}
			} catch (InputMismatchException e) {
				System.err.println(MessageHolder.numberFormatException);
			}
		}
	}
	
	public static void approveCustomerAccounts(Customer c) {
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
	
	public static void initTransferDialog(Admin admin) {
		System.out.println("Which account would you like to transfer funds FROM?");
		int actFromId = 0;
		Account from = null;
		while (true) {
			try {
				actFromId = Integer.parseInt(sc.nextLine());
				from = AccountService.getAccountById(actFromId);
				break;
			} catch (NumberFormatException nfe) {
				System.err.println(MessageHolder.numberFormatException);
			}
		}
		System.out.println("Which account would you like to transfer funds TO?");
		int actToId = 0;
		Account to;
		while (true) {
			try {
				actToId = Integer.parseInt(sc.nextLine());
				to = AccountService.getAccountById(actToId);
				break;
			} catch (NumberFormatException nfe) {
				System.err.println(MessageHolder.numberFormatException);
			}
		}
		System.out.println("How much would you like to transfer?");
		BigDecimal amt = new BigDecimal(sc.nextLine());
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
	
	public static void logout(Admin a) {
		System.out.println("Logging out now...");
		log.info("Admin " + a.getUsername() + " logged out");
		doNotLogout = false;
	}
	
}
