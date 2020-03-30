package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DeleteDataSQL implements PreparedStatementCreator {
	Set<String>keys = new HashSet<String>();

	@Override
	public PreparedStatement createPreparedStatement(Connection conn) {
		String SQL = "Delete from Test where test = ? ;";
		PreparedStatement preps = null;
		try {
			if (!keys.isEmpty()) {
				preps = conn.prepareStatement(SQL);

				for (String key : keys) {
					preps.setString(1, key);
					preps.addBatch();
				}
			}

			return preps;
		} catch (SQLException e) {
			throw new DataAccessException("SQL Exception in DeleteDataSQL Class." + e.getMessage());

		}
	}

	public void addData(String key) {
		keys.add(key);
	}
}
