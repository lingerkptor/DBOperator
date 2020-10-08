package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DataAccessTemplate {

	private PreparedStatement stat = null;
	private ResultSet rs = null;
	private Connection conn = null;
	private ConnectPool pool = null;

	@SuppressWarnings("unused")
	private DataAccessTemplate() {
	}

	public DataAccessTemplate(ConnectPool pool) {
		this.pool = pool;
	}

	/**
	 * 連接資料庫
	 */
	private void doConnect() {
		try {
			this.conn = pool.getConnection();
		} catch (DBOperatorException e) {
			/**
			 * 請查看錯誤碼及訊息,
			 */
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
		}
	}

	/**
	 * 執行查詢
	 * 
	 * @param prepared SQL實作
	 * @param handler  查詢結果逐行如何處理
	 */
	public void query(PreparedStatementCreator prepared, RowCallbackHandler handler) {
		this.doConnect();
		try {
			conn.setAutoCommit(true);
			stat = prepared.createPreparedStatement(conn);

			rs = stat.executeQuery();
			while (rs.next()) {
				handler.processRow(rs);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
	public void update(PreparedStatementCreator prepared) {
		this.doConnect();
		try {
			conn.setAutoCommit(false);
			stat = prepared.createPreparedStatement(conn);
			stat.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// rollback出錯
				e1.printStackTrace();
			}
		} finally {
			// 歸還connect
			pool.returnConnection(conn);
		}
	}
}
