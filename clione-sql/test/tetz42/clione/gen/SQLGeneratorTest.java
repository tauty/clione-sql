package tetz42.clione.gen;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import org.junit.Test;

import tetz42.clione.lang.ContextUtil;
import tetz42.clione.loader.LoaderUtil;
import tetz42.clione.node.SQLNode;

public class SQLGeneratorTest {

	@Test
	public void no_remove() throws Exception {
		SQLNode sqlNode = LoaderUtil.getNodeByClass(SQLGeneratorTest.class,
				"Select.sql", ContextUtil.getProductName());
		String sql = new SQLGenerator().execute(params("age", 100).$("namePart",
				"%A%"), sqlNode);
		assertEqualsWithFile(sql, getClass(), "no_remove");
	}

	@Test
	public void remove_delim_in_kakko_with_OR_front() throws Exception {
		SQLNode sqlNode = LoaderUtil.getNodeByClass(SQLGeneratorTest.class,
				"remove_delim_in_kakko.sql", ContextUtil.getProductName());
		String sql = new SQLGenerator().execute(paramsOn("isEnglish"), sqlNode);
		assertEqualsWithFile(sql, getClass(),
				"remove_delim_in_kakko_with_OR_front");
	}

	@Test
	public void remove_delim_in_kakko_with_OR_back() throws Exception {
		SQLNode sqlNode = LoaderUtil.getNodeByClass(SQLGeneratorTest.class,
				"remove_delim_in_kakko.sql", ContextUtil.getProductName());
		String sql = new SQLGenerator().execute(paramsOn("isJapanese"), sqlNode);
		assertEqualsWithFile(sql, getClass(),
				"remove_delim_in_kakko_with_OR_back");
	}

}
