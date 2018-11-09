package com.bank;

import static com.bank.serialize.AccountReaderWriter.getAccountById;
import static com.bank.serialize.CustomerReaderWriter.saveCustomer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

import com.bank.model.Account;
import com.bank.model.AccountAction;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.Customer;
import com.bank.serialize.AccountReaderWriter;

public class CustomerHomepage {
	private CustomerHomepage() {}
	private static Scanner sc = Util.getScanner();
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
				case 5: logout(); break;
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
			try {
				System.out.println(getAccountById(id));
			} catch (IOException e) {
				System.err.println("Something went wrong - unable to fetch your accounts");
				e.printStackTrace();
			}
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
					try {
						acct = getAccountById(acctId);
						System.out.println("Account details:");
						System.out.println("Name: \t\t" + acct.getName());
						System.out.println("Type: \t\t" + acct.getAcctType());
						System.out.println("Status: \t" + acct.getAcctStatus());
						System.out.println("Balance: \t"+acct.getCurrency().getSymbol()+acct.getBalance());
						System.out.println("Date created: \t"+acct.getCreationDate());
						System.out.println();
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}	
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
				}
			} catch (InputMismatchException e) {
				System.err.println("Please enter a number from the menu above");
			}
		} while (false);
	}
	
	public static void accountActionDialog(Customer c, Account acct, AccountAction aa) {
		do {
			if (!acct.getAcctStatus().equals(AccountStatus.ACTIVE)) {
				System.err.println("Sorry, this account has a status of " +acct.getAcctStatus());
				System.err.println("Therefore, deposits are prohibited from this account");
				break;
			}
			String s = "";
			switch (aa) {
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
					switch (aa) {
					case DEPOSIT: acct.depositMoney(amount); break;
					case WITHDRAW: acct.withdrawMoney(amount); break;
					case TRANSFER: {
						System.out.println("Which account would you like to transfer your funds to?");
						int transferAcctId = Integer.parseInt(sc.nextLine());
						Account transferTo = getAccountById(transferAcctId);
						AccountReaderWriter.transferFunds(acct, transferTo, amount);
						} break;
					}
					AccountReaderWriter.saveAccount(acct);
					if (aa != AccountAction.TRANSFER) {
						System.out.println("Account #"+acct.getId() + " new balance: " +acct.getCurrency().getSymbol()+acct.getBalance());
						System.out.println("Account successfully saved, returning to main menu");
					} else {
						System.out.println("Accounts successfully saved, returning to main menu");
					}
					
				}
			} catch (NumberFormatException e) {
				System.err.println("Please enter a number");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (false);
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
		try {
			saveCustomer(c);
		} catch (IOException e) {
			System.err.println("There was a problem saving your profile information :(");
			e.printStackTrace();
		}
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
					}; break;
				}
				break;
			} else if (s.equals("no") || s.equals("n")) {break;}
			else { System.err.println("Please input either yes or no (y/n)"); }
		}
	}
	
	public static void logout() {
		System.out.println("Logging out now...");
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
		System.out.println("Please choose from CONSOLE BANK supported currencies:");
		System.out.println("1 - US Dollars");
		System.out.println("2 - Euros");
		while (true) {
			try {
				int option = Integer.parseInt(sc.nextLine());
				switch (option) {
				case 1: break; // USD is already default
				case 2: acct.setCurrency(Currency.getInstance("EUR")); break;
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
		try {
			AccountReaderWriter.registerNewAccount(c, acct);
		} catch (IOException e) {
			System.err.println("Sorry, unable to create your account :(");
			e.printStackTrace();
		}
		showMenu(c);
	}
	
}
