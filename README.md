# DBOperator

## 教學 
### STEP1 實作DatabaseConfig 介面
 請參考 /src/test/java/idv/lingekrptor/util/DBOperator/DBConfig.java 作為範例
 
### STEP2  設定資料庫
```java
// 建立一個連接池
ConnectPool pool = new ConnectPool();
// DB設定檔建立
DatabaseConfig config = new DBConfig();
// 將資料庫交付給連接池管理
pool.setDatabase(Database.getDatabase(config));
```

### STEP3  實作PreparedStatementCreator 介面
詳細的使用可參考/src/test/java/idv/lingekrptor/util/DBOperator/DBTest這個類別，有新增、修改、查詢、刪除範例．

### STEP4  實作RowCallbackHandler 介面 
這個介面基本上只有查詢的時候才會使用，用來處理單一row資料處理
可以參考 /src/test/java/idv/lingekrptor/util/DBOperator/QueryDataResult.java

### STEP5 關閉資料庫

```java
try {
	ConnectPool.close();
} catch (DBOperatorException e) {
	switch (e.getState()) {
	// ConnectPool關閉中
	case CLOSING:
	// 尚未給定資料庫
	case UNREADY:
	// ConnectPool已關閉
	case CLOSED:
	default:
		break;
	}
	e.printStackTrace();
}
```


練習Git 裡面的記錄，如果有相同的message，代表同時更改，但是忘記下指令(通常是git add (修改的檔案或新增的檔案))

