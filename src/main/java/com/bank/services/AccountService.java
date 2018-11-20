package com.bank.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;

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
import com.bank.util.Util;

public class AccountService {
	private static Logger log = Util.getFileLogger();
	private static Logger out = Util.getConsoleLogger();
	private static final int LOW_BAL_WARN = 100;
	private static final String ACT_PREFIX = "Account #";
	private AccountReaderWriter arw;
	
	public AccountService(AccountReaderWriter arw) {
		this.arw = arw;
	}
	
	
	// Retrieving accounts
	
	public List<Account> getAllAccounts() {
		log.info("Retrieving all accounts");
		try {
			return arw.getAllAccounts();
		} catch (IOException e) {
			log.error(MessageHolder.exceptionLogMsg, e);
			out.error(MessageHolder.ioMessage);
			return new ArrayList<>();
		}
	}
	
	public List<Account> getAllAccountsByStatus(AccountStatus as) {
		return Util.catchIOExceptionsReturnList(() -> arw.getAllAccountsByStatus(as));
	}
	
	public Account getAccountById(int id) {
		return Util.catchIOExceptionsReturnType(() -> arw.getAccountById(id));
	}
	
	// Transaction methods
	
	public void depositMoney(BigDecimal deposit, Account acct) throws IllegalDepositException {
		if (deposit.compareTo(BigDecimal.ZERO) < 0)
			throw new IllegalDepositException("Cannot deposit a negative amount");
		acct.setBalance(acct.getBalance().add(deposit));
		saveAccount(acct);
		log.info(MessageHolder.getTransactionMsg(acct, deposit, AccountAction.DEPOSIT));
	}
	
	public void withdrawMoney(BigDecimal amount, Account acct) throws OverdraftException, IllegalWithdrawalException {
		if (amount.doubleValue() > acct.getBalance().doubleValue())
			throw new OverdraftException("Overdraft error - you can't withdraw that much");
		else if (amount.doubleValue() < 0)
			throw new IllegalWithdrawalException("Cannot withdraw a negative amount");
		else {
			acct.setBalance(acct.getBalance().subtract(amount));
			saveAccount(acct);
			log.info(MessageHolder.getTransactionMsg(acct, amount, AccountAction.WITHDRAW));
			if (acct.getBalance().compareTo(new BigDecimal(LOW_BAL_WARN)) < 0) {
				String s = ACT_PREFIX+acct.getId()+ " has a low balance of " + acct.getCurrency().getSymbol()+ acct.getBalance(); 
				log.warn(s);
				out.error("WARNING: "+s);
			}
		}
	}
	
	public void transferFunds(Account from, Account to, BigDecimal amount) throws BankException {
		if (from.getCurrency().equals(to.getCurrency())) {
			withdrawMoney(amount, from);
			depositMoney(amount, to);
			saveAccount(from);
			saveAccount(to);
			out.info(ACT_PREFIX+from.getId() + " new balance: "+from.getCurrency().getSymbol() + from.getBalance());
			out.info(ACT_PREFIX+to.getId() + " new balance: " +to.getCurrency().getSymbol()+ to.getBalance());
			log.info("Funds transferred from Account #" + from.getId()
				+ " to Account #" + to.getId()
				+ " - amount: " + from.getCurrency().getSymbol()+ amount);
		} else {
			// TODO: implement conflicting currency solution
		}
	}
	
	public void saveAccount(Account acct) {
		Util.catchIOExceptionsReturnVoid(() -> arw.saveAccount(acct));		
	}
	
	public void registerNewAccount(Customer cust, Account acct) {
		Util.catchIOExceptionsReturnVoid(() -> arw.registerNewAccount(cust, acct));
	}
	
	/**
	 * Approves the specified account - which allows the owner(s) to conduct transactions on the account 
	 * @param accts
	 * @throws IOException
	 */
	public void approveAccount(Account acct) {
		if (acct.getAcctStatus().equals(AccountStatus.UNAPPROVED)) {
			acct.setAcctStatus(AccountStatus.ACTIVE);
			saveAccount(acct);
			log.info(ACT_PREFIX + acct.getId() + " approved");
		}
		else
			throw new UnsupportedOperationException("Account must be unapproved in order to approve and change status to active");
	}
	
	/**
	 * Overloaded method to batch approve accounts 
	 * @param accts
	 * @throws IOException
	 */
	public void approveAccounts(Collection<Account> accts) {
		for (Account acct : accts) {
			approveAccount(acct);
		}
	}
	
	/**
	 * Deactivates the specified account
	 * @param acct
	 * @throws IOException
	 */
	public void deactivateAccount(Account acct) {
		acct.setAcctStatus(AccountStatus.INACTIVE);
		saveAccount(acct);
		log.info(ACT_PREFIX + acct.getId() + " deactivated");
	}
	
	/**
	 * Overloaded method to batch deactivate accounts 
	 * @param accts
	 * @throws IOException
	 */
	public void deactivateAccounts(Collection<Account> accts) {
		for (Account acct : accts) {
			deactivateAccount(acct);
		}
	}
	
	/**
	 * Suspends the specified account - this means no transactions may be performed on this account
	 * @param acct
	 * @throws IOException
	 */
	public void suspendAccount(Account acct) {
		acct.setAcctStatus(AccountStatus.SUSPENDED);
		saveAccount(acct);
		log.info(ACT_PREFIX+acct.getId() + " suspended");
	}
	
	/**
	 * Overloaded method to batch suspend accounts 
	 * @param accts
	 * @throws IOException
	 */
	public void suspendAccounts(Collection<Account> accts) {
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
	public void closeAccount(Account acct) {
		acct.setAcctStatus(AccountStatus.CLOSED);
		saveAccount(acct);
		log.info(ACT_PREFIX+acct.getId() + " closed");
	}
	
	/**
	 * Overloaded method to batch close accounts 
	 * @param accts
	 * @throws IOException
	 */
	public void closeAccounts(Collection<Account> accts) {
		for (Account acct : accts) {
			closeAccount(acct);
		}
	}
}
