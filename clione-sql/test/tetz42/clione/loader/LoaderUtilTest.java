package tetz42.clione.loader;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tetz42.clione.SQLManager;
import tetz42.clione.SQLManagerTest.Employee;
import tetz42.clione.setting.Config;

public class LoaderUtilTest {

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
	public void hotReloading() throws Exception {

		String prop1 = "bin/clione.properties";
		String prop2 = "bin/clione4lodertest.properties";
		try {
			swapFile(prop1, prop2);
			Config.clear();

			// 1st.
			SQLManager sqlManager = sqlManager();
			List<Employee> list;
			list = sqlManager.useFile(getClass(), "Select.sql").findAll(
					Employee.class, params("NO1", 100001).$("NO2", 100003));
			assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
					"hotReloading_1st_sql");
			assertEqualsWithFile(list, getClass(), "hotReloading_1st");

			String sql1 = "bin/tetz42/clione/loader/sql/LoaderUtilTest/Select.sql";
			String sql2 = "bin/tetz42/clione/loader/sql/LoaderUtilTest/SmallSelect.sql";

			try {
				swapFile(sql1, sql2);
				
				// 2nd. The cache is valid.
				list = sqlManager.useFile(getClass(), "Select.sql").findAll(
						Employee.class, params("NO1", 100001).$("NO2", 100003));
				assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
						"hotReloading_1st_sql");
				assertEqualsWithFile(list, getClass(), "hotReloading_1st");

				Thread.sleep(Config.get().SQLFILE_CACHETIME + 10);

				// 3rd. The cache is invalid and SQL file is reloaded.
				list = sqlManager.useFile(getClass(), "Select.sql").findAll(
						Employee.class, params("NO1", 100001).$("NO2", 100003));
				assertEqualsWithFile(sqlManager.getSQLInfo(), getClass(),
						"hotReloading_2nd_sql");
				assertEqualsWithFile(list, getClass(), "hotReloading_2nd");
			} finally {
				swapFile(sql1, sql2);
			}
		} finally {
			swapFile(prop1, prop2);
			Config.clear();
		}
	}
}
