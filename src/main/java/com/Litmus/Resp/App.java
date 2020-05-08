package com.Litmus.Resp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class App {
	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream("E:\\LitmusWorld\\Git\\Sample1\\src\\Resources\\config.properties");
		Properties prop = new Properties();
		prop.load(fis);
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		String jdbcURL = prop.getProperty("jdbcURL");
		String filepath = prop.getProperty("filepath");
		String folderpath = prop.getProperty("folderpath");
		String archiveFolderPath = prop.getProperty("archiveFolderPath");
		String url = prop.getProperty("url");
		String sql = prop.getProperty("sql");
		String createtable = prop.getProperty("createtable");
		String tablename = prop.getProperty("tablename");
		String headers = prop.getProperty("header");
		String[] x = headers.split(",");
		System.out.println("Headers in property file : " + String.valueOf(prop.getProperty("header")));
		CreateQuery();
		System.out.println("..........Files in given folder..........");
		File folder = new File(prop.getProperty("folderpath"));
		App listFiles = new App();
		listFiles.listAllFiles(folder);
		System.out.println("give file path to archieve......");
		Scanner a = new Scanner(System.in);
		String data = a.nextLine();
		File afile = new File(data);
		if (afile.renameTo(new File(prop.getProperty("archiveFolderPath") + afile.getName()))) {
			System.out.println("File is moved successful!");
		} else {
			System.out.println("File is failed to move!");
		}
		File folder1 = new File(prop.getProperty("archiveFolderPath"));
		App listFiles1 = new App();
		listFiles.listAllFiles(folder1);
		System.out.println("archive file path.......:");
		Scanner b = new Scanner(System.in);
		String destFile = b.nextLine();
		FileInputStream inputStream = new FileInputStream(destFile);
		if (destFile.endsWith(".xlsx")) {
			System.out.println("it is a .xlsx file");
			Excel2Db(destFile);
		} else if (destFile.endsWith(".csv")) {
			System.out.println("it is a .csv file");
			Csv2Db(destFile);
		} else {
			System.out.println("it is not a desired file");
		}
	}
	private static void CreateQuery() throws IOException {
		Connection conn = null;
		Statement stmt = null;
		FileInputStream fis = new FileInputStream("E:\\LitmusWorld\\Git\\Sample1\\src\\Resources\\config.properties");
		Properties prop = new Properties();
		prop.load(fis);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(prop.getProperty("jdbcURL"), prop.getProperty("username"),
					prop.getProperty("password"));
			System.out.println("Connected database successfully...");
			System.out.println("Creating table in given database...");
			stmt = conn.createStatement();

			String sql = prop.getProperty("createtable");
			stmt.executeUpdate(sql);
			System.out.println("Created table successfully in given database...");
		} catch (SQLException se) {

			System.out.println("Tablename already exist...");
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
	private void listAllFiles(File folder) {
		File[] fileNames = folder.listFiles();
		for (File file : fileNames) {
			if (file.isDirectory()) {
				listAllFiles(file);
			} else {
				try {
					readContent(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void readContent(File file) throws IOException {

		System.out.println("read file " + file.getCanonicalPath());
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String strLine;
		}
	}
	public void readContent(Path filePath) throws IOException {
		System.out.println("read file " + filePath);
		List<String> fileList = Files.readAllLines(filePath);
		System.out.println("" + fileList);
	}
	private static void Excel2Db(String destFile) throws Exception {
		FileInputStream fis = new FileInputStream("E:\\LitmusWorld\\Git\\Sample1\\src\\Resources\\config.properties");
		Properties prop = new Properties();
		prop.load(fis);
		
		Connection con = null;
		try {
			con = DriverManager.getConnection(prop.getProperty("jdbcURL"),prop.getProperty("username") , prop.getProperty("password"));
			System.out.println("Connected to MySQL database");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Scanner sc = new Scanner(System.in);

		int batchSize = Integer.parseInt(prop.getProperty("batchsize"));
		
		Connection connection = null;
		try {
			long start = System.currentTimeMillis();
			FileInputStream inputStream = new FileInputStream(destFile);//file path given in command line arguments
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.iterator();
			connection = DriverManager.getConnection(prop.getProperty("jdbcURL"),prop.getProperty("username") , prop.getProperty("password"));
			connection.setAutoCommit(false);
			String sql = prop.getProperty("sql");
			PreparedStatement statement = connection.prepareStatement(sql);
			int count = 0;
			rowIterator.next(); // skip the header row
			while (rowIterator.hasNext()) {
				Row nextRow = rowIterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				while (cellIterator.hasNext()) {
					Cell nextCell = cellIterator.next();
					
					  int columnIndex = nextCell.getColumnIndex();
 
                    switch (columnIndex) {
                    case 0:
                        String name = nextCell.getStringCellValue();
                        statement.setString(1, name);
                        break;
                    case 1:
                    	int dateofbirth=(int) nextCell.getNumericCellValue();
                    	statement.setInt(2, dateofbirth);
                                  break;
                      
                    case 2:
                        int age = (int) nextCell.getNumericCellValue();
                        statement.setInt(3, age);
                        break;
                    case 3:
                    	String gender=nextCell.getStringCellValue();
                    	statement.setString(4, gender);
                    	break;
                    case 4:
                    	String education=nextCell.getStringCellValue();
                    	statement.setString(5, education);
                    	break;
                    	}
            }
				statement.addBatch();
				count++;
				if (count==batchSize) {
					count=0;
					statement.executeBatch();
				}
			}
			if(count!=0) {
				statement.executeBatch();
			}
			workbook.close();
			connection.commit();
			connection.close();

			long end = System.currentTimeMillis();
			System.out.printf("Import done in %d ms\n", (end - start));

		} catch (IOException ex1) {
			System.out.println("Error reading file");
			ex1.printStackTrace();
		} catch (SQLException ex2) {
			System.out.println("Already data Transfered");
			ex2.printStackTrace();
		}
	}

	private static void Csv2Db(String destFile) throws IOException {

		FileInputStream fis = new FileInputStream("E:\\LitmusWorld\\Git\\Sample1\\src\\Resources\\config.properties");
		Properties prop = new Properties();
		prop.load(fis);
		String jdbcURL = prop.getProperty("jdbcURL");
		try {
			CSVLoader loader = new CSVLoader(getCon());
			loader.loadCSV(destFile, prop.getProperty("tablename"), true);
			System.out.println("Successfully transferred");
		} catch (Exception e) {
			e.printStackTrace();
			;
		}
	}
	private static Connection getCon() throws IOException {
		Connection connection = null;
		FileInputStream fis = new FileInputStream("E:\\LitmusWorld\\Git\\Sample1\\src\\Resources\\config.properties");
		Properties prop = new Properties();
		prop.load(fis);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(prop.getProperty("jdbcURL"), prop.getProperty("username"),
					prop.getProperty("password"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
