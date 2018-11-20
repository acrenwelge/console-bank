package com.bank.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.bank.model.Customer;

public class TestCustomerSerialization {
	public static Customer c;
	
	@InjectMocks
	CustomerReaderWriter crw;

	@Before
	public void setUp() throws Exception {
		c = new Customer();
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
		c = null;
	}

	@Ignore
	@Test
	public void testCheckNewUsername() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveCustomer() throws IOException {
		c.setUsername("somecrazyus3rname");
		crw.saveCustomer(c);
		Customer newCust = crw.getCustomerByUsername("somecrazyus3rname");
		assertEquals(c, newCust);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSaveCustomerNull() throws IOException {
		c.setUsername("");
		crw.saveCustomer(c);
		c.setUsername(null);
		crw.saveCustomer(c);
	}

	@Ignore
	@Test
	public void testRegisterNewCustomer() {
		c.setFirstName("testFname");
		c.setLastName("testLname");
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetCustomerByUsername() {
		fail("Not yet implemented");
	}

}
