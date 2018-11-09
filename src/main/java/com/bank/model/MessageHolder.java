package com.bank.model;

public class MessageHolder {
	private MessageHolder() {}
	public static String actNotFound = "Sorry, that account does not seem to exist";
	public static String userNotFound = "Sorry, that user does not seem to exist";
	public static String usernameExists = "Sorry, that username already exists. Choose a different one:";
	public static String ioMessage = "Something went wrong with retrieving data. Please try again";
	public static String numberFormatException = "Please enter a valid number";
	public static String invalidCredentials = "Invalid credentials - please try again";
}
