package tetz42.clione.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConverterTest {

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
		setThreadConnection(con);
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
	public void test() throws MalformedURLException {
		Sample sample = new Sample();
		sqlManager().useFile(getClass(), "test.sql").update(sample);
		List<Sample> list = sqlManager().useSQL(
				"select * from sample order by id").findAll(Sample.class);
		assertEqualsWithFile(list, getClass(), "test");
	}

	@Test
	public void reader() throws IOException {
		Reader r = new StringReader("Leader has a book reader, kobo.");
		sqlManager()
				.useSQL("update sample set r = /* r */ where id = /* id */")
				.update(params("r", r).$("id", "100"));
		Reader reader = sqlManager().useSQL(
				"select r from sample where id = /* id */").find(Reader.class,
				params("id", "100"));
		BufferedReader br = new BufferedReader(reader);
		assertThat(br.readLine(), is("Leader has a book reader, kobo."));
		br.close();
		reader.close();
	}

	static class Sample {
		String id = "101";
		BigDecimal bd = new BigDecimal("23423083150983.9923000022");
		BigInteger bi = new BigInteger("419232214420111941");
		// Boolean bl = true;
		// Boolean bln = null;
		// boolean blp = false;
		Byte byt = 10;
		Byte bytn = null;
		byte bytp = 8;
		byte[] bytes = new byte[] { 3, 1, 4, 1, 5, 9, 2 };
		Date dt = new Date(432340980);
		Float fl = 302.193F;
		Float fln = null;
		float flp = 302.193F;
		Long lo = 1021231230129L;
		Long lon = null;
		long lop = 102123123980909L;
		Short sh = 10021;
		Short shn = null;
		short shp = 12312;
		java.sql.Date sdate = new java.sql.Date(1000010100L);
		Time ti = new Time(232340980);
		Timestamp ts = new Timestamp(209823910000L);
		URL url = new URL("http://locahlost:8080/test");

		Sample() throws MalformedURLException {
		}
	}
}
