package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用來管理connection，創造、回收、釋放的類別
 * 
 * created by lingerkptor 2020/03/27
 */
public class ConnectPool {
	/**
	 * 儲存可使用的connection的pool
	 */
	private static List<Connection> pool = new ArrayList<Connection>();
	/**
	 * 資料庫實體
	 */
	private static Database db = null;
	/**
	 * 儲存正在使用的Connection．
	 */
	private static List<Connection> usingConn = null;

	private static STATE state = STATE.UNREADY;

	/**
	 * 服務狀態 <br/>
	 * UNREADY =尚未給定資料庫 <br/>
	 * SERVICE = 服務中 <br/>
	 * CLOSING = 有資料庫資訊但已關閉
	 */
	public enum STATE {
		SERVICE, CLOSING, UNREADY, CLOSED
	};

	public static STATE getState() {
		return ConnectPool.state;
	}

	public static synchronized void setDatabase(Database db) {
		synchronized (state) {
			ConnectPool.db = db;
			usingConn = new ArrayList<Connection>(db.getMaxConnection());
			ConnectPool.state = STATE.SERVICE;
		}
	}

	/**
	 * 取得可使用的connection
	 * 
	 * @return 從connection pool拿connection，如果pool都拿完的時候回傳null
	 * @throws Exception
	 */
	public static Connection getConnection() {
		Connection conn = null;
		synchronized (state) {
			switch (state) {
			case UNREADY:
				throw new DataAccessException("資料庫尚未給定.");
			case CLOSING:
				throw new DataAccessException("資料庫關閉中，已不提供服務.");
			case CLOSED:
				throw new DataAccessException("資料庫已關閉，已不提供服務.");
			default:
				break;
			}
			synchronized (usingConn) {
				if (usingConn.size() < db.getMaxConnection()) {
					synchronized (pool) {
						if (pool.isEmpty()) {
							// 資料庫連接
							conn = db.conecting();
						} else {
							conn = pool.get(0);
							pool.remove(0);
						}
						usingConn.add(conn);
					}
				} else {
					throw new DataAccessException("Connection已滿，請稍後再使用.");
				}
			}
		}
		return conn;
	}

	/**
	 * 歸還connection ，以便後續使用．
	 * 
	 * @param DB connection
	 */
	public static synchronized void returnConnection(Connection conn) {
		synchronized (usingConn) {
			usingConn.remove(conn);
		}
		synchronized (pool) {
			pool.add(conn);
		}

	}

	/**
	 * 關閉pool中的所有connection <br/>
	 * 關閉過程 getState狀態變化 <br/>
	 * 關閉前
	 */
	public static void close() {
		synchronized (state) {
			switch (state) {
			case UNREADY:// 沒有資料庫可關閉 ，直接跳出
				return;
			case CLOSING:// 關閉中，避免重複執行關閉程式碼，直接跳出
				return;
			case CLOSED:// 已經關閉，直接跳出
				return;
			case SERVICE: // 這行要在CLOSING之前，不然不會執行下面的程式碼
				state = STATE.CLOSING;
			default:
				break;
			}
		}
		/**
		 * 等待使用中的Connect直到他都回到pool裡面
		 */
		Thread usingConnWaiting = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (this) {
					while (usingConn.size() > 0) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					notify();
				}
			}
		});
		usingConnWaiting.start();
		synchronized (usingConnWaiting) {
			try {
				usingConnWaiting.wait();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		synchronized (pool) {
			while (pool.size() > 0) {
				try {
					pool.get(0).close();
				} catch (SQLException e) {
					throw new DataAccessException("connection closing error.");
				}
				pool.remove(0);
			}
		}

	}

}
