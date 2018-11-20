package com.bank.serialize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.Customer;
import com.bank.model.MessageHolder;
import com.bank.util.Util;

public class AccountReaderWriter {
	public static final String ACCT_DIR = "Accounts/";
	public static final String FILE_EXT = ".dat";
	public static final String ACCT_MAX_ID_FILE = "maxaccountid.txt";
	
	private static Logger log = Util.getFileLogger();
	private static Logger out = Util.getConsoleLogger();
	
	private CustomerReaderWriter crw;
	
	public AccountReaderWriter(CustomerReaderWriter crw) {
		this.crw = crw;
	}
	
	public List<Account> getAllAccounts() throws IOException {
		List<Account> allAccounts = new LinkedList<>();
		File[] files = new File(ACCT_DIR).listFiles();
		for (File f : files) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				try {
					Account a = (Account) ois.readObject();
					allAccounts.add(a);
				} catch (ClassNotFoundException | IOException e) {
					log.error(MessageHolder.exceptionLogMsg, e);
				}
			}
		}
		return allAccounts;
	}
	
	public List<Account> getAllAccountsByStatus(AccountStatus as) throws IOException {
		List<Account> allOfStatus = new LinkedList<>();
		File[] files = new File(ACCT_DIR).listFiles();
		for (File f : files) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				try {
					Account a = (Account) ois.readObject();
					AccountStatus status = a.getAcctStatus();
					if (status != null && status.equals(as)) allOfStatus.add(a);
				} catch (ClassNotFoundException | IOException e) {
					log.error(MessageHolder.exceptionLogMsg, e);
				}
			}
		}
		return allOfStatus;
	}
	
	public Account getAccountById(int id) throws IOException {
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ACCT_DIR+id+FILE_EXT))) {
			return (Account) ois.readObject();
		} catch (ClassNotFoundException | ClassCastException e) {
			out.error("Something went wrong while reading account from file");
			log.error(MessageHolder.exceptionLogMsg, e);
			return null;
		}
	}
	
	public void registerNewAccount(Customer cust, Account acct) throws IOException {
		acct.setCreationDate(LocalDate.now());
		File f = new File(ACCT_MAX_ID_FILE); // file that stores the current max id
		int currentMax = 0; // will update this later on
		if (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				currentMax = Integer.parseInt(br.readLine()); // get current max from file
				acct.setId(++currentMax); // increment and set new customer id
			}
		}
		else { // handle case of 1st account
			acct.setId(currentMax+1); // set id to 1
		}
		// in either case, save account and add to customer and write the new max to the file
		saveAccount(acct);
		out.info("Success! Your new account"+acct.getName()+" was created");
		cust.addNewAccount(acct.getId());
		crw.saveCustomer(cust);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			bw.write(String.valueOf(acct.getId())); // must write as String
			out.info(f.getName()+" successfully updated");
		}
	}
	
	public void saveAccount(Account acct) throws IOException {
		File custFile = new File(ACCT_DIR+acct.getId()+FILE_EXT);
		if (!custFile.getParentFile().exists()) { // create 'Accounts' directory if it doesn't exist
			boolean success = custFile.getParentFile().mkdirs();
			if (success) out.info("Created 'Accounts' folder");
			else out.error("Unable to create 'Accounts' folder");
		}
		if (custFile.createNewFile()) log.info("Created new file "+custFile.getAbsolutePath()); // create the file if it doesn't already exist
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(custFile))) {
			oos.writeObject(acct);			
		}
	}
}
