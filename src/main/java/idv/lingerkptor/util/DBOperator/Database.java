package idv.lingerkptor.util.DBOperator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Database {
	private Properties dbprops;

	private String driver;
	private String driverUrl;
	private String url;
	private String account;
	private String password;
	private int maxConnection;
	private static Database db = null; 

	private  Database() {
		File propsfile = new File("./config/db.properties");
		if (!propsfile.exists())
			propsfile = new File("./config/db.default.properties");
		dbprops = new Properties();
		try {
			this.dbprops.load(new FileInputStream(propsfile));
			this.account = dbprops.getProperty("account");
			this.driver = this.dbprops.getProperty("driver");
			this.driverUrl = this.dbprops.getProperty("driverUrl");
			this.password = this.dbprops.getProperty("password");
			this.url = this.dbprops.getProperty("url");
			this.maxConnection = Integer.parseInt(this.dbprops.getProperty("maxConnection"));
		} catch (FileNotFoundException e) {
			System.err.println("¨S§ä¨ìÀÉ®×");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static Database getDatabase() {
		if(db ==null)
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
