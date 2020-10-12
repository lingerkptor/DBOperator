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

import idv.lingerkptor.util.DBOperator.ConnectPool.STATE;

/**
 * 資料庫
 * 
 * @author lingerkptor
 *
 */
public class Database {

	/**
	 * JDBC 名稱
	 */
	private String driver;
	/**
	 * JDBC的URL位址
	 */
	private String driverUrl;
	/**
	 * 資料庫的URL位址
	 */
	private String url;
	/**
	 * 資料庫帳號
	 */
	private String account;
	/**
	 * 資料庫密碼
	 */
	private String password;
	/**
	 * 最大連線數
	 */
	private int maxConnection;

	/**
	 * 不給你用
	 */
	private Database() {

	}
	/**
	 * 不給你用
	 */
	private Database(DatabaseConfig config) {
		this.account = config.getAccount();
		this.driver = config.getDriver();
		this.driverUrl = config.getDriverUrl();
		this.password = config.getPassword();
		this.url = config.getUrl();
		this.maxConnection = config.getMaxConnection();
	}

	/**
	 * 用config的設定，去取得資料庫
	 * @param config
	 * @return
	 * @throws DBOperatorException 如果沒有給config，會拋出這個例外．
	 */
	public static Database getDatabase(DatabaseConfig config) throws DBOperatorException {
		if (config == null)
			throw new DBOperatorException("Database Config not Configure.", STATE.UNREADY);
		Database db = new Database(config);
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

	/** 動態載入Driver
	 * 
	 * @return Connection 回傳可用的Connection
	 */
	public Connection conecting() {
		Connection conn = null;
		try {
			URLClassLoader ucl = new URLClassLoader(new URL[] { new URL("jar:file:" + this.getDriverUrl() + " !/ ") });
			Driver d = (Driver) Class.forName(this.getDriver(), true, ucl).getDeclaredConstructor().newInstance();
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
