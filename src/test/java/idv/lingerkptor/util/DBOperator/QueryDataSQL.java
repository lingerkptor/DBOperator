package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryDataSQL implements PreparedStatementCreator {

	@Override
	public PreparedStatement createPreparedStatement(Connection conn) {
		String SQL = "select * from test ;";
		PreparedStatement preps = null;
		try {
			preps = conn.prepareStatement(SQL);
			preps.addBatch();
			return preps;
		} catch (SQLException e) {
			throw new DataAccessException("SQL Exception in QueryDataSQL Class. \n" + e.getMessage());
		}

	}

}
