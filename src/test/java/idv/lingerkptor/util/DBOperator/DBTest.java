package idv.lingerkptor.util.DBOperator;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBTest {

	private DataAccessTemplate template = new DataAccessTemplate();

	/**
	 * 連接資料庫 如果資料庫已關閉，重複以下動作可重新啟用
	 */
	@Test
	public void a_connectDB() {
// DB設定檔建立
DatabaseConfig config = new DBConfig();
// 交付設定檔
Database.setDatabaseConfig(config);
try {
	/**
	 * 如果資料庫已關閉，執行這下一行會可回復<br/>
	 * 如果沒有餵設定檔，在ConnectPool內會拋出DBOperatorException
	 */
	ConnectPool.setDatabase(Database.getDatabase());
} catch (DBOperatorException e) {
	switch (e.getState()) {
	case CONFIGISNULL:
		// 設定檔沒有餵該怎樣處理
	default:
		break;
	}
	e.printStackTrace();
}
/**
 * ConnectPool中如果connect目前都在使用中，會跳出DBOperatorException
 */
Connection conn = null;
try {
	conn = ConnectPool.getConnection();
} catch (DBOperatorException e) {
	/**
	 * 請查看錯誤碼及訊息,
	 */
	switch (e.getState()) {
	// ConnectPool關閉中
	case CLOSING:
		break;
	// 尚未給定資料庫
	case UNREADY:
		break;
	// ConnectPool已關閉
	case CLOSED:
		break;
	// Connection已滿
	case CONNECTFULL:
		break;
	default:
		break;
	}
	e.printStackTrace();
}
		Assert.assertNotNull(conn);
		ConnectPool.returnConnection(conn);
	}

	/**
	 * 新增資料表
	 */
	@Test
	public void b_createTable() {
		PreparedStatementCreator createtable = new CreateTableSQL();
		template.update(createtable);
	}

	/**
	 * 新增兩筆資料
	 */
	@Test
	public void c_addData() {
		AddDataSQL addData = new AddDataSQL();
		addData.addData("test", 555);
		addData.addData("test2", 666);
		template.update(addData);
	}

	/**
	 * 查詢結果新增結果 *
	 */
	@Test
	public void d_queryData() {
		QueryDataSQL queryData = new QueryDataSQL();
		QueryDataResult checkData = new QueryDataResult();
		checkData.addCheckData("test", 555);
		checkData.addCheckData("test2", 666);
		template.query(queryData, checkData);
		Assert.assertEquals("資料量不一致", true, checkData.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData.checkData());
	}

	/**
	 * 更新資料
	 */
	@Test
	public void e_updateData() {
		UpdateDataSQL updateData = new UpdateDataSQL();
		updateData.addData("test", 999);
		template.update(updateData);
	}

	/**
	 * 查詢資料是否更新
	 */
	@Test
	public void f_queryData() {
		QueryDataSQL queryData = new QueryDataSQL();
		QueryDataResult checkData = new QueryDataResult();
		checkData.addCheckData("test", 999);
		checkData.addCheckData("test2", 666);
		template.query(queryData, checkData);
		Assert.assertEquals("資料量不一致", true, checkData.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData.checkData());
	}

	/**
	 * 刪除資料
	 */
	@Test
	public void g_deleteData() {
		DeleteDataSQL deleteData = new DeleteDataSQL();
		deleteData.addData("test2");
		template.update(deleteData);
	}

	/**
	 * 查詢資料是否刪除
	 */
	@Test
	public void h_queryData() {
		QueryDataSQL queryData = new QueryDataSQL();
		QueryDataResult checkData = new QueryDataResult();
		checkData.addCheckData("test", 999);
		template.query(queryData, checkData);
		Assert.assertEquals("資料量不一致", true, checkData.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData.checkData());
	}

	/**
	 * 交易案例
	 */
	@Test
	public void i_TransactionTable() {
		TransactionSQL transaction = new TransactionSQL();
		transaction.goal("test", 555);
		transaction.addTwo("test11", 666);
		template.update(transaction);

		QueryDataSQL queryData = new QueryDataSQL();
		QueryDataResult checkData = new QueryDataResult();
		checkData.addCheckData("test", 555);
		template.query(queryData, checkData);
		Assert.assertEquals("資料量不一致", true, checkData.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData.checkData());

		transaction.goal("test11", 666);
		transaction.addTwo("test22", 777);
		template.update(transaction);

		QueryDataSQL queryData2 = new QueryDataSQL();
		QueryDataResult checkData2 = new QueryDataResult();
		checkData2.addCheckData("test", 555);
		checkData2.addCheckData("test11", 666);
		checkData2.addCheckData("test22", 777);
		template.query(queryData2, checkData2);
		Assert.assertEquals("資料量不一致", true, checkData2.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData2.checkData());

		/*
		 * 下面測試交易失敗結果 當更新後發現已經有test22的資料，但是test22是primary key無法再新增，整筆交易會失敗．
		 * 所以test123並不會寫入資料庫，在測試中會有SQL Exception 內容是Primary key Constraint error．
		 * 
		 */
		transaction.goal("test123", 012);
		transaction.addTwo("test22", 888);
		template.update(transaction);

		template.query(queryData2, checkData2);
		Assert.assertEquals("資料量不一致", true, checkData2.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData2.checkData());
	}

	/**
	 * 刪除資料表
	 */
	@Test
	public void j_dropTable() {
		PreparedStatementCreator droptable = new DropTableSQL();
		template.update(droptable);
	}

	/**
	 * 關閉資料庫
	 */
	@Test
	public void k_closeDB() {
		try {
			ConnectPool.close();
		} catch (DBOperatorException e) {
			switch (e.getState()) {
			// ConnectPool關閉中
			case CLOSING:
				break;
			// 尚未給定資料庫
			case UNREADY:
				break;
			// ConnectPool已關閉
			case CLOSED:
				break;
			default:
				break;

			}
			e.printStackTrace();
		}
		Assert.assertEquals(ConnectPool.STATE.CLOSED, ConnectPool.getState());
	}

}
