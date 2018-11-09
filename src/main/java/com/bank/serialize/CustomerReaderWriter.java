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

import com.bank.model.Customer;

public class CustomerReaderWriter {
	
	public static boolean checkNewUsername(String username) throws IOException {
		Predicate<Path> checkPath = path -> path.getFileName().toString().equals(username+".dat");
		File f = new File("Customers/");
		if (!f.exists()) f.mkdir();
		try (Stream<Path> paths = Files.walk(Paths.get(f.getAbsolutePath()))) {
		    return paths
		        .filter(Files::isRegularFile)
		        .anyMatch(checkPath);
		}
	}
	
	public static void saveCustomer(Customer cust) throws IOException {
		File custFile = new File("Customers/"+cust.getUsername()+".dat");
		custFile.createNewFile(); // create the file if it doesn't already exist
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(custFile));
		oos.writeObject(cust);
		oos.close();
	}
	
	public static void registerNewCustomer(Customer cust) throws IOException {
		File f = new File("maxuserid.txt"); // file that stores the current max id
		int currentMax = 0; // will update this later on
		if (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				currentMax = Integer.parseInt(br.readLine());
				cust.setId(++currentMax); // increment and set new customer id
				saveCustomer(cust);
				System.out.println("Congrats! You were successfully registered!");
			}
		}
		else { // handle case of 1st customer
			cust.setId(++currentMax);
			saveCustomer(cust);
		}
		// in either case, write the new max to the file
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			bw.write(String.valueOf(cust.getId())); // must write as a string
			System.out.println(f.getName()+" successfully updated");
		}
	}
	
	public static Customer getCustomerByUsername(String username) throws IOException {
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Customers/"+username+".dat"))) {
			return (Customer) ois.readObject();
		} catch (ClassNotFoundException | ClassCastException e) {
			System.err.println("Something went wrong while reading customer from file");
			e.printStackTrace();
			return null;
		}
	}
}
