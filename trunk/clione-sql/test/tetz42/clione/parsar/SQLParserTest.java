package tetz42.clione.parsar;

import static tetz42.test.Util.*;
import org.junit.Test;

import tetz42.clione.node.SQLNode;

public class SQLParserTest {

	@Test
	public void valueInBack_normal_contains_dot() {
		SQLNode sqlNode = new SQLParser("From Test")
				.parse(" /* $param1 */tako.ika.namako ");
		assertEqualsWithFile(sqlNode, getClass(), "valueInBack_normal_contains_dot");

	}

}
