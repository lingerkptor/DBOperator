package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
/**
 * 刪除資料
 * @author lingerkptor
 *
 */
public class DeleteDataSQL implements PreparedStatementCreator {
	Set<String> keys = new HashSet<String>();

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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return preps;
	}

	public void addData(String key) {
		keys.add(key);
	}
}
