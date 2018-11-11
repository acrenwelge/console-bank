package com.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.bank.model.Account;
import com.bank.model.AccountAction;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.Customer;
import com.bank.model.MessageHolder;
import com.bank.model.Transaction;
import com.bank.model.exception.BankException;
import com.bank.model.exception.IllegalDepositException;
import com.bank.model.exception.IllegalWithdrawalException;
import com.bank.model.exception.OverdraftException;
import com.bank.services.AccountService;
import com.bank.services.CustomerService;
import com.bank.services.TransactionService;

public class CustomerHomepage {
	private CustomerHomepage() {}
	private static Scanner sc = Util.getScanner();
	private static Logger log = Util.getLogger();
	private static boolean doNotLogout = true;
	
	public static void displayHomepage(Customer c) {
		System.out.println("Welcome, " + c.getFirstName());
		Set<Integer> acctIds = c.getAccounts();
		if (acctIds == null || acctIds.isEmpty()) {
			System.out.println("Looks like you don't have any accounts set up with us yet.");
			System.out.println("Would you like to create one now? (y/n)");
			while(true) {
				String s = sc.nextLine();
				if (s.equals("y") || s.equals("yes")) {
					createNewAccountDialog(c);
					break;
				} else if (s.equals("n") || s.equals("no")) {
					break;
				}
				else {
					System.err.println("Invalid input - please enter yes or no (y/n)");
				}
			}
		}
		else {
			listAccounts(c);
		}
		showMenu(c);
	}
	
	public static void showMenu(Customer c) {
		do {
			doNotLogout = true;
			System.out.println();
			System.out.println("MENU:");
			System.out.println("1 - View / edit personal information");
			System.out.println("2 - View summary of accounts");
			System.out.println("3 - View account details");
			System.out.println("4 - Create new account");
			System.out.println("5 - Logout");
			System.out.println();
			try {
				switch(Integer.parseInt(sc.nextLine())) {
				case 1: showPersonalInfo(c); break;
				case 2: listAccounts(c); break;
				case 3: viewAccountDetailsDialog(c); break;
				case 4: createNewAccountDialog(c); break;
				case 5: logout(c); break;
				default: System.out.println("Please choose an option from the menu"); break;
				}
			} catch (NumberFormatException e) {
				System.err.println("Please enter a number");
			}
		} while (doNotLogout);
	}
	
	public static void listAccounts(Customer c) {
		Set<Integer> acctIds = c.getAccounts();
		System.out.println("Here's an overview of your accounts:");
		for (Integer id : acctIds) {
			System.out.println(AccountService.getAccountById(id));
		}
	}
	
	public static void viewAccountDetailsDialog(Customer c) {
		do {
			Set<Integer> acctIds = c.getAccounts();
			Account acct = null;
			while(true) {
				System.out.println("Which account would you like to view details for? (enter the account id)");
				int acctId = 0;
				try {
					acctId = Integer.parseInt(sc.nextLine());
				} catch (NumberFormatException e) {
					System.err.println("You must enter a number");
					continue;
				}
				if (acctIds.contains(acctId)) {
					acct = AccountService.getAccountById(acctId);
					System.out.println("Account details:");
					System.out.println("Name: \t\t" + acct.getName());
					System.out.println("Type: \t\t" + acct.getAcctType());
					System.out.println("Status: \t" + acct.getAcctStatus());
					System.out.println("Balance: \t"+acct.getCurrency().getSymbol()+acct.getBalance());
					System.out.println("Date created: \t"+acct.getCreationDate());
					System.out.println();
					break;
				} else {
					System.err.println("Sorry, that's not one of your accounts");
				}
			}
			System.out.println("Now what would you like to do?");
			System.out.println("1 - DEPOSIT to this account");
			System.out.println("2 - WITHDRAW from this account");
			System.out.println("3 - TRANSFER funds");
			System.out.println("4 - View another account");
			System.out.println("5 - Go back");
			try {
				switch(Integer.parseInt(sc.nextLine())) {
				case 1: accountActionDialog(c, acct, AccountAction.DEPOSIT); break;
				case 2: accountActionDialog(c, acct, AccountAction.WITHDRAW); break;
				case 3: accountActionDialog(c, acct, AccountAction.TRANSFER); break;
				case 4: continue;
				case 5: break;
				default: break;
				}
			} catch (InputMismatchException e) {
				System.err.println("Please enter a number from the menu above");
			}
		} while (false);
	}
	
	public static void accountActionDialog(Customer c, Account acct, AccountAction action) {
		do {
			if (!acct.getAcctStatus().equals(AccountStatus.ACTIVE)) {
				System.err.println("Sorry, this account has a status of " +acct.getAcctStatus());
				System.err.println("Therefore, transactions are prohibited on this account");
				log.warn(MessageHolder.getIllegalTransactionString(acct, c));
				break;
			}
			String s = "";
			switch (action) {
			case DEPOSIT: s = "deposit"; break;
			case WITHDRAW: s = "withdraw"; break;
			case TRANSFER: s = "transfer"; break;
			}
			System.out.println("How much do you want to "+s+"?");
			try {
				BigDecimal amount = new BigDecimal(sc.nextLine());
				if (Util.getNumberOfDecimalPlaces(amount) > 2) {
					System.err.println("Sorry, you cannot enter currency values with more than 2 decimal places");
				} else {
					switch (action) {
					case DEPOSIT:  depositFunds(c,acct, amount);   break;
					case WITHDRAW: withdrawFunds(c, acct, amount); break;
					case TRANSFER: transferFunds(c, acct, amount); break;
					}
					// saving is done in the service methods, no need to explicitly save accounts here
					if (action != AccountAction.TRANSFER) {
						System.out.println("Account #"+acct.getId() + " new balance: " +acct.getCurrency().getSymbol()+acct.getBalance());
					}
				}
			} catch (NumberFormatException e) {
				System.err.println("Please enter a number");
			} catch(BankException e) {
				log.error(MessageHolder.exceptionLogMsg, e);
				System.err.println(e.getMessage());
			}
		} while (false);
	}
	
	public static void depositFunds(Customer c, Account acct, BigDecimal amount) throws IllegalDepositException {
		AccountService.depositMoney(amount, acct);
		Transaction tr = new Transaction(AccountAction.DEPOSIT,LocalDateTime.now(), amount, c, acct);
		TransactionService.saveTransaction(tr);
		log.info(tr);
	}
	
	public static void withdrawFunds(Customer c, Account acct, BigDecimal amount) throws OverdraftException, IllegalWithdrawalException {
		AccountService.withdrawMoney(amount, acct);
		Transaction tr = new Transaction(AccountAction.WITHDRAW,LocalDateTime.now(), amount, c, acct);
		TransactionService.saveTransaction(tr);
		log.info(tr);
	}
	
	public static void transferFunds(Customer c, Account from, BigDecimal amount) throws BankException {
		System.out.println("Which account would you like to transfer your funds to?");
		int transferAcctId = Integer.parseInt(sc.nextLine());
		Account transferTo = AccountService.getAccountById(transferAcctId);
		AccountService.transferFunds(from, transferTo, amount);
		Transaction tr = new Transaction(LocalDateTime.now(), amount, c, from, transferTo);
		TransactionService.saveTransaction(tr);
		log.info(tr);
	}
	
	public static void showPersonalInfo(Customer c) {
		System.out.println("Your profile:");
		System.out.println("Customer ID: "+c.getId());
		System.out.println("First name: " + c.getFirstName());
		System.out.println("Last name: " + c.getLastName());
		System.out.println("Email: "+c.getEmail());
		System.out.println("Phone Number: "+c.getPhoneNumber());
		System.out.println("Birth date:"+c.getDob());
		System.out.println("Address: "+c.getAddress());
		System.out.println();
		System.out.println("Would you like to edit any of the above information? (y/n)");
		while(true) {
			String s = sc.nextLine();
			if (s.equals("y") || s.equals("yes")) {
				editUserInfo(c);
				break;
			} else if (s.equals("n") || s.equals("no")) {
				break;
			}
			else {
				System.err.println("Invalid input - please enter yes or no (y/n)");
			}
		}
	}
	
	public static void editUserInfo(Customer c) {
		String[] editLoopArgs = {"first name", "last name", "email", "phone number", "address"};
		for (String s : editLoopArgs) {
			editInputLoop(c, s);
		}
		CustomerService.saveCustomer(c);
	}
	
	public static void editInputLoop(Customer c, String option) {
		while (true) {
			System.out.println("Edit " + option + "? (y/n)");
			String s = sc.nextLine();
			if (s.equals("yes") || s.equals("y")) {
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
			} else if (s.equals("no") || s.equals("n")) {break;} // if the user chooses not to edit the field, we simply break out of the loop
			else { System.err.println("Please input either yes or no (y/n)"); }
		}
	}
	
	public static void logout(Customer c) {
		System.out.println("Logging out now...");
		log.info("Customer " + c.getUsername() + " logged out");
		doNotLogout = false;
	}
	
	public static void createNewAccountDialog(Customer c) {
		System.out.println("Let's create a new account for you");
		Account acct = new Account();
		acct.setAcctStatus(AccountStatus.UNAPPROVED);
		System.out.println("What kind of account would you like to create?");
		System.out.println("1 - Checking");
		System.out.println("2 - Savings");
		while (true) {
			try {
				int option = Integer.parseInt(sc.nextLine());
				switch (option) {
				case 1: acct.setAcctType(AccountType.CHECKING); break;
				case 2: acct.setAcctType(AccountType.SAVINGS); break;
				}
				break;
			} catch (InputMismatchException e) {
				System.err.println("Please select a number from the menu");
			}
		}
		System.out.println("What currency should this account be denominated in?");
		System.out.println("Please choose from CONSOLE BANK supported currencies: (default is USD)");
		System.out.println("1 - US Dollars");
		System.out.println("2 - Euros");
		while (true) {
			try {
				int option = Integer.parseInt(sc.nextLine());
				switch (option) {
				case 1: break; // USD is already default in the Account class
				case 2: acct.setCurrency(Currency.getInstance("EUR")); break;
				default: break; // USD is already default in the Account class
				}
				break;
			} catch (InputMismatchException e) {
				System.err.println("Please select a number from the menu");
			}
		}
		System.out.println("What would you like to call this account?");
		String name = sc.nextLine();
		acct.setName(name);
		System.out.println("Please specify your initial deposit");
		BigDecimal deposit = new BigDecimal(sc.nextLine());
		acct.setBalance(deposit);
		System.out.println("Setting up your account...");
		AccountService.registerNewAccount(c, acct);
		showMenu(c);
	}
}
