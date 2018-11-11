package com.bank.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.bank.Util;
import com.bank.model.Account;
import com.bank.model.AccountAction;
import com.bank.model.AccountStatus;
import com.bank.model.Customer;
import com.bank.model.MessageHolder;
import com.bank.model.exception.BankException;
import com.bank.model.exception.IllegalDepositException;
import com.bank.model.exception.IllegalWithdrawalException;
import com.bank.model.exception.OverdraftException;
import com.bank.serialize.AccountReaderWriter;

public class AccountService {
	private static Logger log = Util.getLogger();
	private static int lowBalWarn = 100;
	private AccountService() {}
	
	// Retrieving accounts
	
	public static List<Account> getAllAccounts() {
		log.info("Retrieving all accounts");
		try {
			return AccountReaderWriter.getAllAccounts();
		} catch (IOException e) {
			log.error(MessageHolder.exceptionLogMsg, e);
			System.err.println(MessageHolder.ioMessage);
			return new ArrayList<>();
		}
	}
	
	public static List<Account> getAllAccountsByStatus(AccountStatus as) {
		return Util.catchIOExceptionsReturnList(() -> AccountReaderWriter.getAllAccountsByStatus(as));
	}
	
	public static Account getAccountById(int id) {
		return Util.catchIOExceptionsReturnType(() -> AccountReaderWriter.getAccountById(id));
	}
	
	// Transaction methods
	
	public static void depositMoney(BigDecimal deposit, Account acct) throws IllegalDepositException {
		if (deposit.compareTo(BigDecimal.ZERO) < 0)
			throw new IllegalDepositException("Cannot deposit a negative amount");
		acct.setBalance(acct.getBalance().add(deposit));
		saveAccount(acct);
		log.info(MessageHolder.getTransactionMsg(acct, deposit, AccountAction.DEPOSIT));
	}
	
	public static void withdrawMoney(BigDecimal amount, Account acct) throws OverdraftException, IllegalWithdrawalException {
		if (amount.doubleValue() > acct.getBalance().doubleValue())
			throw new OverdraftException("Overdraft error - you can't withdraw that much");
		else if (amount.doubleValue() < 0)
			throw new IllegalWithdrawalException("Cannot withdraw a negative amount");
		else {
			acct.setBalance(acct.getBalance().subtract(amount));
			saveAccount(acct);
			log.info(MessageHolder.getTransactionMsg(acct, amount, AccountAction.WITHDRAW));
			if (acct.getBalance().compareTo(new BigDecimal(lowBalWarn)) < 0) {
				String s = "Account #"+acct.getId()+ " has a low balance of " + acct.getCurrency().getSymbol()+ acct.getBalance(); 
				log.warn(s);
				System.err.println("WARNING: "+s);
			}
		}
	}
	
	public static void transferFunds(Account from, Account to, BigDecimal amount) throws BankException {
		if (from.getCurrency().equals(to.getCurrency())) {
			withdrawMoney(amount, from);
			depositMoney(amount, to);
			saveAccount(from);
			saveAccount(to);
			System.out.println("Account #"+from.getId() + " new balance: "+from.getCurrency().getSymbol() + from.getBalance());
			System.out.println("Account #"+to.getId() + " new balance: " +to.getCurrency().getSymbol()+ to.getBalance());
			log.info("Funds transferred from Account #" + from.getId()
				+ " to Account #" + to.getId()
				+ " - amount: " + from.getCurrency().getSymbol()+ amount);
		} else {
			// TODO: implement conflicting currency solution
		}
	}
	
	public static void saveAccount(Account acct) {
		Util.catchIOExceptionsVoid(() -> AccountReaderWriter.saveAccount(acct));		
	}
	
	public static void registerNewAccount(Customer cust, Account acct) {
		Util.catchIOExceptionsVoid(() -> AccountReaderWriter.registerNewAccount(cust, acct));
	}
	
	/**
	 * Approves the specified account - which allows the owner(s) to conduct transactions on the account 
	 * @param accts
	 * @throws IOException
	 */
	public static void approveAccount(Account acct) {
		if (acct.getAcctStatus().equals(AccountStatus.UNAPPROVED)) {
			acct.setAcctStatus(AccountStatus.ACTIVE);
			saveAccount(acct);
			log.info("Account #" + acct.getId() + " approved");
		}
		else
			throw new UnsupportedOperationException("Account must be unapproved in order to approve and change status to active");
	}
	
	/**
	 * Overloaded method to batch approve accounts 
	 * @param accts
	 * @throws IOException
	 */
	public static void approveAccounts(Collection<Account> accts) {
		for (Account acct : accts) {
			approveAccount(acct);
		}
	}
	
	/**
	 * Deactivates the specified account
	 * @param acct
	 * @throws IOException
	 */
	public static void deactivateAccount(Account acct) {
		acct.setAcctStatus(AccountStatus.INACTIVE);
		saveAccount(acct);
		log.info("Account #" + acct.getId() + " deactivated");
	}
	
	/**
	 * Overloaded method to batch deactivate accounts 
	 * @param accts
	 * @throws IOException
	 */
	public static void deactivateAccounts(Collection<Account> accts) {
		for (Account acct : accts) {
			deactivateAccount(acct);
		}
	}
	
	/**
	 * Suspends the specified account - this means no transactions may be performed on this account
	 * @param acct
	 * @throws IOException
	 */
	public static void suspendAccount(Account acct) {
		acct.setAcctStatus(AccountStatus.SUSPENDED);
		saveAccount(acct);
		log.info("Account #"+acct.getId() + " suspended");
	}
	
	/**
	 * Overloaded method to batch suspend accounts 
	 * @param accts
	 * @throws IOException
	 */
	public static void suspendAccounts(Collection<Account> accts) {
		for (Account acct : accts) {
			suspendAccount(acct);
		}
	}
	
	/**
	 * Closes the specified account - the account is retained in file or the database, but status is 
	 * changed to CLOSED and the account will no longer appear to its user(s) in the system. 
	 * @param acct
	 * @throws IOException
	 */
	public static void closeAccount(Account acct) {
		acct.setAcctStatus(AccountStatus.CLOSED);
		saveAccount(acct);
		log.info("Account #"+acct.getId() + " closed");
	}
	
	/**
	 * Overloaded method to batch close accounts 
	 * @param accts
	 * @throws IOException
	 */
	public static void closeAccounts(Collection<Account> accts) {
		for (Account acct : accts) {
			closeAccount(acct);
		}
	}
}
