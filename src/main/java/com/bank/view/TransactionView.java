package com.bank.view;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.bank.model.Transaction;
import com.bank.util.Util;

public class TransactionView {
	private static Logger out = Util.getConsoleLogger();

	public void showTransactions(List<Transaction> listTransactions) {
		listTransactions.forEach(out::info);
		out.info("");
	}
	
	public void printMenu() {
		out.info("What would you like to do now?");
		out.info("1 - Filter list by user");
		out.info("2 - Filter list by date");
		out.info("3 - Filter list by transaction size");
		out.info("4 - Filter list by transaction type (deposit/withdrawal/transfer)");
		out.info("5 - Filter list by account type (checking/savings)");
		out.info("6 - Reset filters");
		out.info("7 - Exit");
	}
	
	public void printDateOptions() {
		out.info("Do you want to see transactions before or after the date?");
		out.info("1 - BEFORE");
		out.info("2 - AFTER");
	}
	
	public void printSizeOptions() {
		out.info("Do you want to see transactions larger or smaller than the amount?");
		out.info("1 - LARGER");
		out.info("2 - SMALLER");
	}
	
	public void printTransactionTypeOptions() {
		out.info("Which type of transaction do you want to see?");
		out.info("1 - DEPOSITS");
		out.info("2 - WITHDRAWALS");
		out.info("3 - TRANSFERS");
	}
	
	public void printAccountTypeOptions() {
		out.info("Which type of account do you want to see transactions for?");
		out.info("1 - CHECKING");
		out.info("2 - SAVINGS");
	}
}
