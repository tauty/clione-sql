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
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tetz42.clione.module.Caller;
import tetz42.clione.module.SQLFormatException;
import tetz42.clione.module.SQLParser;

public class SQLParserTest {

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

	@Test
	public void wordEnd_1() {
		SQLParser parser = new SQLParser();

		StringBuilder sb = new StringBuilder().append("tako ");
		assertThat(Caller.wordEnd(parser, sb, 0), is(4));
		assertThat(sb.substring(0, 4), is("tako"));

		sb = new StringBuilder().append("tako");
		assertThat(Caller.wordEnd(parser, sb, 0), is(4));
		assertThat(sb.substring(0, 4), is("tako"));
	}

	@Test
	public void genSql_by_sample() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(), "sql/Sample.sql");

		man.genSql(params("TEST1", 10).$("TEST2", 100).$("TEST3", 1000));
		System.out.println(man.getExecutedSql());
		System.out.println(man.getExecutedParams());
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_by_sample");
	}

	@Test
	public void genSql_empty_line() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useStream(
				getClass().getResourceAsStream("sql/EmptyLineSelect.sql"));
		man.genSql();
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_empty_line");
	}

	@Test
	public void genSql_join_line() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useStream(
				getClass().getResourceAsStream("sql/JoinLineUpdate.sql"));
		System.out.println(dumper(man.lineTreeList));
		man.genSql(new HashMap<String, Object>());
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_join_line");
	}

	@Test
	public void genSql_ireco_comment() throws IOException, SQLException {
		// /* - /* - */ \n */のケース
		SQLExecutor man = sqlManager(con).useStream(
				getClass()
						.getResourceAsStream("sql/RecursiveCommentSelect.sql"));
		System.out.println(dumper(man.lineTreeList));
		man.genSql(new HashMap<String, Object>());
		// TODO 本当はこのケースはWHERE句ごと消したい。genSqlでコメントを無視する方法は、今後の課題。
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_ireco_comment");
	}

	@Test
	public void genSql_ireco_comment2() throws IOException, SQLException {
		// /* - \n /* - */ \n */のケース
		SQLExecutor man = sqlManager(con).useStream(
				getClass().getResourceAsStream(
						"sql/RecursiveCommentSelect2.sql"));
		System.out.println(dumper(man.lineTreeList));
		man.genSql(new HashMap<String, Object>());
		// TODO 本当はこのケースはWHERE句ごと消したい。genSqlでコメントを無視する方法は、今後の課題。
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_ireco_comment2");
	}

	@Test(expected = SQLFormatException.class)
	public void genSql_wrong_comment() throws IOException, SQLException {
		// /* - */ \n */のケース
		sqlManager(con).useStream(
				getClass().getResourceAsStream("sql/WrongCommentSelect.sql"));
	}

	@Test(expected = SQLFormatException.class)
	public void genSql_wrong_comment2() throws IOException, SQLException {
		// /* - \n /* - */のケース
		sqlManager(con).useStream(
				getClass().getResourceAsStream("sql/WrongCommentSelect2.sql"));
	}

	@Test
	public void genSql_using_doller_param_and_normal_param()
			throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(),
				"sql/DollAndNormal.sql");

		man.genSql(params("TAKO", "octpus"));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_using_doller_param_and_normal_param");
		man.genSql();
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_using_doller_param_and_normal_param_by_no_param");
	}

	@Test
	public void genSql_normal_comment() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(),
				"sql/NormalComment.sql");

		man.genSql(params("TAKO", "octpus"));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_normal_comment");
		assertEqualsWithFile(man.lineTreeList, getClass(),
				"genSql_normal_comment_lineTreeList");
	}

	@Test
	public void genSql_remove_root() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con)
				.useFile(getClass(), "sql/RemoveRoot.sql");

		man.genSql(params("ROOT", "octpus"));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_remove_root_1_param");
		man.genSql();
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_remove_root_no_param");
	}

	@Test
	public void genSql_2_key_at_1_line() throws IOException, SQLException {
		SQLExecutor man = sqlManager(con).useFile(getClass(),
				"sql/2KeyAt1Line.sql");

		man.genSql(params("TAKO", "octopus").$("IKA", "squid"));
		System.out.println(dumper(man.lineTreeList));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_2_key_at_1_line_2param");
		assertThat(man.getExecutedParams().size(), is(4));
		assertThat((String) man.getExecutedParams().get(0), is("octopus"));
		assertThat((String) man.getExecutedParams().get(1), is("squid"));
		assertThat((String) man.getExecutedParams().get(2), is("octopus"));
		assertThat((String) man.getExecutedParams().get(3), is("squid"));

		man.genSql(params("TAKO", "octopus"));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_2_key_at_1_line_1st_param");
		assertThat(man.getExecutedParams().size(), is(2));
		assertThat((String) man.getExecutedParams().get(0), is("octopus"));
		assertThat((String) man.getExecutedParams().get(1), is("octopus"));

		man.genSql(params("IKA", "squid"));
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_2_key_at_1_line_2nd_param");
		assertThat(man.getExecutedParams().size(), is(2));
		assertThat((String) man.getExecutedParams().get(0), is("squid"));
		assertThat((String) man.getExecutedParams().get(1), is("squid"));

		man.genSql();
		assertEqualsWithFile(man.getExecutedSql(), getClass(),
				"genSql_2_key_at_1_line_no_param");
		assertThat(man.getExecutedParams().size(), is(0));
	}

}
