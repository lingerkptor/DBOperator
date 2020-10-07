package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用來管理connection，創造、回收、釋放的類別
 * 
 * created by lingerkptor 2020/03/27
 * 
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

	/**
	 * 設定CooncetPool 狀態
	 * 
	 * @param state CooncetPool 狀態
	 */
	static void setState(STATE state) {
		ConnectPool.state = state;
	}

	/**
	 * 查詢目前CoonectPool狀態
	 * 
	 * @return CooncetPool 狀態
	 */
	public static STATE getState() {
		return ConnectPool.state;
	}

	/**
	 * 設定資料庫 <br/>
	 * 如果資料庫已關閉，執行這下一行會可回復
	 * 
	 * @param db 資料庫
	 */
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
	public static Connection getConnection() throws DBOperatorException {
		Connection conn = null;
		synchronized (state) {
			switch (state) {
			case UNREADY:
				throw new DBOperatorException("資料庫尚未給定.", DBOperatorException.CODE.UNREADY);
			case CLOSING:
				throw new DBOperatorException("ConnectPool關閉中，已不提供服務，.", DBOperatorException.CODE.CLOSING);
			case CLOSED:
				throw new DBOperatorException("ConnectPool已關閉，已不提供服務，如果想再次使用請重新給定資料庫．.",
						DBOperatorException.CODE.CLOSED);
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
					throw new DBOperatorException("Connection已滿，請稍後再使用.", DBOperatorException.CODE.CONNECTFULL);
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
	 */
	public static void close() throws DBOperatorException {
		synchronized (state) {
			switch (state) {
			case UNREADY:// 沒有資料庫可關閉 ，直接跳出
				throw new DBOperatorException("沒有資料庫可關閉 ，直接跳出．", DBOperatorException.CODE.UNREADY);
			case CLOSING:// 關閉中，避免重複執行關閉程式碼，直接跳出
				throw new DBOperatorException("關閉中，避免重複執行關閉程式碼，直接跳出．", DBOperatorException.CODE.CLOSING);
			case CLOSED:// 已經關閉，直接跳出
				throw new DBOperatorException("已經關閉，直接跳出．", DBOperatorException.CODE.CLOSED);
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
					e.printStackTrace();
				}
				pool.remove(0);
			}
		}
		state = STATE.CLOSED;
	}

}
