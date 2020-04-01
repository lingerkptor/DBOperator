package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataAccessTemplate {

	private PreparedStatement stat = null;
	private ResultSet rs = null;
	private Connection conn = null;
/**
 *  連接資料庫
 */
	private void doConnect() {
		this.conn = ConnectPool.getConnection();
		while (conn == null) {
			try {
				Thread.sleep(1000);
				conn = ConnectPool.getConnection();
			} catch (InterruptedException e) {
				throw new DataAccessException("Connection 被占滿,請重新發送查詢");
			}
		}
	}

	/**
	 * 執行查詢
	 * 
	 * @param prepared SQL實作
	 * @param handler  查詢結果逐行如何處理
	 * @throws DataAccessException
	 */
	public void query(PreparedStatementCreator prepared, RowCallbackHandler handler) throws DataAccessException {
		this.doConnect();

		stat = prepared.createPreparedStatement(conn);
		try {
			conn.setAutoCommit(false);
			rs = stat.executeQuery();
			while (rs.next()) {
				handler.processRow(rs);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("QueryTemplate 中的SQLExeption" + e.getMessage());

		} finally {
			//歸還connect
			ConnectPool.returnConnection(conn);
		}

	}

	/**
	 * 執行Create、update、Delete 
	 * @param prepared SQL實作
	 * @throws DataAccessException
	 */
	public void update(PreparedStatementCreator prepared) throws DataAccessException {
		this.doConnect();
		stat = prepared.createPreparedStatement(conn);
		try {
			conn.setAutoCommit(false);
			stat.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			throw new DataAccessException("QueryTemplate 中的SQLExeption" + e.getMessage());

		} finally {
			//歸還connect
			ConnectPool.returnConnection(conn);
		}
	}
}
