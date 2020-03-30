package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateTableSQL implements PreparedStatementCreator {

	@Override
	public PreparedStatement createPreparedStatement(Connection conn) {
		String SQL = "Create Table Test ( test text private Key, inttest Integer);";
		try {
			PreparedStatement prps = conn.prepareStatement(SQL);
			prps.addBatch();
			return prps;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}