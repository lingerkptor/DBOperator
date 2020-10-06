package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TransactionSQL implements PreparedStatementCreator {

	private String goal, two;
	private int value1, value2;

	@Override
	public PreparedStatement createPreparedStatement(Connection conn) {
		String SQL = "select count(*) from Test where test = ?;";
		PreparedStatement preps = null;
		ResultSet rs = null;
		try {
			preps = conn.prepareStatement(SQL);
			preps.setString(1, goal);
			preps.addBatch();
			rs = preps.executeQuery();
			if (rs == null) {
				throw new DataAccessException("ResultSet is Null ");
			}
			int count = rs.getInt(1);
			rs.close();
			if (count > 0) {
				SQL = "Update Test Set inttest = ? where test = ? ;";
				preps = conn.prepareStatement(SQL);
				preps.setInt(1, value1);
				preps.setString(2, goal);
			} else {
				SQL = "Insert into Test Values (?,?)";
				preps = conn.prepareStatement(SQL);
				preps.setString(1, two);
				preps.setInt(2, value2);
				preps.addBatch();
				preps.setString(1, goal);
				preps.setInt(2, value1);
			}
			preps.addBatch();
			return preps;
		} catch (SQLException e) {
			throw new DataAccessException("SQL Exception in TransactionSQL Class." + e.getMessage());
		}

	}

	public void goal(String goal, int inttest) {
		this.goal = goal;
		this.value1 = inttest;

	}

	public void addTwo(String two, int inttest) {
		this.two = two;
		this.value2 = inttest;
	}

}
