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
 * �ΨӺ޲zconnection�A�гy�B�^���B�������O
 * 
 *  created by lingerkptor 2020/03/27
 */
public class ConnectPool {
	/**
	 *  �x�s�i�ϥΪ�connection��pool  
	 */
	private static List<Connection> pool= new ArrayList<Connection>();
	/**
	 * ��Ʈw����
	 */
	private static Database db = Database.getDatabase();
	/**
	 * �x�s���b�ϥΪ�Connection�D
	 */
	private static List<Connection> usingConn= new ArrayList<Connection>(db.getMaxConnection());

	/**
	 *  ���o�i�ϥΪ�connection
	 * 
	 * @return �qconnection pool��connection�A�p�Gpool���������ɭԦ^��null
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
	 * �k��connection �A�H�K����ϥΡD
	 * 
	 * @param  DB connection
	 */
	public static synchronized void returnConnection(Connection conn) {
		usingConn.remove(conn);
		pool.add(conn);
	}

	/**
	 * ����pool�����Ҧ�connection
	 * 
	 * @return�@�����O�_���\�A�p�G���٦b�ϥΪ�connection�A�h�|��^false�D�Ҧ��������h�|�^��true�D
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
