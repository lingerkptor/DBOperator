package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
/**
 * SQL指令
 * @author lingerkptor
 *
 */
public interface PreparedStatementCreator {
	public PreparedStatement createPreparedStatement(Connection conn) ;
}
