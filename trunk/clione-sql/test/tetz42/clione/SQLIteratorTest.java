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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
		SQLExecutor exe = sqlManager().useFile(SQLExecutorTest.class,
				"Select.sql");
		List<Person2> people = exe.findAll(Person2.class, new ParamDto(31,
				"%H%"));
		assertEqualsWithFile(people, getClass(), "findAll_by_dto_param");
	}

	@Test
	public void find_tree_entity() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<GranpaEntity> list = sqlManager
				.useSQL("select * from EMPLOYEES where SHOZOKU_BU_KA like /* %L shozoku_bu_ka '%' */'%課'")
				.findAll(GranpaEntity.class, new GranpaParam());

		assertEqualsWithFile(list, getClass(), "find_tree_entity");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void find_pureMap() throws IOException, SQLException {
		SQLManager sqlManager = sqlManager();
		List<Map> list = sqlManager
				.useSQL("select * from EMPLOYEES where SHOZOKU_BU_KA like /* %L shozoku_bu_ka '%' */'%課'")
				.findAll(Map.class, new GranpaParam());

		assertEqualsWithFile(list, getClass(), "find_pureMap");
	}

	@Test
	public void iterator_test()  {
		SQLManager sqlManager = sqlManager();
		Iterator<GranpaEntity> iterator = sqlManager
				.useSQL("select * from EMPLOYEES where SHOZOKU_BU_KA like /* %L shozoku_bu_ka '%' */'%課'")
				.each(GranpaEntity.class, new GranpaParam()).iterator();

		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.hasNext(), is(true));

		assertNotNull(iterator.next());
		assertNotNull(iterator.next());
		try {
			iterator.next();
			fail();
		}catch(UnsupportedOperationException e) {
			e.printStackTrace();
		}
		try {
			iterator.next();
			fail();
		}catch(UnsupportedOperationException e) {
			e.printStackTrace();
		}

		assertThat(iterator.hasNext(), is(false));
		assertThat(iterator.hasNext(), is(false));
		assertThat(iterator.hasNext(), is(false));
	}
}

class Person2 extends Person {
	int age;
}

class GranpaParam {
	ParentParam shozoku = new ParentParam();
}

class ParentParam {
	ChildParam bu = new ChildParam();
}

class ChildParam {
	String ka = "柔道";
}

class GranpaEntity {
	String id;
	ParentEntity shain = new ParentEntity();
	ParentEntity shozoku = shain;
	String name;
	String address;
}

class ParentEntity {
	String no;
	ChildEntity bu;
}

class ChildEntity {
	String ka;
}