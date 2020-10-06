package idv.lingerkptor.util.DBOperator;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBTest {

	private DataAccessTemplate template = new DataAccessTemplate();

	@Test
	public void a_connectDB() {
		Connection conn = null;
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
	}

	@Test
	public void b_createTable() {
		PreparedStatementCreator createtable = new CreateTableSQL();
		template.update(createtable);
	}

	@Test
	public void c_addData() {
		AddDataSQL addData = new AddDataSQL();
		addData.addData("test", 555);
		addData.addData("test2", 666);
		template.update(addData);
	}

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

	@Test
	public void e_updateData() {
		UpdateDataSQL updateData = new UpdateDataSQL();
		updateData.addData("test", 999);
		template.update(updateData);
	}

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

	@Test
	public void g_deleteData() {
		DeleteDataSQL deleteData = new DeleteDataSQL();
		deleteData.addData("test2");
		template.update(deleteData);
	}

	@Test
	public void h_queryData() {
		QueryDataSQL queryData = new QueryDataSQL();
		QueryDataResult checkData = new QueryDataResult();
		checkData.addCheckData("test", 999);
		template.query(queryData, checkData);
		Assert.assertEquals("資料量不一致", true, checkData.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData.checkData());
	}

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
		 * 下面測試交易失敗結果
		 * 當更新後發現已經有test22的資料，但是test22是primary key無法再新增，整筆交易會失敗．
		 * 所以test123並不會寫入資料庫，在測試中會有SQL Exception 內容是Primary key Constraint error．
		 * 
		 * */
		transaction.goal("test123", 012);
		transaction.addTwo("test22", 888);
		template.update(transaction);

		template.query(queryData2, checkData2);
		Assert.assertEquals("資料量不一致", true, checkData2.checkSize());
		Assert.assertEquals("資料內容不一致", true, checkData2.checkData());
	}

	@Test
	public void j_dropTable() {
		PreparedStatementCreator droptable = new DropTableSQL();
		template.update(droptable);
	}

	@Test
	public void k_closeDB() {
		ConnectPool.close();
		Assert.assertEquals(ConnectPool.STATE.CLOSED, ConnectPool.getState());

	}
}
