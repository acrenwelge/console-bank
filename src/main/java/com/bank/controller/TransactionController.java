package com.bank.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

import com.bank.model.AccountAction;
import com.bank.model.AccountType;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.services.TransactionService;
import com.bank.util.Util;
import com.bank.view.TransactionView;

public class TransactionController {
	private TransactionView view = new TransactionView();
	private List<Transaction> transactions = new ArrayList<>();
	private static Scanner sc = Util.getScanner();
	private static Logger out = Util.getConsoleLogger();
	private boolean exit = false;
	
	private TransactionService tService;
	
	public TransactionController(TransactionService ts) {
		this.tService = ts;
	}
	
	/**
	 * For admins - to see all transactions
	 */
	public void showAllTransactions() {
		transactions = tService.getAllTransactions();
		out.info("Here are all the transactions:");
		while (!exit) {
			out.info(transactions.size() + " transactions found:");
			out.info("");
			view.showTransactions(transactions);
			view.printMenu();
			getInput();
		}
	}
	
	/**
	 * For customers - to see only their own transactions
	 */
	public void showUserTransactions(User u) {
		transactions = tService.getTransactionsByUsername(u.getUsername());
		out.info("Here are all your transactions:");
		while (!exit) {
			out.info(transactions.size() + " transactions found:");
			out.info("");
			view.showTransactions(transactions);
			view.printMenu();
			getInput();
		}
	}
	
	public void getInput() {
		Integer choice = Integer.parseInt(sc.nextLine());
		switch(choice) {
		case 1: determineUserFilter(); break;
		case 2: determineDateFilter(); break;
		case 3: determineSizeFilter(); break;
		case 4: determineTransactionTypeFilter(); break;
		case 5: determineAccountTypeFilter(); break;
		case 6: resetFilters(); break;
		default: exit = true; break;
		}
	}
	
	public void resetFilters() {
		transactions = tService.getAllTransactions();
	}
	
	public void determineUserFilter() {
		out.info("Enter the username to filter by:");
		String username = sc.nextLine();
		transactions = TransactionService.filterByUser(transactions, username);
	}
	
	public void determineDateFilter() {
		out.info("Enter the date to filter by:");
		LocalDate date = LocalDate.parse(sc.nextLine());
		view.printDateOptions();
		Integer choice = Integer.parseInt(sc.nextLine());
		if (choice.equals(1))
			transactions = TransactionService.filterBeforeDate(transactions, date);
		else if (choice.equals(2))
			transactions = TransactionService.filterAfterDate(transactions, date);
	}
	
	public void determineSizeFilter() {
		out.info("Enter size to filter by:");
		BigDecimal size = new BigDecimal(sc.nextLine());
		view.printSizeOptions();
		Integer choice = Integer.parseInt(sc.nextLine());
		if (choice.equals(1))
			transactions = TransactionService.filterBiggerThan(transactions, size);
		else if (choice.equals(2))
			transactions = TransactionService.filterSmallerThan(transactions, size);
	}
	
	public void determineTransactionTypeFilter() {
		view.printTransactionTypeOptions();
		AccountAction type = null;
		boolean b = true;
		while(b) {
			b = false; // exit by default, unless unavailable option selected
			Integer choice = Integer.parseInt(sc.nextLine());
			switch (choice) {
			case 1: type = AccountAction.DEPOSIT; break;
			case 2: type = AccountAction.WITHDRAW; break;
			case 3: type = AccountAction.TRANSFER; break;
			default: b = true; 
			}
		}
		transactions = TransactionService.filterByTransactionType(transactions, type);
	}
	
	public void determineAccountTypeFilter() {
		view.printAccountTypeOptions();
		Integer choice = Integer.parseInt(sc.nextLine());
		AccountType type = null;
		boolean b = true;
		while(b) {
			b = false; // exit by default, unless unavailable option selected
			switch (choice) {
			case 1: type = AccountType.CHECKING; break;
			case 2: type = AccountType.SAVINGS; break;
			default: b = true;
			}			
		}
		transactions = TransactionService.filterByAccountType(transactions, type);
	}
	
}
