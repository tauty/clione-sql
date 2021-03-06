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
import static tetz42.test.Auty.*;
import static tetz42.util.ObjDumper4j.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tetz42.clione.exception.ParameterNotFoundException;
import tetz42.clione.exception.SQLFileNotFoundException;

public class SQLManager2Test {

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

	@Test(expected = SQLFileNotFoundException.class)
	public void sqlManager_sqlfile_not_found() throws IOException, SQLException {
		sqlManager(con).useFile(getClass(), "NotFound.sql");
	}

	@Test(expected = SQLFileNotFoundException.class)
	public void sqlManager_sqlpath_not_found() throws IOException, SQLException {
		sqlManager(con).useFile("NotFound.sql");
	}

	@Test(expected = ParameterNotFoundException.class)
	public void genSql_required_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "Required.sql");
		man.generateSql();
	}

	@Test
	public void genSql_required_by_required_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "Required.sql");
		man.generateSql(params("REQUIRED", "CLIONE"));
		System.out.println(man.getSql());
		System.out.println(man.getParams());
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_required_by_required_param");
	}

	@Test
	public void genSql_notreplace_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "NotReplace.sql");
		man.generateSql(params("NOT_REPLACE", Boolean.TRUE));
		System.out.println(man.getSql());
		System.out.println(man.getParams());
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_notreplace_by_1_param");
	}

	@Test
	public void genSql_notreplace_by_no_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "NotReplace.sql");
		man.generateSql();
		System.out.println(man.getSql());
		System.out.println(man.getParams());
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_notreplace_by_no_param");
	}

	@Test
	public void genSql_replacein_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "In.sql");

		man.generateSql(params("FISH", 10));
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_replacein_by_1_param");
		assertThat(man.getParams().size(), is(2));
		assertThat((Integer) man.getParams().get(0), is(10));
		assertThat((Integer) man.getParams().get(1), is(10));
	}

	@Test
	public void genSql_replacein_by_list_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "In.sql");

		man.generateSql(params("FISH", Arrays.asList("octopus", "squid",
				"sea cucumber")));
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_replacein_by_list_param");
		assertThat(man.getParams().size(), is(6));
		assertThat((String) man.getParams().get(0), is("octopus"));
		assertThat((String) man.getParams().get(1), is("squid"));
		assertThat((String) man.getParams().get(2), is("sea cucumber"));
		assertThat((String) man.getParams().get(3), is("octopus"));
		assertThat((String) man.getParams().get(4), is("squid"));
		assertThat((String) man.getParams().get(5), is("sea cucumber"));
	}

	@Test
	public void genSql_replacein_by_ary_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "In.sql");

		man.generateSql(params("FISH", new String[] { "octopus", "squid",
				"sea cucumber" }));
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_replacein_by_ary_param");
		assertThat(man.getParams().size(), is(6));
		assertThat((String) man.getParams().get(0), is("octopus"));
		assertThat((String) man.getParams().get(1), is("squid"));
		assertThat((String) man.getParams().get(2), is("sea cucumber"));
		assertThat((String) man.getParams().get(3), is("octopus"));
		assertThat((String) man.getParams().get(4), is("squid"));
		assertThat((String) man.getParams().get(5), is("sea cucumber"));
	}

	@Test
	public void genSql_replacein_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "In.sql");

		man.generateSql();
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_replacein_by_no_param");
		assertThat(man.getParams().size(), is(0));
	}

	@Test
	public void genSql_replacein_by_empty_list_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "In.sql");

		man.generateSql(params("FISH", Arrays.asList()));
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_replacein_by_empty_list_param");
		assertThat(man.getParams().size(), is(0));
	}

	@Test
	public void genSql_replacein_by_empty_ary_param() throws IOException,
			SQLException {
		SQLManager man = sqlManager(con);
		SQLExecutor exec = man.useFile(getClass(), "In.sql");

		exec.generateSql(params("FISH", new String[] {}));
		assertEqualsWithFile(exec.getSql(), getClass(),
				"genSql_replacein_by_empty_ary_param");
		assertEqualsWithFile(man.getSql(), getClass(),
				"genSql_replacein_by_empty_ary_param");
		assertThat(exec.getParams().size(), is(0));
	}

	// No exception is thrown when no default value found since parser has
	// changed.
	// @Test(expected = SQLRuntimeException.class)
	// public void genSql_have_no_default_value() throws Exception {
	// try {
	// sqlManager(con).useFile(getClass(), "NoDefault.sql").find();
	// fail();
	// } catch (Exception e) {
	// assertEqualsWithFile(e.getMessage(), getClass(),
	// "genSql_have_no_default_value", 1);
	// throw e;
	// }
	// }

	// No exception is thrown when no default value found since parser has
	// changed.
	// @Test
	// public void genSql_have_no_default_value2() throws IOException,
	// SQLException {
	// List<ResultMap> list = sqlManager(con).useFile(getClass(),
	// "NoDefault2.sql").findAll();
	// System.out.println(dumper(list).superSafe());
	// assertEqualsWithFile(list, getClass(), "genSql_have_no_default_value2");
	// }

	@Test
	public void find_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(SQLManagerTest.class,
				"Select.sql");

		Sample sample = man.find(Sample.class, params("$age", 34));
		System.out.println(dumper(sample).superSafe());
		assertEqualsWithFile(sample, getClass(), "find_by_1_param");
	}

	@Test
	public void find_by_emptyStr_asNull() throws IOException, SQLException {
		SQLManager man = sqlManager(con).asNegative("");
		Sample sample = man.useFile(SQLManagerTest.class, "Select.sql").find(
				Sample.class, params("age", 34).$("name_part", ""));

		assertEqualsWithFile(sample, getClass(), "find_by_emptyStr_asNull");
		assertEqualsWithFile(man.getSql(), getClass(),
				"find_by_emptyStr_asNull_sql");
	}

	@Test
	public void find_by_emptyStr_asNull2() throws IOException, SQLException {
		SQLManager man = sqlManager(con).emptyAsNegative();
		Sample sample = man.useFile(SQLManagerTest.class, "Select.sql").find(
				Sample.class, params("age", 34).$("name_part", ""));

		assertEqualsWithFile(sample, getClass(), "find_by_emptyStr_asNull");
		assertEqualsWithFile(man.getSql(), getClass(),
				"find_by_emptyStr_asNull_sql");
	}

	@Test
	public void find_by_emptyStr_asNull3() throws IOException, SQLException {
		SQLManager man = sqlManager(con).asNegative("");
		Cond cond = new Cond();
		cond.age = 34;
		cond.name_part = "";
		Sample sample = man.useFile(SQLManagerTest.class, "Select.sql").find(
				Sample.class, cond);

		assertEqualsWithFile(man.getSql(), getClass(),
				"find_by_emptyStr_asNull_sql");
		assertEqualsWithFile(sample, getClass(), "find_by_emptyStr_asNull");
	}

}

@SuppressWarnings("unused")
class Sample {
	private int id;
	private String name;
	private int age;
}

class Cond {
	int age;
	String name_part;
}
