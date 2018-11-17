package com.bank.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.MessageHolder;
import com.bank.services.AccountService;
import com.bank.services.CustomerService;

public class InputUtil {
	private InputUtil() {}
	public static final Scanner sc = Util.getScanner();
	public static final Logger log = Util.getLogger();
	
	public static boolean getYesOrNo(String msg) {
		System.out.println(msg);
		while (true) {
			String s = sc.nextLine();
			if (s.equals("y") || s.equals("yes"))
				return true;
			else if (s.equals("n") || s.equals("no"))
				return false;
			else
				System.err.println("Please enter yes or no (y/n)");
		}
	}
	
	public static int getInteger() {
		while (true) {
			try {
				return Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				System.err.println(MessageHolder.numberFormatException);
			}
		}
	}
	
	public static Account getAccountFromUser(String msg) {
		System.out.println(msg);
		while (true) {
			try {
				int actFromId = Integer.parseInt(sc.nextLine());
				return AccountService.getAccountById(actFromId);
			} catch (NumberFormatException nfe) {
				System.err.println(MessageHolder.numberFormatException);
			}
		}
	}
	
	public static BigDecimal getAmountFromUser(String msg) {
		System.out.println(msg);
		while (true) {
			try {
				BigDecimal amt = new BigDecimal(sc.nextLine());
				int places = Util.getNumberOfDecimalPlaces(amt);
				if (places != 2)
					return new BigDecimal(sc.nextLine());
				else
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				log.error(MessageHolder.exceptionLogMsg, e);
				System.err.println(e.getMessage());
			}
		}
	}
	
	public static Customer getCustomerFromUser(String msg) {
		System.out.println(msg);
		Customer c = null;
		while (true) {
			String custUsername = sc.nextLine();
			if (custUsername.equals("exit")) break;
			c = CustomerService.getCustomerByUsername(custUsername);
			if (c != null) break;
		}
		return c;
	}
	
	public static LocalDate getDateFromUser() {
		while (true) {
			try {
				return LocalDate.parse(sc.nextLine());
			} catch (DateTimeParseException e) {
				System.err.println("Invalid format - try again");
			}
		}
	}
}
