package idv.lingerkptor.util.DBOperator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Database {

	private static DatabaseConfig config = null;
	private String driver;
	private String driverUrl;
	private String url;
	private String account;
	private String password;
	private int maxConnection;
	private static Database db = null;

	private Database() {
		this.account = config.getAccount();
		this.driver = config.getDriver();
		this.driverUrl = config.getDriverUrl();
		this.password = config.getPassword();
		this.url = config.getUrl();
		this.maxConnection = config.getMaxConnection();
	}

	public static void setDatabaseConfig(DatabaseConfig config) {
		Database.config = config;
	}

	public static Database getDatabase() throws Exception {
		if (config == null)
			throw new Exception("Database Config not Configure.");
		if (db == null)
			db = new Database();
		return db;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getAccount() {
		return account;
	}

	public String getPassword() {
		return password;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public String getDriverUrl() {
		return driverUrl;
	}

}
