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
	private static Scanner sc = Util.getScanner();
	private static Logger log = Util.getFileLogger();
	private static Logger out = Util.getConsoleLogger();
	private boolean doNotLogout = true;
	
	private Customer cust;
	private CustomerHomeView chv;
	private CustomerService custService;
	private TransactionController tc;
	private AccountService actService;
	private TransactionService tService;
	private InputUtil iUtil;
	
	public void setCustomer(Customer cust) {
		this.cust = cust;
	}
	
	public CustomerHomeController(Customer cust, AccountService as, CustomerHomeView chv, 
			TransactionController tc, CustomerService cs, InputUtil iu, TransactionService ts) {
		this.cust = cust;
		this.actService = as;
		this.chv = chv;
		this.tc = tc;
		this.custService = cs;
		this.iUtil = iu;
		this.tService = ts;
	}
	
	public void displayHomepage() {
		out.info("Welcome, " + cust.getFirstName());
		Set<Integer> acctIds = cust.getAccounts();
		if (acctIds == null || acctIds.isEmpty()) {
			out.info("Looks like you don't have any accounts set up with us yet.");
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
			int choice = chv.displayRootMenu();
			switch(choice) {
			case 1: showPersonalInfo(); break;
			case 2: listAccounts(); break;
			case 3: viewAccountDetailsDialog(); break;
			case 4: createNewAccountDialog(); break;
			case 5: tc.showUserTransactions(cust); break;
			case 6: logout(); break;
			default: out.info("Please choose an option from the menu"); break;
			}
		} while (doNotLogout);
	}
	
	public void listAccounts() {
		Set<Integer> acctIds = cust.getAccounts();
		out.info("Here's an overview of your accounts:");
		for (Integer id : acctIds) {
			out.info(actService.getAccountById(id));
		}
	}
	
	public void viewAccountDetailsDialog() {
		do {
			Set<Integer> acctIds = cust.getAccounts();
			Account acct = null;
			while(true) {
				Account act = iUtil.getAccountFromUser("Which account would you like to view details for? (enter the account id)");
				if (acctIds.contains(act.getId())) {
					acct = actService.getAccountById(act.getId());
					chv.viewAccountDetails(acct);
					break;
				} else {
					out.error("Sorry, that's not one of your accounts");
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
			out.error("Sorry, this account has a status of " +acct.getAcctStatus());
			out.error("Therefore, transactions are prohibited on this account");
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
				out.error("Sorry, you cannot enter currency values with more than 2 decimal places");
			} else {
				switch (action) {
				case DEPOSIT:  depositFunds(acct, amount);   break;
				case WITHDRAW: withdrawFunds(acct, amount); break;
				case TRANSFER: transferFunds(acct, amount); break;
				}
				// saving is done in the service methods, no need to explicitly save accounts here
				if (action != AccountAction.TRANSFER) {
					out.info("Account #"+acct.getId() + " new balance: " +acct.getCurrency().getSymbol()+acct.getBalance());
				} 
			}
		} catch(BankException e) {
			log.error(MessageHolder.exceptionLogMsg, e);
			out.error(e.getMessage());
		}
	}
	
	public void depositFunds(Account acct, BigDecimal amount) throws IllegalDepositException {
		actService.depositMoney(amount, acct);
		Transaction tr = new Transaction(AccountAction.DEPOSIT,LocalDateTime.now(), amount, cust, acct);
		tService.saveTransaction(tr);
	}
	
	public void withdrawFunds(Account acct, BigDecimal amount) throws OverdraftException, IllegalWithdrawalException {
		actService.withdrawMoney(amount, acct);
		Transaction tr = new Transaction(AccountAction.WITHDRAW,LocalDateTime.now(), amount, cust, acct);
		tService.saveTransaction(tr);
	}
	
	public void transferFunds(Account from, BigDecimal amount) throws BankException {
		out.info("Which account would you like to transfer your funds to?");
		int transferAcctId = Integer.parseInt(sc.nextLine());
		Account transferTo = actService.getAccountById(transferAcctId);
		actService.transferFunds(from, transferTo, amount);
		Transaction tr = new Transaction(LocalDateTime.now(), amount, cust, from, transferTo);
		tService.saveTransaction(tr);
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
		custService.saveCustomer(cust);
	}
	
	public void editInputLoop(String option) {
		boolean choice = InputUtil.getYesOrNo("Edit " + option + "? (y/n)");
		if (choice)
			chv.setUserInfo(cust, option);
	}
	
	public void logout() {
		out.info("Logging out now...");
		log.info("Customer " + cust.getUsername() + " logged out");
		doNotLogout = false;
	}
	
	public void createNewAccountDialog() {
		Account acct = new Account();
		acct.setAcctStatus(AccountStatus.UNAPPROVED);
		int choice = chv.displayAccountMenu();
		boolean b = true;
		while (b) {
			b = false;
			switch (choice) {
			case 1: acct.setAcctType(AccountType.CHECKING); break;
			case 2: acct.setAcctType(AccountType.SAVINGS); break;
			default: b = true;
			}
		}
		int currencyChoice = chv.displayAccountCurrencyMenu();
		switch (currencyChoice) {
		case 1: break; // USD is already default in the Account class
		case 2: acct.setCurrency(Currency.getInstance("EUR")); break;
		default: break; // USD is already default in the Account class
		}
		out.info("What would you like to call this account?");
		String name = sc.nextLine();
		acct.setName(name);
		BigDecimal deposit = InputUtil.getAmountFromUser("Please specify your initial deposit");
		acct.setBalance(deposit);
		out.info("Setting up your account...");
		actService.registerNewAccount(cust, acct);
		showMenu();
	}
}
