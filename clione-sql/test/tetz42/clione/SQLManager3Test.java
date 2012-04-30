package tetz42.clione;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tetz42.clione.SQLManagerTest.Employee;
import tetz42.util.Pair;

public class SQLManager3Test {

	@Before
	public void setUp() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("db");
		Connection con = DriverManager.getConnection(bundle.getString("url"),
				bundle.getString("user"), bundle.getString("pass"));
		con.setAutoCommit(false);
		setThreadConnection(con);
	}

	@After
	public void tearDown() throws Exception {
		Connection con = getThreadConnection();
		if (con.isValid(0)) {
			con.rollback();
			con.close();
		}
		setThreadConnection(null);
	}

	@Test
	public void if_block_true() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<Employee> list = sqlManager.useFile(getClass(), "IfBlock.sql")
				.findAll(Employee.class, params("cond", "o.com"));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"if_block_true_sql");
		assertEqualsWithFile(list, getClass(), "if_block_true");
	}

	@Test
	public void if_block_true_contains_persent() throws IOException,
			SQLException {
		SQLManager sqlManager = sqlManager();
		List<Employee> list = sqlManager.useFile(getClass(), "IfBlock.sql")
				.findAll(Employee.class, params("cond", "%o_com"));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"if_block_true_contains_persent_sql");
		assertEqualsWithFile(list, getClass(), "if_block_true_contains_persent");
	}

	@Test
	public void if_block_false() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<Employee> list = sqlManager.useFile(getClass(), "IfBlock.sql")
				.findAll(Employee.class, params("ids", 1, 2));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"if_block_false_sql");
		assertEqualsWithFile(list, getClass(), "if_block_false");
	}

	@Test
	public void if_block_null() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<Employee> list = sqlManager.useFile(getClass(), "IfBlock.sql")
				.findAll(Employee.class);
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"if_block_null_sql");
		assertEqualsWithFile(list, getClass(), "if_block_null");
	}

	@Test
	public void if_line_block_true() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<Employee> list = sqlManager.useFile(getClass(), "IfLineBlock.sql")
				.findAll(Employee.class, params("cond", "o.com"));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"if_line_block_true_sql");
		assertEqualsWithFile(list, getClass(), "if_line_block_true");
	}

	@Test
	public void if_line_block_true_contains_persent() throws IOException,
			SQLException {
		SQLManager sqlManager = sqlManager();
		List<Employee> list = sqlManager.useFile(getClass(), "IfLineBlock.sql")
				.findAll(Employee.class, params("cond", "%o_com"));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"if_line_block_true_contains_persent_sql");
		assertEqualsWithFile(list, getClass(),
				"if_line_block_true_contains_persent");
	}

	@Test
	public void if_line_block_false() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<Employee> list = sqlManager.useFile(getClass(), "IfLineBlock.sql")
				.findAll(Employee.class, params("ids", 1, 2));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"if_line_block_false_sql");
		assertEqualsWithFile(list, getClass(), "if_line_block_false");
	}

	@Test
	public void if_line_block_null() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<Employee> list = sqlManager.useFile(getClass(), "IfLineBlock.sql")
				.findAll(Employee.class);
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"if_line_block_null_sql");
		assertEqualsWithFile(list, getClass(), "if_line_block_null");
	}

	@Test
	public void indent_whitespace_and_tab() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();

		List<Employee> list = sqlManager.useFile(getClass(),
				"IndentBlankAndTab.sql").findAll(Employee.class);
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"indent_whitespace_and_tab1_sql");
		assertEqualsWithFile(list, getClass(), "indent_whitespace_and_tab1");

		list = sqlManager.useFile(getClass(), "IndentBlankAndTab.sql").findAll(
				Employee.class, params("cond2", ".com"));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"indent_whitespace_and_tab2_sql");
		assertEqualsWithFile(list, getClass(), "indent_whitespace_and_tab2");

		list = sqlManager.useFile(getClass(), "IndentBlankAndTab.sql").findAll(
				Employee.class, params("cond1", ".com").$("cond2", ".com"));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"indent_whitespace_and_tab3_sql");
		assertEqualsWithFile(list, getClass(), "indent_whitespace_and_tab3");

		list = sqlManager.useFile(getClass(), "IndentBlankAndTab.sql").findAll(
				Employee.class, params("cond1", ".com"));
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
				"indent_whitespace_and_tab4_sql");
		assertEqualsWithFile(list, getClass(), "indent_whitespace_and_tab4");
	}

	@Test
	public void useSQL() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<Integer> list = sqlManager.useSQL("select id from employees")
				.findAll(Integer.class);
		assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(), "useSQL_sql");
		assertEqualsWithFile(list, getClass(), "useSQL");
	}

	@Test
	public void include_no_param() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		Pair<String, List<Object>> pair = sqlManager.useFile(getClass(),
				"Include.sql").genSqlAndParams();
		// List<ResultMap> list = sqlManager.useFile(getClass(), "Include.sql")
		// .findAll();
		// assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
		// "include_no_param_sql");
		assertEqualsWithFile(pair, getClass(), "include_no_param");
	}

	@Test
	public void include_namePrefix_positiveEmpty() throws IOException,
			SQLException {
		SQLManager sqlManager = sqlManager();
		Pair<String, List<Object>> pair = sqlManager.useFile(getClass(),
				"Include.sql").genSqlAndParams(params("namePrefix", ""));
		assertEqualsWithFile(pair, getClass(),
		"include_namePrefix_positiveEmpty");
		// assertEqualsWithFile(pair.getFirst(), getClass(),
		// "include_namePrefix_positiveEmpty_sql");
		// assertEqualsWithFile(pair.getSecond().toString(), getClass(),
		// "include_namePrefix_positiveEmpty_params");
	}

	@Test
	public void include_namePrefix_negativeEmpty() throws IOException,
			SQLException {
		SQLManager sqlManager = sqlManager();
		Pair<String, List<Object>> pair = sqlManager
				.useFile(getClass(), "Include.sql").emptyAsNegative()
				.genSqlAndParams(params("namePrefix", ""));
		assertEqualsWithFile(pair, getClass(),
				"include_namePrefix_negativeEmpty");
	}

	@Test
	public void includeon_namePrefix_positiveEmpty() throws IOException,
			SQLException {
		SQLManager sqlManager = sqlManager();
		Pair<String, List<Object>> pair = sqlManager.useFile(getClass(),
				"Include.sql").genSqlAndParams(
				paramsOn("isEmp").$("namePrefix", ""));
		assertEqualsWithFile(pair, getClass(),
				"includeon_namePrefix_positiveEmpty");
	}

	@Test
	public void includeon_namePrefix_negativeEmpty() throws IOException,
			SQLException {
		SQLManager sqlManager = sqlManager();
		// List<ResultMap> list = sqlManager.useFile(getClass(), "Include.sql")
		// .emptyAsNegative()
		// .findAll(paramsOn("isEmp").$("namePrefix", ""));
		Pair<String, List<Object>> pair = sqlManager
				.useFile(getClass(), "Include.sql").emptyAsNegative()
				.genSqlAndParams(paramsOn("isEmp").$("namePrefix", ""));
		// assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
		// "includeon_namePrefix_negativeEmpty_sql");
		assertEqualsWithFile(pair, getClass(),
				"includeon_namePrefix_negativeEmpty");
	}
}
