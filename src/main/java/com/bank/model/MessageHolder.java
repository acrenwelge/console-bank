package com.bank.model;

import java.math.BigDecimal;

public class MessageHolder {
	private MessageHolder() {}
	public static String actNotFound = "Sorry, that account does not seem to exist";
	public static String userNotFound = "Sorry, that user does not seem to exist";
	public static String usernameExists = "Sorry, that username already exists. Choose a different one:";
	public static String ioMessage = "Something went wrong with retrieving data. Please try again";
	public static String numberFormatException = "Please enter a valid number";
	public static String invalidCredentials = "Invalid credentials - please try again";
	public static String exceptionLogMsg = "Exception thrown: ";
	
	public static String getIllegalTransactionString(Account a, Customer cust) {
		return "Attempted illegal transaction detected on account #" +a.getId() + " by user " + cust.getUsername();
	}
	
	public static String getTransactionMsg(Account acct, BigDecimal amount, AccountAction action) {
		String s = "";
		String word = "";
		switch(action) {
		case DEPOSIT: {s = "Deposited "; word = "into";}  break;
		case WITHDRAW: {s = "Withdrew "; word = "from";} break;
		case TRANSFER: s = "Transferred "; break;
		}
		return s + acct.getCurrency().getSymbol() + amount + " "+word+" account #" + acct.getId() 
		+ " - new balance is " + acct.getCurrency().getSymbol() + acct.getBalance();
	}
}
