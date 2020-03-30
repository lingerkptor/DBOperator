package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateDataSQL implements PreparedStatementCreator {
	Map<String, Integer> datas = new HashMap<String, Integer>();

	@Override
	public PreparedStatement createPreparedStatement(Connection conn) {
		String SQL = "Update Test Set inttest = ? where test = ? ;";
		PreparedStatement preps = null;
		try {
			if (!datas.isEmpty()) {
				preps = conn.prepareStatement(SQL);

				for (String key : datas.keySet()) {
					preps.setInt(1, datas.get(key));
					preps.setString(2, key);
					preps.addBatch();
				}
			}

			return preps;
		} catch (SQLException e) {
			throw new DataAccessException("SQL Exception in UpdateDataSQL Class." + e.getMessage());

		}
	}

	public void addData(String string, int i) {
		datas.put(string, i);
	}

}
