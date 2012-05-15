package tetz42.clione.lang;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.util.Map;

import org.junit.Test;

import tetz42.util.HereDoc;

public class UpperIfTest {

	private static final Map<String, String> doc = HereDoc
			.get(UpperIfTest.class);

	@Test
	public void IF_block1() {
		String sql = sqlManager().useSQL(doc.get("test")).genSql(
				paramsOn("block1", "block2", "block3").$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block1");
	}

	@Test
	public void IF_block2() {
		String sql = sqlManager().useSQL(doc.get("test")).genSql(
				paramsOn("block2", "block3").$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block2");
	}

	@Test
	public void IF_block3() {
		String sql = sqlManager().useSQL(doc.get("test")).genSql(
				paramsOn("block3").$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block3");
	}

	@Test
	public void IF_block4_ELSE() {
		String sql = sqlManager().useSQL(doc.get("test")).genSql(
				params().$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block4_ELSE");
	}

	@Test
	public void IF_block1_no_child() {
		String sql = sqlManager().useSQL(doc.get("test")).genSql(
				paramsOn("block1", "block2", "block3").$("tako", 800).$("ika",
						100));
		assertEqualsWithFile(sql, getClass(), "IF_block1_no_child");
	}

	@Test
	public void IF_block1_child1() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("block1", "block2", "block3", "childBlock1",
						"childBlock2").$("tako", 800).$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block1_child1");
	}

	@Test
	public void IF_block1_child2() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("block1", "block2", "block3", "childBlock2").$("tako",
						800).$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block1_child2");
	}

	@Test
	public void IF_block2_no_child() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("block2", "block3").$("tako", 800).$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block2_no_child");
	}

	@Test
	public void IF_block2_child1() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("block2", "block3", "childBlock1").$("tako", 800).$(
						"ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block2_child1");
	}

	@Test
	public void IF_block2_child2() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("block2", "block3", "childBlock2").$("tako", 800).$(
						"ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block2_child2");
	}

	@Test
	public void IF_block3_child() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("block3", "childBlock").$("tako", 800).$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block3_child");
	}

	@Test
	public void IF_block3_childElse() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("block3").$("tako", 800).$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block3_childElse");
	}

	@Test
	public void IF_block4_ELSE_no_child() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				params().$("tako", 800).$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block4_ELSE_no_child");
	}

	@Test
	public void IF_block4_ELSE_child1() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("childBlock1").$("tako", 800).$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block4_ELSE_child1");
	}

	@Test
	public void IF_block4_ELSE_child2() {
		String sql = sqlManager().useSQL(doc.get("nest")).genSql(
				paramsOn("childBlock2").$("tako", 800).$("ika", 100));
		assertEqualsWithFile(sql, getClass(), "IF_block4_ELSE_child2");
	}
}
