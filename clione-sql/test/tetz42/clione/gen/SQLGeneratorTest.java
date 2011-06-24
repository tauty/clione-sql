package tetz42.clione.gen;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import org.junit.Test;

import tetz42.clione.loader.LoaderUtil;
import tetz42.clione.node.SQLNode;

public class SQLGeneratorTest {

	@Test
	public void no_remove() throws Exception {
		SQLNode sqlNode = LoaderUtil.getNodeByClass(SQLGeneratorTest.class,
				"Select.sql");
		String sql = new SQLGenerator().genSql(
				params("age", 100).$("namePart", "%A%"), sqlNode);
		assertEqualsWithFile(sql, getClass(), "no_remove");
	}

}
