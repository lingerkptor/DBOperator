package idv.lingerkptor.util.DBOperator;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBTest {

	private Connection conn = null;
	private DataAccessTemplate template = new DataAccessTemplate();

	@Test
	public void a_connectDB() {
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
		Assert.assertEquals("��ƶq���@�P", true, checkData.checkSize());
		Assert.assertEquals("��Ƥ��e���@�P", true, checkData.checkData());
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
		Assert.assertEquals("��ƶq���@�P", true, checkData.checkSize());
		Assert.assertEquals("��Ƥ��e���@�P", true, checkData.checkData());
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
		Assert.assertEquals("��ƶq���@�P", true, checkData.checkSize());
		Assert.assertEquals("��Ƥ��e���@�P", true, checkData.checkData());
	}

	@Test
	public void i_dropTable() {
		PreparedStatementCreator droptable = new DropTableSQL();
		template.update(droptable);
	}
}
