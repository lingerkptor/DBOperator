package idv.lingerkptor.util.DBOperator;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import idv.lingerkptor.util.DBOperator.DBOperatorException.CODE;

/**
 * 資料庫
 * 
 * @author lingerkptor
 *
 */
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

	public static Database getDatabase() throws DBOperatorException {
		if (config == null)
			throw new DBOperatorException("Database Config not Configure.", CODE.CONFIGISNULL);
		if (db == null)
			db = new Database();
		return db;
	}

	String getDriver() {
		return driver;
	}

	String getUrl() {
		return url;
	}

	String getAccount() {
		return account;
	}

	String getPassword() {
		return password;
	}

	int getMaxConnection() {
		return maxConnection;
	}

	String getDriverUrl() {
		return driverUrl;
	}

	// 動態載入Driver
	public Connection conecting() {
		Connection conn = null;
		try {
			URLClassLoader ucl = new URLClassLoader(new URL[] { new URL("jar:file:" + this.getDriverUrl() + " !/ ") });
			Driver d = (Driver) Class.forName(db.getDriver(), true, ucl).getDeclaredConstructor().newInstance();
			DriverManager.registerDriver(new Driver() {
				@Override
				public Connection connect(String url, Properties info) throws SQLException {
					return d.connect(url, info);
				}

				@Override
				public boolean acceptsURL(String url) throws SQLException {
					return d.acceptsURL(url);
				}

				@Override
				public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {

					return d.getPropertyInfo(url, info);
				}

				@Override
				public int getMajorVersion() {
					return d.getMajorVersion();
				}

				@Override
				public int getMinorVersion() {
					return d.getMinorVersion();
				}

				@Override
				public boolean jdbcCompliant() {
					return d.jdbcCompliant();
				}

				@Override
				public Logger getParentLogger() throws SQLFeatureNotSupportedException {
					return d.getParentLogger();
				}
			});
			conn = DriverManager.getConnection(this.getUrl(), this.getAccount(), this.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
