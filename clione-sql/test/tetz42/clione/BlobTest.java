package tetz42.clione;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tetz42.clione.node.LineNodeTest;
import tetz42.clione.setting.Config;
import tetz42.util.IOUtil;

public class BlobTest {

	@BeforeClass
	public static void start() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
	}

	@Before
	public void setUp() throws SQLException {
		ResourceBundle bundle = ResourceBundle.getBundle("db");
		Connection con = DriverManager.getConnection(bundle.getString("url"),
				bundle.getString("user"), bundle.getString("pass"));
		con.setAutoCommit(false);
		setThreadConnection(con);
	}

	@After
	public void tearDown() throws SQLException {
		Connection con = getThreadConnection();
		if (con.isValid(0)) {
			con.rollback();
			con.close();
		}
		setThreadConnection(null);
	}

	@Test
	public void crud_file() {
		SQLManager sqlManager = sqlManager();

		// insert
		int count = sqlManager
				.useSQL("insert into blobtest (id, data) values (/* id */, /* data */)")
				.update(params("id", 1)
					.$("data", LineNodeTest.class.getResourceAsStream("LineNodeTest.txt")));
		assertThat(count, is(1));

		// type not specified(byte array)
		Map<String, Object> map = sqlManager.useSQL("select * from blobtest")
				.find();
		assertEqualsWithFile(map, getClass(), "crud_file_findmap");

		// string
		String data = sqlManager.useSQL("select data from blobtest").find(
				String.class);
		assertEqualsWithFile(data, getClass(), "crud_file_string");

		// find inputstream
		// Note: It can be obtain its contents after closing
		// ResultSet and Statement.
		InputStream in = sqlManager.useSQL("select data from blobtest").find(
				InputStream.class);
		assertEqualsWithFile(
				IOUtil.toString(in, Config.get().SQLFILE_ENCODING), getClass(),
				"crud_file_string");
	}

	// This test case need not be performed every time, so comment out this.
	// @Test
	// public void crud_javafile() throws FileNotFoundException {
	// SQLManager sqlManager = sqlManager();
	//
	// // insert
	// int count = sqlManager
	// .useSQL("insert into blobtest (id, data) values (/* id */, /* data */)")
	// .update(params("id", 1).$("data",
	// new FileInputStream(new File("clione-sql-0.5.0b5.jar"))));
	// assertThat(count, is(1));
	//
	// // find inputstream and copy it.
	// InputStream in = sqlManager.useSQL("select data from blobtest").find(
	// InputStream.class);
	// FileOutputStream out = new FileOutputStream(new File("test.jar"));
	// IOUtil.copy(in, out); // success!
	// }
}
