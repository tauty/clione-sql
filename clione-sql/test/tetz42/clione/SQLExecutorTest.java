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
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tetz42.clione.SQLManagerTest.Tameshi;
import tetz42.clione.util.ResultMap;

public class SQLExecutorTest {

	@BeforeClass
	public static void start() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
	}

	@Before
	public void setUp() throws SQLException {
		Connection con = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/test", "root", "rootroot");
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
	public void findAll_by_dto_param() throws IOException, SQLException {
		SQLExecutor exe = sqlManager().useFile(getClass(), "exesql/Select.sql");
		List<Person> people = exe
				.findAll(Person.class, new ParamDto(31, "%H%"));
		assertEqualsWithFile(people, getClass(), "findAll_by_dto_param");
		assertEqualsWithFile(exe.getSql(), getClass(),
				"findAll_by_dto_param-sql");
		assertEqualsWithFile(exe.getParams(), getClass(),
				"findAll_by_dto_param-params");
	}

	@Test
	public void find_by_dto_param() throws IOException, SQLException {
		Person person = sqlManager().useFile(getClass(), "exesql/Select.sql")
				.find(Person.class, new ParamDto(31, "%k%"));
		assertEqualsWithFile(person, getClass(), "find_by_dto_param");
	}

	@Test
	public void find_by_dto_no_result() throws IOException, SQLException {
		Person person = sqlManager().useFile(getClass(), "exesql/Select.sql")
				.find(Person.class, new ParamDto(31, "%X%"));
		assertNull(person);
	}

	@Test
	public void findmapAll_by_dto_param() throws IOException, SQLException {
		SQLExecutor exe = sqlManager().useFile(getClass(), "exesql/Select.sql");
		List<ResultMap> people = exe.findAll(new ParamDto(31, "%H%"));
		assertEqualsWithFile(people, getClass(), "findmapAll_by_dto_param");
	}

	@Test
	public void findmap_by_dto_param() throws IOException, SQLException {
		Map<String, Object> person = sqlManager().useFile(getClass(),
				"exesql/Select.sql").find(new ParamDto(31, "%k%"));
		assertEqualsWithFile(person, getClass(), "findmap_by_dto_param");
	}

	@Test
	public void findmap_by_dto_no_result() throws IOException, SQLException {
		Map<String, Object> person = sqlManager().useFile(getClass(),
				"exesql/Select.sql").find(new ParamDto(31, "%X%"));
		assertNull(person);
	}

	@Test(expected = SQLException.class)
	public void findAll_wrong_by_dto_param() throws Exception {
		final SQLExecutor exe = sqlManager().useFile(getClass(),
				"exesql/WrongSelect.sql");
		assertSQLException("findAll_wrong_by_dto_param", new Proc() {
			@Override
			public void proc() throws SQLException {
				exe.findAll(Person.class, new ParamDto(31, "%H%"));
			}
		});
	}

	@Test(expected = SQLException.class)
	public void find_wrong_by_dto_param() throws Exception {
		assertSQLException("find_wrong_by_dto_param", new Proc() {
			@Override
			public void proc() throws SQLException {
				sqlManager().useFile(getClass(), "exesql/WrongSelect.sql")
						.find(Person.class, new ParamDto(31, "%k%"));
			}
		});
	}

	@Test(expected = SQLException.class)
	public void findmapAll_wrong_by_dto_param() throws Exception {
		final SQLExecutor exe = sqlManager().useFile(getClass(),
				"exesql/WrongSelect.sql");
		assertSQLException("findmapAll_wrong_by_dto_param", new Proc() {
			@Override
			public void proc() throws SQLException {
				exe.findAll(new ParamDto(31, "%H%"));
			}
		});
	}

	@Test(expected = SQLException.class)
	public void findmap_wrong_by_dto_param() throws Exception {
		assertSQLException("findmap_wrong_by_dto_param", new Proc() {
			@Override
			public void proc() throws SQLException {
				sqlManager().useFile(getClass(), "exesql/WrongSelect.sql")
						.find(new ParamDto(31, "%k%"));
			}
		});
	}

	@Test
	public void update_set_age_35() throws Exception {
		int count = sqlManager().useFile(getClass(), "exesql/Update.sql")
				.update(new ParamDto(35, ""));
		assertThat(count, is(1));
		List<Tameshi> list = sqlManager().useFile(getClass(), "sql/Select.sql")
				.findAll(Tameshi.class);
		assertEqualsWithFile(list, getClass(), "update_set_age_35");
	}

	@Test
	public void find_by_age_on() throws Exception {
		List<ResultMap> people = sqlManager().useFile(getClass(),
				"exesql/AmpersandSelect.sql").findAll(paramsOn("age"));
		assertEqualsWithFile(people, getClass(), "find_by_age_on");
	}

	@Test
	public void find_by_age_and_namePart_on() throws Exception {
		List<ResultMap> people = sqlManager().useFile(getClass(),
				"exesql/AmpersandSelect.sql").findAll(paramsOn("age", "namePart"));
		assertEqualsWithFile(people, getClass(), "find_by_age_and_namePart_on");
	}

	private void assertSQLException(String fileName, Proc proc)
			throws Exception {
		try {
			proc.proc();
			fail();
		} catch (Exception e) {
			assertEqualsWithFile(e.getMessage(), getClass(), fileName);
			throw e;
		}
	}

}

class Person {
	int id;
	String name;
	int age;
}

class ParamDto {
	int age;
	String namePart;

	ParamDto(int age, String namePart) {
		this.age = age;
		this.namePart = namePart;
	}
}
