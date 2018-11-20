package com.bank.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bank.model.Account;
import com.bank.model.exception.BankException;
import com.bank.model.exception.IllegalDepositException;
import com.bank.model.exception.IllegalWithdrawalException;
import com.bank.model.exception.OverdraftException;
import com.bank.serialize.AccountReaderWriter;

public class TestAccountService {
	
	// accounts to use for testing
	Account acct1;
	Account acct2;
	
	@Mock
	private static Logger log;
	
	@Mock
	private static AccountReaderWriter arw;
	
	@InjectMocks
	private AccountService service;

	@Before
	public void setUp() throws Exception {
		acct1 = new Account();
		acct2 = new Account();
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

//	 Testing that the .depositMoney() method:
//	 1. Changes the account balance correctly for a valid deposit
//	 2. Calls the .saveAccount() method on the AccountReaderWriter
//	 3. Throws an IllegalDepositException when the deposit is a negative amount
	@Test
	public void testDeposit() throws BankException {
		// start from default - $0
		service.depositMoney(new BigDecimal("100"), acct1);
		assertEquals(acct1.getBalance(), BigDecimal.valueOf(100));
		service.depositMoney(BigDecimal.valueOf(45.23), acct1);
		assertEquals(acct1.getBalance(), BigDecimal.valueOf(145.23));
	}
	
	@Test(expected=IllegalDepositException.class)
	public void testDeposit_ThrowsException() throws IllegalDepositException {
		BigDecimal deposit = new BigDecimal(-6);
		service.depositMoney(deposit, acct1);
	}
	
	// Testing that the .withdrawMoney() method:
	// 1. Changes the account balance correctly
	// 2. Throws an IllegalWithdrawalException when a negative amount is specified
	// 3. Throws an OverdraftException when the amount is greater than the account balance
	@Test
	public void testWithdraw() throws BankException {
		acct1.setBalance(BigDecimal.valueOf(100.01));
		service.withdrawMoney(BigDecimal.valueOf(50.01), acct1);
		assertTrue(acct1.getBalance().compareTo(BigDecimal.valueOf(50.00)) == 0);
	}
	
	@Test(expected=IllegalWithdrawalException.class)
	public void testWithdraw_Negative() throws BankException {
		acct1.setBalance(BigDecimal.valueOf(100.01));
		service.withdrawMoney(BigDecimal.valueOf(-43.51), acct1);
	}
	
	@Test(expected=OverdraftException.class)
	public void testOverdraft() throws BankException {
		acct1.setBalance(BigDecimal.valueOf(100.01));
		service.withdrawMoney(BigDecimal.valueOf(101.01), acct1);
	}
	
	// Testing that the .transferFunds() method:
	// 1. Adjusts the balance of both accounts accurately
	// 2. Throws a BankException if the amount to transfer is 
	//   (a) negative or 
	//   (b) greater than the balance of the account transferring from 
	@Test
	public void testTransfer() throws BankException {
		acct1.setBalance(BigDecimal.valueOf(39.93));
		acct2.setBalance(BigDecimal.valueOf(24.41));
		service.transferFunds(acct1, acct2, BigDecimal.valueOf(10.49));
		assertTrue(acct1.getBalance().compareTo(BigDecimal.valueOf(29.44)) == 0);
		assertTrue(acct2.getBalance().compareTo(BigDecimal.valueOf(34.90)) == 0);
	}

}
