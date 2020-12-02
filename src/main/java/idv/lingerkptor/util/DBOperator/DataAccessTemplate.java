package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataAccessTemplate {

	private ConnectPool pool = null;

	@SuppressWarnings("unused")
	private DataAccessTemplate() {
	}

	/**
	 * Constructor
	 * 
	 * @param pool 連接池
	 */
	public DataAccessTemplate(ConnectPool pool) {
		this.pool = pool;
	}

	/**
	 * 取得˙資料庫連接
	 */
	private Connection getConnection() {
		try {
			return pool.getConnection();
		} catch (DBOperatorException e) {
			switch (e.getState()) {
			// ConnectPool關閉中
			case CLOSING:
				// ConnectPool已關閉
			case CLOSED:
				// 尚未給定資料庫
			case UNREADY:
			default:
				e.printStackTrace();
				break;
			}
		} catch (SQLException e) {
			System.out.println("連線失敗");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 執行查詢
	 * 
	 * @param prepared SQL實作
	 * @param handler  查詢結果逐行如何處理
	 */
	public void query(PreparedStatementCreator prepared, RowCallbackHandler handler)
			throws SQLException {
		ResultSet rs = null;
		Connection conn = this.getConnection();
		PreparedStatement stat = null;
		try {
			conn.setAutoCommit(true);
			stat = prepared.createPreparedStatement(conn);

			rs = stat.getResultSet();
			while (rs.next()) {
				handler.processRow(rs);
			}
			rs.close();
		} catch (SQLException e) {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw e;
		} catch (NullPointerException e) {
			e.printStackTrace();
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw e;
		} finally {
			// 歸還connect
			pool.returnConnection(conn);
		}
	}

	/**
	 * 執行Create、update、Delete
	 * 
	 * @param prepared SQL實作
	 */
	public void update(PreparedStatementCreator prepared) throws SQLException {
		Connection conn = this.getConnection();
		try {
			conn.setAutoCommit(false);
			prepared.createPreparedStatement(conn);
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// rollback出錯
				throw e1;
			}
			throw e;
		} finally {
			// 歸還connect
			pool.returnConnection(conn);
		}
	}
}
