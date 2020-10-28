package idv.lingerkptor.util.DBOperator;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * 每筆資料如何處理
 * @author lingerkptor
 *
 */
public interface RowCallbackHandler {
	public void processRow(ResultSet rs) throws SQLException;
}
