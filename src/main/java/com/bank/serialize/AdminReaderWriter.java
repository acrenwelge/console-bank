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

import com.bank.model.Admin;
import com.bank.model.MessageHolder;
import com.bank.util.Util;

public class AdminReaderWriter {
	public static final String ADMIN_DIR = "Admins/";
	public static final String ADMIN_MAX_ID_FILE = "maxadminid.txt";
	public static final String FILE_EXT = ".dat";
	
	private static Logger log = Util.getFileLogger();
	private static Logger out = Util.getConsoleLogger();
	
	public boolean checkNewUsername(String username) throws IOException {
		Predicate<Path> checkPath = path -> path.getFileName().toString().equals(username+FILE_EXT);
		File f = new File(ADMIN_DIR);
		if (!f.exists()) f.mkdir(); // create directory if it doesn't exist
		try (Stream<Path> paths = Files.walk(Paths.get(f.getAbsolutePath()))) {
		    return paths
		        .filter(Files::isRegularFile)
		        .anyMatch(checkPath);
		}
	}
	
	public void saveAdmin(Admin a) throws IOException {
		File adminFolder = new File(ADMIN_DIR);
		File custFile = new File(ADMIN_DIR+a.getUsername()+FILE_EXT);
		if (!adminFolder.exists()) {
			adminFolder.mkdir();
		}
		if (custFile.createNewFile()) // create the file if it doesn't already exist
			log.info("File "+custFile.getAbsolutePath() + " created");
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(custFile))) {
			oos.writeObject(a);
		}
	}
	
	public void registerNewAdmin(Admin a) throws IOException {
		File f = new File(ADMIN_MAX_ID_FILE); // file that stores the current max id
		int currentMax = 0; // will update this later on
		if (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				currentMax = Integer.parseInt(br.readLine());
				a.setId(++currentMax); // increment and set new customer id
				saveAdmin(a);
				out.info("Congrats! You were successfully registered!");
			}
		}
		else { // handle case of 1st admin
			if (f.createNewFile()) log.info("Created new file "+f.getAbsolutePath());
			a.setId(++currentMax);
			saveAdmin(a);
		}
		// in either case, write the new max to the file
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			bw.write(String.valueOf(a.getId())); // must write as a string
			out.info(f.getName()+" successfully updated");
		}
	}
	
	public Admin getAdminByUsername(String username) throws IOException {
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ADMIN_DIR+username+FILE_EXT))) {
			return (Admin) ois.readObject();
		} catch (ClassNotFoundException | ClassCastException e) {
			out.error("Something went wrong while reading customer from file");
			log.error(MessageHolder.exceptionLogMsg, e);
			return null;
		}
	}
}
