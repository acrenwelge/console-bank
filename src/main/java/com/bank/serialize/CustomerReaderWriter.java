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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import com.bank.model.Customer;
import com.bank.model.MessageHolder;
import com.bank.util.Util;

public class CustomerReaderWriter {
	private static Logger out = Util.getConsoleLogger();
	private static Logger log = Util.getFileLogger();
	
	private static final String CUST_DIR = "Customers/";
	private static final String FILE_EXT = ".dat";
	
	public boolean checkNewUsername(String username) throws IOException {
		Predicate<Path> checkPath = path -> path.getFileName().toString().equals(username+FILE_EXT);
		File f = new File(CUST_DIR);
		if (!f.exists()) f.mkdir();
		try (Stream<Path> paths = Files.walk(Paths.get(f.getAbsolutePath()))) {
		    return paths
		        .filter(Files::isRegularFile)
		        .anyMatch(checkPath);
		}
	}
	
	public void saveCustomer(Customer cust) throws IOException {
		File custFile = new File(CUST_DIR+cust.getUsername()+FILE_EXT);
		if (custFile.createNewFile()) // create the file if it doesn't already exist
			log.info("File "+custFile.getAbsolutePath() + " created");
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(custFile))) {
			oos.writeObject(cust);
		}
	}
	
	public void registerNewCustomer(Customer cust) throws IOException {
		File f = new File("maxuserid.txt"); // file that stores the current max id
		int currentMax = 0; // will update this later on
		if (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				currentMax = Integer.parseInt(br.readLine());
				cust.setId(++currentMax); // increment and set new customer id
				saveCustomer(cust);
				out.info("Congrats! You were successfully registered!");
			}
		}
		else { // handle case of 1st customer
			cust.setId(++currentMax);
			saveCustomer(cust);
		}
		// in either case, write the new max to the file
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			bw.write(String.valueOf(cust.getId())); // must write as a string
			out.info(f.getName()+" successfully updated");
		}
	}
	
	public Customer getCustomerByUsername(String username) throws IOException {
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CUST_DIR+username+FILE_EXT))) {
			return (Customer) ois.readObject();
		} catch (ClassNotFoundException | ClassCastException e) {
			log.error("Something went wrong while reading customer from file");
			log.error(MessageHolder.exceptionLogMsg, e);
			return null;
		}
	}
}
