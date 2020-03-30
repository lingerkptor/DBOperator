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
 *  �s����Ʈw
 */
	private void doConnect() {
		this.conn = ConnectPool.getConnection();
		while (conn == null) {
			try {
				Thread.sleep(1000);
				conn = ConnectPool.getConnection();
			} catch (InterruptedException e) {
				throw new DataAccessException("Connection �Q�e��,�Э��s�o�e�d��");
			}
		}
	}

	/**
	 * ����d��
	 * 
	 * @param prepared SQL��@
	 * @param handler  �d�ߵ��G�v��p��B�z
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
			throw new DataAccessException("QueryTemplate ����SQLExeption" + e.getMessage());

		} finally {
			//�k��connect
			ConnectPool.returnConnection(conn);
		}

	}

	/**
	 * ����Create�Bupdate�BDelete 
	 * @param prepared SQL��@
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
			throw new DataAccessException("QueryTemplate ����SQLExeption" + e.getMessage());

		} finally {
			//�k��connect
			ConnectPool.returnConnection(conn);
		}
	}
}
