package com.bank.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
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
import com.bank.util.InputUtil;
import com.bank.util.Util;
import com.bank.view.CustomerHomeView;

public class CustomerHomeController {
	private Scanner sc = Util.getScanner();
	private Logger log = Util.getLogger();
	private boolean doNotLogout = true;
	
	private Customer cust;
	private CustomerHomeView chv = new CustomerHomeView();
	private TransactionController tc = new TransactionController();
	
	public CustomerHomeController(Customer cust) {
		this.cust = cust;
	}
	
	public void displayHomepage() {
		System.out.println("Welcome, " + cust.getFirstName());
		Set<Integer> acctIds = cust.getAccounts();
		if (acctIds == null || acctIds.isEmpty()) {
			System.out.println("Looks like you don't have any accounts set up with us yet.");
			if (InputUtil.getYesOrNo("Would you like to create one now? (y/n)"))
				createNewAccountDialog();
		}
		else {
			listAccounts();
		}
		showMenu();
	}
	
	public void showMenu() {
		do {
			doNotLogout = true;
			int choice = chv.displayRootMenu(cust);
			switch(choice) {
			case 1: showPersonalInfo(); break;
			case 2: listAccounts(); break;
			case 3: viewAccountDetailsDialog(); break;
			case 4: createNewAccountDialog(); break;
			case 5: tc.showUserTransactions(cust); break;
			case 6: logout(); break;
			default: System.out.println("Please choose an option from the menu"); break;
			}
		} while (doNotLogout);
	}
	
	public void listAccounts() {
		Set<Integer> acctIds = cust.getAccounts();
		System.out.println("Here's an overview of your accounts:");
		for (Integer id : acctIds) {
			System.out.println(AccountService.getAccountById(id));
		}
	}
	
	public void viewAccountDetailsDialog() {
		do {
			Set<Integer> acctIds = cust.getAccounts();
			Account acct = null;
			while(true) {
				Account act = InputUtil.getAccountFromUser("Which account would you like to view details for? (enter the account id)");
				if (acctIds.contains(act.getId())) {
					acct = AccountService.getAccountById(act.getId());
					chv.viewAccountDetails(acct);
					break;
				} else {
					System.err.println("Sorry, that's not one of your accounts");
				}
			}
			int choice = chv.displayAccountMenu();
			switch(choice) {
			case 1: accountActionDialog(acct, AccountAction.DEPOSIT); break;
			case 2: accountActionDialog(acct, AccountAction.WITHDRAW); break;
			case 3: accountActionDialog(acct, AccountAction.TRANSFER); break;
			case 4: continue;
			case 5: break;
			default: break;
			}
		} while (false);
	}
	
	public void accountActionDialog(Account acct, AccountAction action) {
		if (!acct.getAcctStatus().equals(AccountStatus.ACTIVE)) {
			System.err.println("Sorry, this account has a status of " +acct.getAcctStatus());
			System.err.println("Therefore, transactions are prohibited on this account");
			log.warn(MessageHolder.getIllegalTransactionString(acct, cust));
			return;
		}
		String s = "";
		switch (action) {
		case DEPOSIT: s = "deposit"; break;
		case WITHDRAW: s = "withdraw"; break;
		case TRANSFER: s = "transfer"; break;
		}
		try {
			BigDecimal amount = InputUtil.getAmountFromUser("How much do you want to "+s+"?");
			if (Util.getNumberOfDecimalPlaces(amount) > 2) {
				System.err.println("Sorry, you cannot enter currency values with more than 2 decimal places");
			} else {
				switch (action) {
				case DEPOSIT:  depositFunds(acct, amount);   break;
				case WITHDRAW: withdrawFunds(acct, amount); break;
				case TRANSFER: transferFunds(acct, amount); break;
				}
				// saving is done in the service methods, no need to explicitly save accounts here
				if (action != AccountAction.TRANSFER) {
					System.out.println("Account #"+acct.getId() + " new balance: " +acct.getCurrency().getSymbol()+acct.getBalance());
				} 
			}
		} catch(BankException e) {
			log.error(MessageHolder.exceptionLogMsg, e);
			System.err.println(e.getMessage());
		}
	}
	
	public void depositFunds(Account acct, BigDecimal amount) throws IllegalDepositException {
		AccountService.depositMoney(amount, acct);
		Transaction tr = new Transaction(AccountAction.DEPOSIT,LocalDateTime.now(), amount, cust, acct);
		TransactionService.saveTransaction(tr);
		log.info(tr);
	}
	
	public void withdrawFunds(Account acct, BigDecimal amount) throws OverdraftException, IllegalWithdrawalException {
		AccountService.withdrawMoney(amount, acct);
		Transaction tr = new Transaction(AccountAction.WITHDRAW,LocalDateTime.now(), amount, cust, acct);
		TransactionService.saveTransaction(tr);
		log.info(tr);
	}
	
	public void transferFunds(Account from, BigDecimal amount) throws BankException {
		System.out.println("Which account would you like to transfer your funds to?");
		int transferAcctId = Integer.parseInt(sc.nextLine());
		Account transferTo = AccountService.getAccountById(transferAcctId);
		AccountService.transferFunds(from, transferTo, amount);
		Transaction tr = new Transaction(LocalDateTime.now(), amount, cust, from, transferTo);
		TransactionService.saveTransaction(tr);
		log.info(tr);
	}
	
	public void showPersonalInfo() {
		chv.displayPersonalInfo(cust);
		boolean choice = InputUtil.getYesOrNo("Would you like to edit any of the above information? (y/n)");
		if (choice)
			editUserInfo();
	}
	
	public void editUserInfo() {
		String[] editLoopArgs = {"first name", "last name", "email", "phone number", "address"};
		for (String s : editLoopArgs) {
			editInputLoop(s);
		}
		CustomerService.saveCustomer(cust);
	}
	
	public void editInputLoop(String option) {
		boolean choice = InputUtil.getYesOrNo("Edit " + option + "? (y/n)");
		if (choice)
			chv.setUserInfo(cust, option);
	}
	
	public void logout() {
		System.out.println("Logging out now...");
		log.info("Customer " + cust.getUsername() + " logged out");
		doNotLogout = false;
	}
	
	public void createNewAccountDialog() {
		Account acct = new Account();
		acct.setAcctStatus(AccountStatus.UNAPPROVED);
		int choice = chv.displayAccountMenu();
		switch (choice) {
		case 1: acct.setAcctType(AccountType.CHECKING); break;
		case 2: acct.setAcctType(AccountType.SAVINGS); break;
		}
		int currencyChoice = chv.displayAccountCurrencyMenu();
		switch (currencyChoice) {
		case 1: break; // USD is already default in the Account class
		case 2: acct.setCurrency(Currency.getInstance("EUR")); break;
		default: break; // USD is already default in the Account class
		}
		System.out.println("What would you like to call this account?");
		String name = sc.nextLine();
		acct.setName(name);
		BigDecimal deposit = InputUtil.getAmountFromUser("Please specify your initial deposit");
		acct.setBalance(deposit);
		System.out.println("Setting up your account...");
		AccountService.registerNewAccount(cust, acct);
		showMenu();
	}
}
