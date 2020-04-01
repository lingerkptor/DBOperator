package idv.lingerkptor.util.DBOperator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DBConfig implements DatabaseConfig {
	private Properties dbprops;
	private String driver;
	private String driverUrl;
	private String url;
	private String account;
	private String password;
	private int maxConnection;

	public DBConfig() {
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
			System.err.println("沒找到檔案");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDriver() {
		return driver;
	}

	@Override
	public String getDriverUrl() {
		return driverUrl;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public int getMaxConnection() {
		return maxConnection;
	}

}
