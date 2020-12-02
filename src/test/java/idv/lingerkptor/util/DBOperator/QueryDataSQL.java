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
			preps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return preps;
	}

}
