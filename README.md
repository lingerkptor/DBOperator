# DBOperator

## 教學 
### STEP1 實作DatabaseConfig 介面
 
 ```java
 
 /**
  Example implements DatabaseConfig
 **/
 
 package idv.lingerkptor.util.DBOperator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DBConfig implements DatabaseConfig {
	private Properties dbprops;
	private String driver;
	private String driverUrl;
	private String url;
	private String account;
	private String password;
	private int maxConnection;

	public DBConfig() {
		File propsfile = new File("./config/db.properties");
		if (!propsfile.exists())
			propsfile = new File("./config/db.default.properties");
		dbprops = new Properties();
		try {
			this.dbprops.load(new FileInputStream(propsfile));
			this.account = dbprops.getProperty("account");
			this.driver = this.dbprops.getProperty("driver");
			this.driverUrl = this.dbprops.getProperty("driverUrl");
			this.password = this.dbprops.getProperty("password");
			this.url = this.dbprops.getProperty("url");
			this.maxConnection = Integer.parseInt(this.dbprops.getProperty("maxConnection"));
		} catch (FileNotFoundException e) {
			System.err.println("沒找到檔案");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDriver() {
		return driver;
	}

	@Override
	public String getDriverUrl() {
		return driverUrl;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public int getMaxConnection() {
		return maxConnection;
	}

}
 
 ```

### STEP2  連接資料庫

```java
 // DB設定檔建立
DatabaseConfig config = new DBConfig();
// 將設定檔餵給DB，如果沒有餵，在ConnectPool內會拋出Exception 
Database.setDatabaseConfig(config);
try {
	ConnectPool.setDatabase(Database.getDatabase());
} catch (Exception e) {
	e.printStackTrace();
}
conn = ConnectPool.getConnection();
Assert.assertNotNull(conn);
ConnectPool.returnConnection(conn);
```

### STEP3  查詢
實作PreparedStatementCreator 介面

```java
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

```
實作RowCallbackHandler 介面

```java
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

```

在您想使用這兩個類別的地方

```java
DataAccessTemplate template = new DataAccessTemplate();
// 查詢用的語句
QueryDataSQL queryData = new QueryDataSQL();
//查詢的結果處理
QueryDataResult checkData = new QueryDataResult();

checkData.addCheckData("test", 555);
checkData.addCheckData("test2", 666);
// 執行查詢
template.query(queryData, checkData);
```

詳細的使用可參考idv.lingerkptor.util.DBOperator.DBTest這個類別，有新增、修改、查詢、刪除範例．


練習Git 裡面的記錄，如果有相同的message，代表同時更改，但是忘記下指令(通常是git add (修改的檔案或新增的檔案))
