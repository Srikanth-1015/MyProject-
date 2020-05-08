package com.Litmus.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MySingleton {
	public String username;
	public String password;
	public String jdbcURL;
	public String folderpath;
	public String archiveFolderPath;
	public String createtable;
	public String sql;
	public String header;
	public String tablename;
	public String batchsize;
	public String driver;
	private static MySingleton INSTANCE = null;
	private MySingleton(FileInputStream fis) {
		Properties prop = new Properties();
	try {
			prop.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.header = prop.getProperty("header");
		this.username = prop.getProperty("username");
		this.password = prop.getProperty("password");
		this.folderpath = prop.getProperty("folderpath");
		this.batchsize = prop.getProperty("batchsize");
		this.archiveFolderPath = prop.getProperty("archiveFolderPath");
		this.createtable = prop.getProperty("createtable");
		this.jdbcURL = prop.getProperty("jdbcURL");
		this.sql = prop.getProperty("sql");
		this.driver = prop.getProperty("driver");
		this.tablename = prop.getProperty("tablename");

	}

	public static MySingleton getInstance(FileInputStream fis) {
		if(INSTANCE==null) {
			INSTANCE=new MySingleton( fis);
		}
		return MySingleton.INSTANCE;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getJdbcURL() {
		return jdbcURL;
	}

	public String getFolderpath() {
		return folderpath;
	}

	public String getArchiveFolderPath() {
		return archiveFolderPath;
	}

	public String getSql() {
		return sql;
	}

	public String getCreatetable() {
		return createtable;
	}

	public String getTablename() {
		return tablename;
	}

	public String getHeader() {
		return header;
	}

	public String getBatchsize() {
		return batchsize;
	}

	public String getDriver() {
		return driver;
	}
}