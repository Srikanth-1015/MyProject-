package com.Litmus.Response;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import utilities.DateUtil;

public class CSVLoader {

	private static final 
	String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
private static final String TABLE_REGEX = "\\$\\{table\\}";
private static final String KEYS_REGEX = "\\$\\{keys\\}";
private static final String VALUES_REGEX = "\\$\\{values\\}";

private Connection connection;
private char seprator;

public CSVLoader(Connection connection) {
	this.connection = connection;
	//Set default separator
	this.seprator = ',';
}

/**
 * Parse CSV file using OpenCSV library and load in 
 * given database table. 
 * @param csvFile Input CSV file
 * @param tableName Database table name to import data
 * @param truncateBeforeLoad Truncate the table before inserting 
 * 			new records.
 * @throws Exception
 */
public void loadCSV(String csvFile, String tableName,
		boolean truncateBeforeLoad) throws Exception {
	
	FileInputStream fis=null;
	
	MySingleton ms = MySingleton.getInstance(fis);
	int linescount=0;
	int totallines=0;
	int remainlines=0;
	
	
	CSVReader csvReader = null;
	if(null == this.connection) {
		throw new Exception("Not a valid connection.");
	}
	try {
		
		csvReader = new CSVReader(new FileReader(csvFile), this.seprator);

	} catch (Exception e) {
		e.printStackTrace();
		throw new Exception("Error occured while executing file. "
				+ e.getMessage());
	}

	String[] headerRow = csvReader.readNext();

	if (null == headerRow) {
		throw new FileNotFoundException(
				"No columns defined in given CSV file." +
				"Please check the CSV file format.");
	}

	String questionmarks = StringUtils.repeat("?,", headerRow.length);
	questionmarks = (String) questionmarks.subSequence(0, questionmarks
			.length() - 1);

	String query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
	String head=StringUtils.join(headerRow, ",");
	query = query
			.replaceFirst(KEYS_REGEX, head);
	query = query.replaceFirst(VALUES_REGEX, questionmarks);

	System.out.println("Query: " + query);

	String[] nextLine;
	Connection con = null;
	PreparedStatement ps = null;
	try {
		con = this.connection;
		con.setAutoCommit(false);
		ps = con.prepareStatement(query);

		//if(truncateBeforeLoad) {
			//delete data from table before loading csv
			//con.createStatement().execute("DELETE FROM " + tableName);
		//}
int batchSize=Integer.parseInt(ms.getBatchsize());
	//final int batchSize=1000;
		int count = 0;
		int[] c=null;
		int a=0;
		
		Date date = null;
		while ((nextLine = csvReader.readNext()) != null) {
			if (null != nextLine) {
				int index = 1;
				for (String string : nextLine) {
					date = DateUtil.convertToDate(string);
					if (null != date) {
						ps.setDate(index++, new java.sql.Date(date
								.getTime()));
						
					} else {
						ps.setString(index++, string);
						
					}
				}
				totallines++;
				//++linescount;
				 ps.addBatch();
			}
			if (++count % batchSize == 0) {
				c=ps.executeBatch();
				a=a+c.length;
				}
		}if (count != 0) {
			c=ps.executeBatch();
			a=a+c.length;
		}
		     // insert remaining records
		con.commit();
		System.out.println("Total number of lines:"+totallines);
		System.out.println("no. of lines transferred: "+a);
		remainlines=totallines-a;
		System.out.println("remaining lines: "+remainlines);
		HashMap<String,Integer> hashMap=new HashMap<>();
		hashMap.put("remainingLines", remainlines);
		hashMap.put("totalLines", totallines);
		hashMap.put("sendedLines",a);
		SendEmail.Sendmail("sri.litmus@gmail.com",hashMap);
		
	} catch (Exception e) {
		con.rollback();
		System.out.println("Invalid headerss");
		/*throw new Exception(
				"Error occured while loading data from file to database."
						);//+ e.getMessage()*/System.exit(0);
	} finally {
		if (null != ps)
			ps.close();
		if (null != con)
			con.close();
		csvReader.close();
	}
}

public char getSeprator() {
	return seprator;
}

public void setSeprator(char seprator) {
	this.seprator = seprator;
}
	
}
