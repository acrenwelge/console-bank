package com.bank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.bank.model.AccountAction;
import com.bank.model.AccountType;
import com.bank.model.Transaction;
import com.bank.services.TransactionService;

public class TransactionController {
	private static TransactionView view = new TransactionView();
	private static List<Transaction> transactions = new ArrayList<>();
	private static Scanner sc = Util.getScanner();
	private static boolean exit = false;
	
	public static void main(String[] args) {
		transactions = TransactionService.getAllTransactions();
		System.out.println("Here are all the transactions:");
		while (!exit) {
			System.out.println(transactions.size() + " transactions found:");
			System.out.println();
			view.showTransactions(transactions);
			view.printMenu();
			getInput();
		}
	}
	
	public static void getInput() {
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
	
	public static void resetFilters() {
		transactions = TransactionService.getAllTransactions();
	}
	
	public static void determineUserFilter() {
		System.out.println("Enter the username to filter by:");
		String username = sc.nextLine();
		transactions = TransactionService.filterByUser(transactions, username);
	}
	
	public static void determineDateFilter() {
		System.out.println("Enter the date to filter by:");
		LocalDate date = LocalDate.parse(sc.nextLine());
		view.printDateOptions();
		Integer choice = Integer.parseInt(sc.nextLine());
		if (choice.equals(1))
			transactions = TransactionService.filterBeforeDate(transactions, date);
		else if (choice.equals(2))
			transactions = TransactionService.filterAfterDate(transactions, date);
	}
	
	public static void determineSizeFilter() {
		System.out.println("Enter size to filter by:");
		BigDecimal size = new BigDecimal(sc.nextLine());
		view.printSizeOptions();
		Integer choice = Integer.parseInt(sc.nextLine());
		if (choice.equals(1))
			transactions = TransactionService.filterBiggerThan(transactions, size);
		else if (choice.equals(2))
			transactions = TransactionService.filterSmallerThan(transactions, size);
	}
	
	public static void determineTransactionTypeFilter() {
		view.printTransactionTypeOptions();
		Integer choice = Integer.parseInt(sc.nextLine());
		AccountAction type = null;
		switch (choice) {
		case 1: type = AccountAction.DEPOSIT; break;
		case 2: type = AccountAction.WITHDRAW; break;
		case 3: type = AccountAction.TRANSFER; break;
		}
		transactions = TransactionService.filterByTransactionType(transactions, type);
	}
	
	public static void determineAccountTypeFilter() {
		view.printAccountTypeOptions();
		Integer choice = Integer.parseInt(sc.nextLine());
		AccountType type = null;
		switch (choice) {
		case 1: type = AccountType.CHECKING; break;
		case 2: type = AccountType.SAVINGS; break;
		}
		transactions = TransactionService.filterByAccountType(transactions, type);
	}
	
}
