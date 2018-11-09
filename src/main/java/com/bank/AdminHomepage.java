package com.bank;

import static com.bank.serialize.AccountReaderWriter.getAllAccounts;
import static com.bank.serialize.AccountReaderWriter.getAllAccountsByStatus;
import static com.bank.serialize.CustomerReaderWriter.getCustomerByUsername;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.Admin;
import com.bank.model.Customer;
import com.bank.model.MessageHolder;
import com.bank.serialize.AccountReaderWriter;
import com.bank.services.AccountService;
import com.bank.services.CustomerService;

public class AdminHomepage {
	private AdminHomepage() {}
	private static Scanner sc = Util.getScanner();
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
				case 4: initTransferDialog(); break;
				case 5: logout(); break;
				default: System.out.println("Please choose an option from the menu"); break;
				}
			} catch (NumberFormatException e) {
				System.err.println("Please enter a number");
			}
		} while (doNotLogout);
	}
	
	public static void showPendingAccounts() {
		try {
			List<Account> allPending = getAllAccountsByStatus(AccountStatus.UNAPPROVED);
			if (!allPending.isEmpty()) {
				System.out.println("Here are all currently pending accounts:");
				allPending.forEach(System.out::println);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void reviewAllPendingAccounts() {
		try {
			List<Account> allPending = getAllAccountsByStatus(AccountStatus.UNAPPROVED);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void reviewIndividualPendingAccounts(List<Account> allPending) throws IOException {
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
		try {
			List<Account> allAccounts = getAllAccounts();
			System.out.println("All accounts at CONSOLE BANK:");
			for (Account acct: allAccounts) {
				System.out.println(acct);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				try {
					cust = getCustomerByUsername(custUsername);
					break;
				} catch (IOException e) {
					System.err.println("Looks like that username does not exist. Try again or enter 'exit' to quit");
					e.printStackTrace();
				}
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void approveCustomerAccounts(Customer c) throws IOException {
		List<Account> customerPendingAccounts = new ArrayList<>();
		c.getAccounts().forEach((Integer id) -> {
			try {
				Account acct = AccountReaderWriter.getAccountById(id);
				if (acct.getAcctStatus().equals(AccountStatus.UNAPPROVED))
					customerPendingAccounts.add(acct);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		if (customerPendingAccounts.isEmpty())
			System.out.println("Customer has no pending accounts");
		else
			reviewIndividualPendingAccounts(customerPendingAccounts);
	}
	
	public static void initTransferDialog() {
		System.out.println("Which account would you like to transfer funds FROM?");
		int actFromId = 0;
		Account from = null;
		while (true) {
			try {
				actFromId = Integer.parseInt(sc.nextLine());
				from = AccountReaderWriter.getAccountById(actFromId);
				break;
			} catch (NumberFormatException nfe) {
				System.err.println(MessageHolder.numberFormatException);
			} catch (FileNotFoundException fnfe) {
				System.err.println(MessageHolder.actNotFound);
			} catch (IOException ioe) {
				System.err.println(MessageHolder.ioMessage);
			} 
		}
		System.out.println("Which account would you like to transfer funds TO?");
		int actToId = 0;
		Account to;
		while (true) {
			try {
				actToId = Integer.parseInt(sc.nextLine());
				to = AccountReaderWriter.getAccountById(actToId);
				break;
			} catch (NumberFormatException nfe) {
				System.err.println(MessageHolder.numberFormatException);
			} catch(FileNotFoundException fnfe) {
				System.err.println(MessageHolder.actNotFound);
			} catch (IOException e) {
				System.err.println(MessageHolder.ioMessage);
			}
		}
		System.out.println("How much would you like to transfer?");
		BigDecimal amt = new BigDecimal(sc.nextLine());
		try {
			AccountReaderWriter.transferFunds(from, to, amt);
		} catch (IOException e) {
			System.err.println(MessageHolder.ioMessage);
		}
	}
	
	public static void logout() {
		System.out.println("Logging out now...");
		doNotLogout = false;
	}
	
}
