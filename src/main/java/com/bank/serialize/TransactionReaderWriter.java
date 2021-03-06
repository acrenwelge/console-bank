package com.bank.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.bank.model.MessageHolder;
import com.bank.model.Transaction;
import com.bank.util.Util;

public class TransactionReaderWriter {
	public static final String TRANSACTION_DIR = "Transactions/";
	public static final String FILE_EXT = ".dat";
	
	private static Logger log = Util.getFileLogger();

	public void saveTransaction(Transaction tr) throws IOException {
		File dir = new File(TRANSACTION_DIR);
		if (!dir.exists()) { // create the directory if it doesn't exist
			dir.mkdirs();
		}
		// use a timestamp for the file name, replacing all the colons in the time with periods for proper filename
		String modifiedTimestamp = tr.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll(":", ".");
		File newFile = new File(TRANSACTION_DIR + modifiedTimestamp + FILE_EXT);
		if (newFile.createNewFile()) // create the file if it doesn't already exist
			log.info("File "+newFile.getAbsolutePath() + " created");
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(newFile))) {
			oos.writeObject(tr);
		}
	}

	public List<Transaction> getAllTransactions() throws IOException {
		List<Transaction> trList = new LinkedList<>();
		File[] files = new File(TRANSACTION_DIR).listFiles();
		for (File f : files) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				try {
					Transaction a = (Transaction) ois.readObject();
					trList.add(a);
				} catch (ClassNotFoundException | IOException e) {
					log.error(MessageHolder.exceptionLogMsg, e);
				}
			}
		}
		return trList;
	}
	
}
