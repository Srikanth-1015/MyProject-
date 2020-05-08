package com.Litmus.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utilities.DbUtilities;


public class App {
	public static void main(String[] args) throws Exception {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(args[0]);
		} catch (Exception e) {
			System.out.println("Please enter the Properties file path");
			System.exit(0);
		}
		System.out.println("Entered file path-" + args[0]);
		MySingleton ms = MySingleton.getInstance(fis);
		String[] x = ms.getHeader().split(",");
		Thread thread = new Thread();
		System.out.println("Headers in property file : " + String.valueOf(ms.getHeader()));
		CreateQuery();
	}

	private static void CreateQuery() throws Exception {
		Connection con = null;
		Statement stmt = null;
		FileInputStream fis = null;
		MySingleton ms = MySingleton.getInstance(fis);
		try {
			DbUtilities dbUtilities = new DbUtilities();
			con = dbUtilities.getCon();
			System.out.println("Connected database successfully...");
			System.out.println("Creating table in given database...");
			stmt = con.createStatement();
			String sql = ms.getCreatetable();
			stmt.executeUpdate(sql);
			System.out.println("Created table successfully in given database...");
			files();
		} catch (SQLException se) {
			System.out.println("Tablename already exist...");
			files();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					con.close();
			} catch (SQLException se) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public static void files() throws Exception {
		FileInputStream fis = null;
		MySingleton ms = MySingleton.getInstance(fis);

		System.out.println("..........Files in given folder..........");
		File folder = null;
		App listFiles = new App();
		try {
			folder = new File(ms.getFolderpath());
			listFiles.listAllFiles(folder);
		} catch (Exception e) {
			System.out.println("Please enter valid folder path...");
			System.exit(0);
		}
		System.out.println(".......Give file path to archieve......");
		Scanner a = new Scanner(System.in);
		String data = a.nextLine();
		File afile = new File(data);
		if (afile.renameTo(new File(ms.getArchiveFolderPath() + afile.getName()))) {
			System.out.println("File is moved successful!");
		} else {
			System.out.println("File is failed to move!, please check the filepath");
			System.exit(0);
		}
		File folder1 = new File(ms.getArchiveFolderPath());
		App listFiles1 = new App();
		String[] files = new File(ms.getArchiveFolderPath()).list();
		System.out.println("Archived file paths:");
		File[] fileNames = folder1.listFiles();
		for(File file: fileNames ) {
			if (file.isDirectory()) {
				ms.getArchiveFolderPath();
			} else {
				try {
					System.out.println("> " + file.getCanonicalPath());
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						String strLine;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Scanner b = new Scanner(System.in);
		String destFile = b.nextLine();
		
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(destFile);
			Test(destFile);
		} catch (Exception e) {
			System.out.println("please enter valid file path....");
			System.exit(0);
		}	
	}
	private static void Test(String destFile) throws Exception {
		if (destFile.endsWith(".xlsx")) {
			System.out.println("it is a .xlsx file");
			App.Excel2Db(destFile);
		} else if (destFile.endsWith(".csv")) {
			System.out.println("it is a .csv file");
			App.Csv2Db(destFile);
		} else {
			System.out.println("it is not a desired file");
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
		System.out.println("> " + file.getCanonicalPath());
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String strLine;
		}
	}

	public void readContent(Path filePath) throws IOException {
		System.out.println("> " + filePath);
		List<String> fileList = Files.readAllLines(filePath);
		System.out.println("" + fileList);
	}

	private static void Excel2Db(String destFile) throws Exception {
		FileInputStream fis = null;

		MySingleton ms = MySingleton.getInstance(fis);
		Connection con = null;
		DbUtilities dbUtilities = new DbUtilities();
		con = dbUtilities.getCon();

		int batchSize = Integer.parseInt(ms.getBatchsize());
		try {
			long start = System.currentTimeMillis();
			File f1 = new File(destFile);
			int[] c = null;
			int a = 0;
			int totallines = 0;
			int remainlines = 0;
			FileReader fr = new FileReader(f1);
			BufferedReader br = new BufferedReader(fr);
			String s;

			Workbook workbook = new XSSFWorkbook(f1);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.iterator();
			con = dbUtilities.getCon();
			con.setAutoCommit(false);
			String sql = ms.getSql();
			PreparedStatement statement = con.prepareStatement(sql);
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
						java.util.Date date = nextCell.getDateCellValue();
						Date newDate = new Date(date.getTime());
						// System.out.println(newDate);
						statement.setDate(2, newDate);
						break;
					case 2:
						int age = (int) nextCell.getNumericCellValue();
						statement.setInt(3, age);
						break;
					case 3:
						String gender = nextCell.getStringCellValue();
						statement.setString(4, gender);
						break;
					case 4:
						String education = nextCell.getStringCellValue();
						statement.setString(5, education);
						break;
					}
				}
				++totallines;

				statement.addBatch();
				count++;

				if (count == batchSize) {
					count = 0;

					c = statement.executeBatch();
					a = a + c.length;
				}
			}
			if (count != 0) {
				c = statement.executeBatch();
				a = a + c.length;
			}
			workbook.close();
			con.commit();
			con.close();
			fr.close();
			long end = System.currentTimeMillis();
			System.out.printf("Import done in %d ms\n", (end - start));
			System.out.println("Total no of lines in a file: " + totallines);
			System.out.println("Total no of lines sended: " + a);
			remainlines = totallines - a;
			System.out.println("number of lines not sended: " + remainlines);
			HashMap<String, Integer> hashMap = new HashMap<>();
			hashMap.put("remainingLines", remainlines);
			hashMap.put("totalLines", totallines);
			hashMap.put("sendedLines", a);
			SendEmail.Sendmail("sri.litmus@gmail.com", hashMap);
			System.out.println("........Successfully imported.......");
		} catch (IOException ex1) {
			System.out.println("Error reading file");
			ex1.printStackTrace();
		} catch (SQLException ex2) {
			System.out.println("Already data Transfered");
			ex2.printStackTrace();
		}
	}

	private static void Csv2Db(String destFile) throws Exception {
		FileInputStream fis = null;
		MySingleton ms = MySingleton.getInstance(fis);
		Connection con = null;
		try {
			DbUtilities dbUtilities = new DbUtilities();
			con = dbUtilities.getCon();
			CSVLoader loader = new CSVLoader(dbUtilities.getCon());
			loader.loadCSV(destFile, ms.getTablename(), true);
			System.out.println("........Successfully transferred........");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}