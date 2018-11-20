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
	private static final Scanner sc = Util.getScanner();
	private static final Logger log = Util.getFileLogger();
	private static final Logger out = Util.getConsoleLogger(); 
	private AccountService actService;
	private CustomerService custService;
	
	public InputUtil(AccountService aService, CustomerService custService) { 
		this.custService = custService;
		this.actService = aService; 
	}
	
	public static boolean getYesOrNo(String msg) {
		out.info(msg);
		while (true) {
			String s = sc.nextLine();
			if (s.equals("y") || s.equals("yes"))
				return true;
			else if (s.equals("n") || s.equals("no"))
				return false;
			else
				out.error("Please enter yes or no (y/n)");
		}
	}
	
	public static int getInteger() {
		while (true) {
			try {
				return Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				out.error(MessageHolder.numberFormatException);
			}
		}
	}
	
	public Account getAccountFromUser(String msg) {
		out.info(msg);
		while (true) {
			try {
				int actFromId = Integer.parseInt(sc.nextLine());
				return actService.getAccountById(actFromId);
			} catch (NumberFormatException nfe) {
				out.error(MessageHolder.numberFormatException);
			}
		}
	}
	
	public static BigDecimal getAmountFromUser(String msg) {
		out.info(msg);
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
				out.error(e.getMessage());
			}
		}
	}
	
	public Customer getCustomerFromUser(String msg) {
		out.info(msg);
		Customer c = null;
		while (true) {
			String custUsername = sc.nextLine();
			if (custUsername.equals("exit")) break;
			c = custService.getCustomerByUsername(custUsername);
			if (c != null) break;
		}
		return c;
	}
	
	public static LocalDate getDateFromUser() {
		while (true) {
			try {
				return LocalDate.parse(sc.nextLine());
			} catch (DateTimeParseException e) {
				out.error("Invalid format - try again");
			}
		}
	}
}
