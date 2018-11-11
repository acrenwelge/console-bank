package com.bank;

import java.util.List;

import com.bank.model.Transaction;

public class TransactionView {

	public void showTransactions(List<Transaction> listTransactions) {
		listTransactions.forEach(System.out::println);
		System.out.println();
	}
	
	public void printMenu() {
		System.out.println("What would you like to do now?");
		System.out.println("1 - Filter list by user");
		System.out.println("2 - Filter list by date");
		System.out.println("3 - Filter list by transaction size");
		System.out.println("4 - Filter list by transaction type (deposit/withdrawal/transfer)");
		System.out.println("5 - Filter list by account type (checking/savings)");
		System.out.println("6 - Reset filters");
		System.out.println("7 - Exit");
	}
	
	public void printDateOptions() {
		System.out.println("Do you want to see transactions before or after the date?");
		System.out.println("1 - BEFORE");
		System.out.println("2 - AFTER");
	}
	
	public void printSizeOptions() {
		System.out.println("Do you want to see transactions larger or smaller than the amount?");
		System.out.println("1 - LARGER");
		System.out.println("2 - SMALLER");
	}
	
	public void printTransactionTypeOptions() {
		System.out.println("Which type of transaction do you want to see?");
		System.out.println("1 - DEPOSITS");
		System.out.println("2 - WITHDRAWALS");
		System.out.println("3 - TRANSFERS");
	}
	
	public void printAccountTypeOptions() {
		System.out.println("Which type of account do you want to see transactions for?");
		System.out.println("1 - CHECKING");
		System.out.println("2 - SAVINGS");
	}
}
