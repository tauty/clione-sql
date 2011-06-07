/*
 * Copyright 2011 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tetz42.clione;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.clione.SQLManager.*;
import static tetz42.test.Util.*;
import static tetz42.util.ObjDumper4j.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tetz42.clione.exception.ConnectionNotFoundException;
import tetz42.clione.exception.WrapException;
import tetz42.clione.util.ResultMap;

public class SQLManagerTest {

	@BeforeClass
	public static void start() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
	}

	Connection con;

	@Before
	public void setUp() throws SQLException {
		ResourceBundle bundle = ResourceBundle.getBundle("db");
		con = DriverManager.getConnection(bundle.getString("url"), bundle
				.getString("user"), bundle.getString("pass"));
		con.setAutoCommit(false);
	}

	@After
	public void tearDown() throws SQLException {
		if (con.isValid(0)) {
			con.rollback();
			con.close();
		}
		setThreadConnection(null);
	}

	@Test
	public void con() throws IOException, SQLException {
		// Null Instance Connection
		SQLManager manager = sqlManager();
		try {
			assertThat(manager.con(), is(this.con));
			fail();
		} catch (ConnectionNotFoundException e) {
		}
		assertNull(getThreadConnection());

		// Set Thread Connection
		setThreadConnection(this.con);
		assertThat(getThreadConnection(), is(this.con));

		// Null Instance Connection
		manager = sqlManager();
		assertThat(manager.con(), is(this.con));
		assertThat(getThreadConnection(), is(this.con));

		// Set Instance Connection
		ResourceBundle bundle = ResourceBundle.getBundle("db");
		Connection anotherCon = DriverManager.getConnection(bundle
				.getString("url"), bundle.getString("user"), bundle
				.getString("pass"));
		manager = sqlManager(anotherCon);
		assertThat(manager.con(), is(anotherCon));
		assertThat(getThreadConnection(), is(this.con));

		// Clear Thread Connection
		setThreadConnection(null);
		assertThat(manager.con(), is(anotherCon));
		assertNull(getThreadConnection());

		// Close Connection
		manager.closeConnection();
	}

	@Test
	public void find_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Select.sql");

		Tameshi tameshi = man.find(Tameshi.class, params("$age", 34));
		System.out.println(dumper(tameshi).superSafe());
		assertEqualsWithFile(tameshi, getClass(), "find_by_1_param");
	}

	@Test
	public void find_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Select.sql");

		Tameshi tameshi = man.find(Tameshi.class);
		System.out.println(dumper(tameshi).superSafe());
		assertEqualsWithFile(tameshi, getClass(), "find_by_no_param");
	}

	@Test
	public void findAll_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Select.sql");

		List<Tameshi> list = man.findAll(Tameshi.class, params("$age", 31));
		System.out.println(dumper(list).superSafe());
		assertEqualsWithFile(list, getClass(), "findAll_by_1_param");
	}

	@Test
	public void findAll_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Select.sql");

		List<Tameshi> list = man.findAll(Tameshi.class);
		System.out.println(dumper(list).superSafe());
		assertEqualsWithFile(list, getClass(), "findAll_by_no_param");
	}

	@Test(expected = SQLException.class)
	public void findAll_using_wrongSQL() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(),
				"sql/WrongSelect.sql");
		man.findAll(Tameshi.class);
	}

	@Test
	public void findmap_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Select.sql");

		Map<String, Object> map = man.find(params("$age", 34));
		System.out.println(dumper(map).superSafe());
		assertEqualsWithFile(map, getClass(), "findmap_by_1_param");
	}

	@Test
	public void findmap_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Select.sql");

		Map<String, Object> map = man.find();
		System.out.println(dumper(map).superSafe());
		assertEqualsWithFile(map, getClass(), "findmap_by_no_param");
	}

	@Test
	public void findAllmap_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Select.sql");

		List<ResultMap> list = man.findAll(params("$age", 31));
		System.out.println(dumper(list).superSafe());
		assertEqualsWithFile(list, getClass(), "findAllmap_by_1_param");
	}

	@Test
	public void findAllmap_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Select.sql");

		List<ResultMap> list = man.findAll();
		System.out.println(dumper(list).superSafe());
		assertEqualsWithFile(list, getClass(), "findAllmap_by_no_param");
	}

	@Test(expected = SQLException.class)
	public void findAllmap_using_wrongSQL() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(),
				"sql/WrongSelect.sql");
		man.findAll();
	}

	@Test
	public void update_by_age_10() throws Exception {
		setThreadConnection(con);
		int count = sqlManager().useFile(getClass(), "sql/Update.sql").update(
				params("$age", 10));
		assertThat(count, is(0));
		List<Tameshi> list = sqlManager().useFile(getClass(), "sql/Select.sql")
				.findAll(Tameshi.class);
		System.out.println(dumper(list).superSafe());
		assertEqualsWithFile(list, getClass(), "findAll_by_no_param");
	}

	@Test
	public void update_by_age_31() throws Exception {
		setThreadConnection(con);
		int count = sqlManager().useFile(getClass(), "sql/Update.sql").update(
				params("$age", 31));
		assertThat(count, is(2));
		List<Tameshi> list = sqlManager().useFile(getClass(), "sql/Select.sql")
				.findAll(Tameshi.class);
		System.out.println(dumper(list).superSafe());
		assertEqualsWithFile(list, getClass(), "update_by_age_31");
	}

	@Test
	public void update_by_no_param() throws Exception {
		setThreadConnection(con);
		int count = sqlManager().useFile(getClass(), "sql/Update.sql").update();
		assertThat(count, is(5));
		List<Tameshi> list = sqlManager().useFile(getClass(), "sql/Select.sql")
				.findAll(Tameshi.class);
		System.out.println(dumper(list).superSafe());
		assertEqualsWithFile(list, getClass(), "update_by_no_param");
	}

	@Test(expected = SQLException.class)
	public void update_using_wrongSQL() throws IOException, SQLException {
		setThreadConnection(con);
		sqlManager().useFile(getClass(), "sql/WrongUpdate.sql").update();
	}

	@Test
	public void find_employee_by_id_1() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(),
				"sql/EmployeeSelect.sql");

		Employee employee = man.find(Employee.class, params("ID", 1));
		System.out.println(dumper(employee).superSafe());
		System.out.println(man.getSQLInfo());
		assertEqualsWithFile(employee, getClass(), "find_employee_by_id_1");
	}

	@Test
	public void findAll_employee_by_no_0_and_3() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(),
				"sql/EmployeeSelect.sql");

		List<Employee> list = man.findAll(Employee.class,
				params("$NO1", 100000).$("$NO2", 100003));
		System.out.println(dumper(list).superSafe());
		System.out.println(man.getSQLInfo());
		assertEqualsWithFile(list, getClass(), "findAll_employee_by_no_2_and_3");
	}

	@Test
	public void find_employee_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(),
				"sql/EmployeeSelect.sql");

		Employee employee = man.find(Employee.class);
		System.out.println(dumper(employee).superSafe());
		System.out.println(man.getSQLInfo());
		assertEqualsWithFile(employee, getClass(), "find_employee_by_no_param");
		// man.closeConnection();
	}

	@Test
	public void sqlManager_by_path() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(
				"tetz42/clione/sql/Sample.sql");

		man.genSql(params("TEST1", 10).$("TEST2", 100).$("TEST3", 1000));
		System.out.println(man.getSql());
		System.out.println(man.getParams());
		assertEqualsWithFile(man.getSql(), getClass(), "genSql_by_sample");
	}

	@Test
	public void sqlManager_by_path_and_con() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(
				"tetz42/clione/sql/Sample.sql");

		man.genSql(params("TEST1", 10).$("TEST2", 100).$("TEST3", 1000));
		System.out.println(man.getSql());
		System.out.println(man.getParams());
		assertEqualsWithFile(man.getSql(), getClass(), "genSql_by_sample");
	}

	@Test
	public void sqlManager_by_istream() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useStream(
				getClass().getResourceAsStream("sql/Sample.sql"));

		man.genSql(params("TEST1", 10).$("TEST2", 100).$("TEST3", 1000));
		System.out.println(man.getSql());
		System.out.println(man.getParams());
		assertEqualsWithFile(man.getSql(), getClass(), "genSql_by_sample");
	}

	@Test
	public void sqlManager_by_istream_and_con() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useStream(
				getClass().getResourceAsStream("sql/Sample.sql"));

		man.genSql(params("TEST1", 10).$("TEST2", 100).$("TEST3", 1000));
		System.out.println(man.getSql());
		System.out.println(man.getParams());
		assertEqualsWithFile(man.getSql(), getClass(), "genSql_by_sample");
	}

	@Test(expected = WrapException.class)
	public void cannot_create_instance() throws IOException, SQLException {
		sqlManager(con).useFile(getClass(), "sql/Select.sql").find(
				NoDefaultConstructor.class);
	}

	public static class Tameshi {
		int id;
		String name;
		int age;
	}

	public static class Employee {
		int id;
		int SHAIN_NO;
		String shozokuBuKa;
		String name;
		int age;
	}

	public static class NoDefaultConstructor {
		NoDefaultConstructor(int id) {
			this.id = id;
		}

		int id;
		int SHAIN_NO;
		String shozokuBuKa;
		String name;
		int age;
	}
}
