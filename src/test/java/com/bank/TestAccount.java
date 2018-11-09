package com.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bank.model.Account;

public class TestAccount {
	
	Account acct1;
	Account acct2;

	@Before
	public void setUp() throws Exception {
		acct1 = new Account();
		acct2 = new Account();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDeposit() {
		// start from default - $0
		acct1.depositMoney(new BigDecimal("100"));
		assertEquals(acct1.getBalance(), BigDecimal.valueOf(100));
		acct1.depositMoney(BigDecimal.valueOf(45.23));
		assertEquals(acct1.getBalance(), BigDecimal.valueOf(145.23));
	}
	
	@Test
	public void testWithdraw() {
		acct1.setBalance(BigDecimal.valueOf(100.01));
		acct1.withdrawMoney(BigDecimal.valueOf(50.01));
		assertTrue(acct1.getBalance().compareTo(BigDecimal.valueOf(50.00)) == 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOverdraft() {
		acct1.setBalance(BigDecimal.valueOf(100.01));
		acct1.withdrawMoney(BigDecimal.valueOf(101.01));
	}
	
	@Test
	public void testTransfer() {
		acct1.setBalance(BigDecimal.valueOf(39.93));
		acct2.setBalance(BigDecimal.valueOf(24.41));
		Account.transferFunds(acct1, acct2, BigDecimal.valueOf(10.49));
		assertTrue(acct1.getBalance().compareTo(BigDecimal.valueOf(29.44)) == 0);
		assertTrue(acct2.getBalance().compareTo(BigDecimal.valueOf(34.90)) == 0);
	}

}
