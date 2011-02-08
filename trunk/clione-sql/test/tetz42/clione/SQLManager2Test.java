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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tetz42.clione.exception.ParameterNotFoundException;
import tetz42.clione.exception.SQLFormatException;

public class SQLManager2Test {

	@BeforeClass
	public static void start() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
	}

	Connection con;

	@Before
	public void setUp() throws SQLException {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test",
				"root", "rootroot");
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

	@Test(expected = NullPointerException.class)
	public void sqlManager_sqlfile_not_found() throws IOException, SQLException {
		sqlManager(con).useFile(getClass(), "NotFound.sql");
	}

	@Test(expected = NullPointerException.class)
	public void sqlManager_sqlpath_not_found() throws IOException, SQLException {
		sqlManager(con).useFile("NotFound.sql");
	}

	@Test(expected = ParameterNotFoundException.class)
	public void genSql_required_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/Required.sql");
		man.genSql();
	}

	@Test
	public void genSql_required_by_required_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/Required.sql");
		man.genSql(params("REQUIRED", "CLIONE"));
		System.out.println(man.getExecutedSql());
		System.out.println(man.getExecutedParams());
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_required_by_required_param");
	}

	@Test
	public void genSql_notreplace_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/NotReplace.sql");
		man.genSql(params("NOT_REPLACE", Boolean.TRUE));
		System.out.println(man.getExecutedSql());
		System.out.println(man.getExecutedParams());
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_notreplace_by_1_param");
	}

	@Test
	public void genSql_notreplace_by_no_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/NotReplace.sql");
		man.genSql();
		System.out.println(man.getExecutedSql());
		System.out.println(man.getExecutedParams());
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_notreplace_by_no_param");
	}

	@Test
	public void genSql_replacein_by_1_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/In.sql");

		man.genSql(params("FISH", 10));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_replacein_by_1_param");
		assertThat(man.getExecutedParams().size(), is(2));
		assertThat((Integer) man.getExecutedParams().get(0), is(10));
		assertThat((Integer) man.getExecutedParams().get(1), is(10));
	}

	@Test
	public void genSql_replacein_by_list_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/In.sql");

		man.genSql(params("FISH",
				Arrays.asList("octopus", "squid", "sea cucumber")));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_replacein_by_list_param");
		assertThat(man.getExecutedParams().size(), is(6));
		assertThat((String) man.getExecutedParams().get(0), is("octopus"));
		assertThat((String) man.getExecutedParams().get(1), is("squid"));
		assertThat((String) man.getExecutedParams().get(2), is("sea cucumber"));
		assertThat((String) man.getExecutedParams().get(3), is("octopus"));
		assertThat((String) man.getExecutedParams().get(4), is("squid"));
		assertThat((String) man.getExecutedParams().get(5), is("sea cucumber"));
	}

	@Test
	public void genSql_replacein_by_ary_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/In.sql");

		man.genSql(params("FISH", new String[] { "octopus", "squid",
				"sea cucumber" }));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_replacein_by_ary_param");
		assertThat(man.getExecutedParams().size(), is(6));
		assertThat((String) man.getExecutedParams().get(0), is("octopus"));
		assertThat((String) man.getExecutedParams().get(1), is("squid"));
		assertThat((String) man.getExecutedParams().get(2), is("sea cucumber"));
		assertThat((String) man.getExecutedParams().get(3), is("octopus"));
		assertThat((String) man.getExecutedParams().get(4), is("squid"));
		assertThat((String) man.getExecutedParams().get(5), is("sea cucumber"));
	}

	@Test
	public void genSql_replacein_by_no_param() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/In.sql");

		man.genSql();
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_replacein_by_no_param");
		assertThat(man.getExecutedParams().size(), is(0));
	}

	@Test
	public void genSql_replacein_by_empty_list_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/In.sql");

		man.genSql(params("FISH", Arrays.asList()));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_replacein_by_empty_list_param");
		assertThat(man.getExecutedParams().size(), is(0));
	}

	@Test
	public void genSql_replacein_by_empty_ary_param() throws IOException,
			SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql2/In.sql");

		man.genSql(params("FISH", new String[] {}));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_replacein_by_empty_ary_param");
		assertThat(man.getExecutedParams().size(), is(0));
	}

	@Test(expected = SQLFormatException.class)
	public void genSql_have_no_default_value() throws IOException,
			SQLException {
		sqlManager(con).useFile(getClass(), "sql2/NoDefault.sql");
	}

	@Test(expected = SQLFormatException.class)
	public void genSql_have_no_default_value2() throws IOException,
			SQLException {
		sqlManager(con).useFile(getClass(), "sql2/NoDefault2.sql");
	}
}
