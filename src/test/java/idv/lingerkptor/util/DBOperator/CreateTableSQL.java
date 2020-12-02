package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 新增資料表
 * 
 * @author lingerkptor
 *
 */
public class CreateTableSQL implements PreparedStatementCreator {

	@Override
	public PreparedStatement createPreparedStatement(Connection conn) {
		String SQL = "Create Table Test ( test char(20) PRIMARY KEY, inttest Integer);";
		try {
			PreparedStatement prps = conn.prepareStatement(SQL);
			prps.addBatch();
			prps.executeBatch();
			return prps;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}