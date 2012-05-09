package tetz42.clione.node;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.util.Map;

import org.junit.Test;

import tetz42.util.HereDoc;

public class LineNodeTest {

	private static final Map<String, String> hereDoc = HereDoc
			.get(LineNodeTest.class);

	@Test
	public void remove_last() {
		String sql = sqlManager().useSQL(hereDoc.get("remove_last")).genSql(
				params("id", "1"));
		assertEqualsWithFile(sql, getClass(), "remove_last");
	}

	@Test
	public void remove_last2() {
		String sql = sqlManager().useSQL(hereDoc.get("remove_last")).genSql(
				params("id", "1").$("name", "son"));
		assertEqualsWithFile(sql, getClass(), "remove_last2");
	}

	@Test
	public void remove_last3() {
		String sql = sqlManager().useSQL(hereDoc.get("remove_last")).genSql(
				params(params("name", "son").$("job",
						new String[] { "SE", "PM", "PL" })));
		assertEqualsWithFile(sql, getClass(), "remove_last3");
	}

	@Test
	public void union_all() {
		String sql = sqlManager().useSQL(hereDoc.get("union")).genSql(
				paramsOn("first", "second", "third").$("name", "son").$("job",
						new String[] { "SE", "PM", "PL" }));
		assertEqualsWithFile(sql, getClass(), "union_all");
	}

	@Test
	public void union_1_2() {
		String sql = sqlManager().useSQL(hereDoc.get("union")).genSql(
				paramsOn("first", "second").$("id", 9).$("name", "son"));
		assertEqualsWithFile(sql, getClass(), "union_1_2");
	}

	@Test
	public void union_1_3() {
		String sql = sqlManager().useSQL(hereDoc.get("union")).genSql(
				paramsOn("first", "third").$("name", "son"));
		assertEqualsWithFile(sql, getClass(), "union_1_3");
	}

	@Test
	public void union_2_3() {
		String sql = sqlManager().useSQL(hereDoc.get("union")).genSql(
				paramsOn("second", "third").$("id", 9).$("name", "son"));
		assertEqualsWithFile(sql, getClass(), "union_2_3");
	}

	@Test
	public void union_1() {
		String sql = sqlManager().useSQL(hereDoc.get("union")).genSql(
				paramsOn("first").$("id", 9).$("name", "son"));
		assertEqualsWithFile(sql, getClass(), "union_1");
	}

	@Test
	public void union_2() {
		String sql = sqlManager().useSQL(hereDoc.get("union")).genSql(
				paramsOn("second").$("id", 9).$("name", "son"));
		assertEqualsWithFile(sql, getClass(), "union_2");
	}

	@Test
	public void union_3() {
		String sql = sqlManager().useSQL(hereDoc.get("union")).genSql(
				paramsOn("third").$("id", 9).$("name", "son"));
		assertEqualsWithFile(sql, getClass(), "union_3");
	}

}
