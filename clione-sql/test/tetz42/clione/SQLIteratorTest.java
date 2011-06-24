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
import org.junit.BeforeClass;
import org.junit.Test;

public class SQLIteratorTest {

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
	public void findAll_by_dto_param() throws IOException, SQLException {
		SQLExecutor exe = sqlManager().useFile(SQLExecutorTest.class, "Select.sql");
		List<Person2> people = exe.findAll(Person2.class, new ParamDto(31,
				"%H%"));
		assertEqualsWithFile(people, getClass(), "findAll_by_dto_param");
	}

}

class Person2 extends Person {
	int age;
}