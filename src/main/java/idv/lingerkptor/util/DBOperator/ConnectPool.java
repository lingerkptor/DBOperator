package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	private static Database db ;
	/**
	 * 儲存正在使用的Connection．
	 */
	private static List<Connection> usingConn;

	public static void setDatabase(Database db) {
		ConnectPool.db = db;
		usingConn= new ArrayList<Connection>(db.getMaxConnection());
	}
	
	/**
	 *  取得可使用的connection
	 * 
	 * @return 從connection pool拿connection，如果pool都拿完的時候回傳null
	 */
	public synchronized static Connection getConnection() {
		Connection conn = null;
		if (usingConn.size() < db.getMaxConnection()) {
			if (pool.isEmpty()) {
				// 資料庫連接
				conn = db.conecting();
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
				throw new  DataAccessException("connection closing error.");
			}
			pool.remove(0);
		}
		return true;
	}

}
