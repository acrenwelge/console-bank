package com.bank.services;

import java.io.IOException;
import java.util.Collection;

import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.serialize.AccountReaderWriter;

public class AccountService {
	private AccountService() {}
	
	/**
	 * Approves the specified account - which allows the owner(s) to conduct transactions on the account 
	 * @param accts
	 * @throws IOException
	 */
	public static void approveAccount(Account acct) throws IOException {
		if (acct.getAcctStatus().equals(AccountStatus.UNAPPROVED)) {
			acct.setAcctStatus(AccountStatus.ACTIVE);
			AccountReaderWriter.saveAccount(acct);
		}
		else
			throw new UnsupportedOperationException("Account must be unapproved in order to approve and change status to active");
	}
	
	/**
	 * Overloaded method to batch approve accounts 
	 * @param accts
	 * @throws IOException
	 */
	public static void approveAccounts(Collection<Account> accts) throws IOException {
		for (Account acct : accts) {
			approveAccount(acct);
		}
	}
	
	/**
	 * Deactivates the specified account
	 * @param acct
	 * @throws IOException
	 */
	public static void deactivateAccount(Account acct) throws IOException {
		acct.setAcctStatus(AccountStatus.INACTIVE);
		AccountReaderWriter.saveAccount(acct);
	}
	
	/**
	 * Overloaded method to batch deactivate accounts 
	 * @param accts
	 * @throws IOException
	 */
	public static void deactivateAccounts(Collection<Account> accts) throws IOException {
		for (Account acct : accts) {
			deactivateAccount(acct);
		}
	}
	
	/**
	 * Suspends the specified account - this means no transactions may be performed on this account
	 * @param acct
	 * @throws IOException
	 */
	public static void suspendAccount(Account acct) throws IOException {
		acct.setAcctStatus(AccountStatus.SUSPENDED);
		AccountReaderWriter.saveAccount(acct);
	}
	
	/**
	 * Overloaded method to batch suspend accounts 
	 * @param accts
	 * @throws IOException
	 */
	public static void suspendAccounts(Collection<Account> accts) throws IOException {
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
	public static void closeAccount(Account acct) throws IOException {
		acct.setAcctStatus(AccountStatus.CLOSED);
		AccountReaderWriter.saveAccount(acct);
	}
	
	/**
	 * Overloaded method to batch close accounts 
	 * @param accts
	 * @throws IOException
	 */
	public static void closeAccounts(Collection<Account> accts) throws IOException {
		for (Account acct : accts) {
			closeAccount(acct);
		}
	}
}
