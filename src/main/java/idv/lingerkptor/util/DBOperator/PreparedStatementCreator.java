package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface PreparedStatementCreator {
	public PreparedStatement createPreparedStatement(Connection conn);
}
