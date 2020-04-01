package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	private static Database db ;
	/**
	 * �x�s���b�ϥΪ�Connection�D
	 */
	private static List<Connection> usingConn;

	public static void setDatabase(Database db) {
		ConnectPool.db = db;
		usingConn= new ArrayList<Connection>(db.getMaxConnection());
	}
	
	/**
	 *  ���o�i�ϥΪ�connection
	 * 
	 * @return �qconnection pool��connection�A�p�Gpool���������ɭԦ^��null
	 */
	public synchronized static Connection getConnection() {
		Connection conn = null;
		if (usingConn.size() < db.getMaxConnection()) {
			if (pool.isEmpty()) {
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
