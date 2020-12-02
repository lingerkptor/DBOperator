package idv.lingerkptor.util.DBOperator;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OperatorTest {
	private static ConnectPool pool = null;

	/**
	 * 連接資料庫 如果資料庫已關閉，重複以下動作可重新啟用
	 */
	@Test
	public void a_connectDB() {
		// DB設定檔建立
		pool = new ConnectPool();
		DatabaseConfig config = new DBConfig();
		try {
			/**
			 * 如果資料庫已關閉，執行這下一行會可回復<br/>
			 * 如果沒有餵設定檔，在ConnectPool內會拋出DBOperatorException
			 */
			pool.setDatabase(Database.getDatabase(config));
		} catch (DBOperatorException e) {
			switch (e.getState()) {
			case UNREADY:
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
			try {
				conn = pool.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (DBOperatorException e) {
			/**
			 * 請查看錯誤碼及訊息,
			 */
			switch (e.getState()) {
			case CLOSING: // ConnectPool關閉中
			case UNREADY: // 尚未給定資料庫
			case CLOSED:// ConnectPool已關閉
			default:
				break;
			}
			e.printStackTrace();
		}
		Assert.assertNotNull(conn);
		pool.returnConnection(conn);
	}

	private DataAccessTemplate template = new DataAccessTemplate(pool);

	/**
	 * 新增資料表
	 */
	@Test
	public void b_createTable() {
		PreparedStatementCreator createtable = new CreateTableSQL();
		try {
			template.update(createtable);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新增兩筆資料
	 */
	@Test
	public void c_addData() {
		AddDataSQL addData = new AddDataSQL();
		addData.addData("test", 555);
		addData.addData("test2", 666);
		try {
			template.update(addData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		try {
			template.query(queryData, checkData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		try {
			template.update(updateData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		try {
			template.query(queryData, checkData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		try {
			template.update(deleteData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查詢資料是否刪除
	 */
	@Test
	public void h_queryData() {
		QueryDataSQL queryData = new QueryDataSQL();
		QueryDataResult checkData = new QueryDataResult();
		checkData.addCheckData("test", 999);
		try {
			template.query(queryData, checkData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Assert.assertEquals("資料量不一致", true, checkData.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData.checkData());
	}

	/**
	 * 交易案例
	 * @throws SQLException 
	 */
	@Test(expected = SQLException.class)
	public void i_TransactionTable() throws SQLException {
		TransactionSQL transaction = new TransactionSQL();
		transaction.goal("test", 555);
		transaction.addTwo("test11", 666);
		try {
			template.update(transaction);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		QueryDataSQL queryData = new QueryDataSQL();
		QueryDataResult checkData = new QueryDataResult();
		checkData.addCheckData("test", 555);
		try {
			template.query(queryData, checkData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Assert.assertEquals("資料量不一致", true, checkData.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData.checkData());

		transaction.goal("test11", 666);
		transaction.addTwo("test22", 777);
		try {
			template.update(transaction);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		QueryDataSQL queryData2 = new QueryDataSQL();
		QueryDataResult checkData2 = new QueryDataResult();
		checkData2.addCheckData("test", 555);
		checkData2.addCheckData("test11", 666);
		checkData2.addCheckData("test22", 777);
		try {
			template.query(queryData2, checkData2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Assert.assertEquals("資料量不一致", true, checkData2.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData2.checkData());

		transaction.goal("test123", 012);
		transaction.addTwo("test22", 888);
		/*
		 * 下面一行測試交易失敗 當更新後發現已經有test22的資料，但是test22是primary key無法再新增，整筆交易會失敗．
		 * 所以test123並不會寫入資料庫，在測試中會有SQL Exception 內容是Primary key Constraint error．
		 */
		try {
			template.update(transaction);
		} catch (SQLException e) {
			throw e;
		}

		try {
			template.query(queryData2, checkData2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Assert.assertEquals("資料量不一致", true, checkData2.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData2.checkData());
	}

	/**
	 * 刪除資料表
	 */
	@Test
	public void j_dropTable() {
		PreparedStatementCreator droptable = new DropTableSQL();
		try {
			template.update(droptable);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 關閉資料庫
	 */
	@Test
	public void k_closeDB() {
		try {
			pool.close();
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
		Assert.assertEquals(ConnectPool.STATE.CLOSED, pool.getState());
	}

}
