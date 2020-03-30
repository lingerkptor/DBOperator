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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 用來管理connection，創造、回收、釋放的類別
 * 
 *  created by lingerkptor 2020/03/27
 */
public class ConnectPool {
	/**
	 *  儲存可使用的connection的pool  
	 */
	private static List<Connection> pool= new ArrayList<Connection>();
	/**
	 * 資料庫實體
	 */
	private static Database db = Database.getDatabase();
	/**
	 * 儲存正在使用的Connection．
	 */
	private static List<Connection> usingConn= new ArrayList<Connection>(db.getMaxConnection());

	/**
	 *  取得可使用的connection
	 * 
	 * @return 從connection pool拿connection，如果pool都拿完的時候回傳null
	 */
	public synchronized static Connection getConnection() {
		Connection conn = null;
		if (usingConn.size() < db.getMaxConnection()) {
			if (pool.isEmpty()) {
				try {
					URLClassLoader ucl = new URLClassLoader(
							new URL[] { new URL("jar:file:" + db.getDriverUrl() + " !/ ") });
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

					conn = DriverManager.getConnection(db.getUrl(), db.getAccount(), db.getPassword());
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
			} else {
				conn = pool.get(0);
				pool.remove(0);
			}
			usingConn.add(conn);
			return conn;
		}
		return conn;
	}

	/**
	 * 歸還connection ，以便後續使用．
	 * 
	 * @param  DB connection
	 */
	public static synchronized void returnConnection(Connection conn) {
		usingConn.remove(conn);
		pool.add(conn);
	}

	/**
	 * 關閉pool中的所有connection
	 * 
	 * @return　關閉是否成功，如果有還在使用的connection，則會返回false．所有都關閉則會回傳true．
	 */
	public static synchronized boolean close() {
		if (usingConn.size() > 0)
			return false;
		while (pool.size() > 0) {
			try {
				pool.get(0).close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pool.remove(0);
		}
		return true;
	}
}
