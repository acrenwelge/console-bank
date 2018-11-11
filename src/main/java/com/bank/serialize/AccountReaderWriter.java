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

import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.Customer;

public class AccountReaderWriter {
	public static final String ACCT_DIR = "Accounts/";
	public static final String FILE_EXT = ".dat";
	public static final String ACCT_MAX_ID_FILE = "maxaccountid.txt";
	
	public static List<Account> getAllAccounts() throws IOException {
		List<Account> allAccounts = new LinkedList<>();
		File[] files = new File(ACCT_DIR).listFiles();
		for (File f : files) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				try {
					Account a = (Account) ois.readObject();
					allAccounts.add(a);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		return allAccounts;
	}
	
	public static List<Account> getAllAccountsByStatus(AccountStatus as) throws IOException {
		List<Account> allOfStatus = new LinkedList<>();
		File[] files = new File(ACCT_DIR).listFiles();
		for (File f : files) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				try {
					Account a = (Account) ois.readObject();
					if (a.getAcctStatus().equals(as)) allOfStatus.add(a);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		return allOfStatus;
	}
	
	public static Account getAccountById(int id) throws IOException {
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ACCT_DIR+id+FILE_EXT))) {
			return (Account) ois.readObject();
		} catch (ClassNotFoundException | ClassCastException e) {
			System.err.println("Something went wrong while reading account from file");
			e.printStackTrace();
			return null;
		}
	}
	
	public static void registerNewAccount(Customer cust, Account acct) throws IOException {
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
		System.out.println("Success! Your new account"+acct.getName()+" was created");
		cust.addNewAccount(acct.getId());
		CustomerReaderWriter.saveCustomer(cust);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			bw.write(String.valueOf(acct.getId())); // must write as String
			System.out.println(f.getName()+" successfully updated");
		}
	}
	
	public static void saveAccount(Account acct) throws IOException {
		File custFile = new File(ACCT_DIR+acct.getId()+FILE_EXT);
		if (!custFile.getParentFile().exists()) { // create 'Accounts' directory if it doesn't exist
			boolean success = custFile.getParentFile().mkdirs();
			if (success) System.out.println("Created 'Accounts' folder");
			else System.err.println("Unable to create 'Accounts' folder");
		}
		custFile.createNewFile(); // create the file if it doesn't already exist
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(custFile))) {
			oos.writeObject(acct);			
		}
	}
}
