package idv.lingerkptor.util.DBOperator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QueryDataResult implements RowCallbackHandler {

	private Map<String, Integer> checkData = new HashMap<String, Integer>();
	private Map<String, Integer> result = new HashMap<String, Integer>();

	public void  addCheckData(String col,int col2) {
		checkData.put(col, col2);
	}

	@Override
	public void processRow(ResultSet rs) throws SQLException {
		result.put(rs.getString(1), rs.getInt(2));
	}

	public boolean checkSize() {
		return checkData.size() == result.size();
		
	}

	public boolean checkData() {
		if (checkData.size() == result.size()) {
			for (String pk : checkData.keySet()) {
				if (!result.keySet().contains(pk) || !result.get(pk).equals(checkData.get(pk))) {
					return false;
				}
			}
		}
		return true;
	}
}
