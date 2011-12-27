package tetz42.cellom.generator;

import static tetz42.test.Auty.*;

import org.junit.Test;

import tetz42.cellom.TableManager;
import tetz42.cellom.annotation.EachHeader;
import tetz42.cellom.annotation.Header;
import tetz42.cellom.body.CelloMap;

public class HtmlGeneratorTest {

	@Test
	public void test() throws Exception {
		TableManager<Foo> tm = TableManager.create(Foo.class);

		tm.newRow();
		tm.row().get().fooInt = 1;
		tm.row().get().fooString = "one";
		tm.row().get().barMap.get("1st").barInt = 1;
		tm.row().get().barMap.get("1st").barString = "one";
		tm.row().get().barMap.get("2nd").barInt = 2;
		tm.row().get().barMap.get("2nd").barString = "two";
		tm.row().get().barMap.get("3rd").barInt = 3;
		tm.row().get().barMap.get("3rd").barString = "three";

		tm.newRow();
		tm.row().get().fooInt = 10;
		tm.row().get().fooString = "ten";
		tm.row().get().barMap.get("1st").barInt = 10;
		tm.row().get().barMap.get("1st").barString = "ten";
		tm.row().get().barMap.get("2nd").barInt = 20;
		tm.row().get().barMap.get("2nd").barString = "twenty";
		tm.row().get().barMap.get("3rd").barInt = 30;
		tm.row().get().barMap.get("3rd").barString = "thirty";

		tm.newRow();
		tm.row().get().fooInt = 100;
		tm.row().get().fooString = "hundred";
		tm.row().get().barMap.get("1st").barInt = 100;
		tm.row().get().barMap.get("1st").barString = "hundred";
		tm.row().get().barMap.get("2nd").barInt = 200;
		tm.row().get().barMap.get("2nd").barString = "two-hundred";
		tm.row().get().barMap.get("3rd").barInt = 300;
		tm.row().get().barMap.get("3rd").barString = "three-hundred";

		assertEqualsWithFile(new HtmlGenerator().generate(tm), getClass(),
				"test");
	}

	static class Foo {
		@Header(title = "Foo Int.")
		int fooInt;

		@Header(title = "Foo String.")
		String fooString;

		@EachHeader
		CelloMap<Bar> barMap = CelloMap.create(Bar.class);
	}

	static class Bar {
		@Header(title = "Bar Int.")
		int barInt;

		@Header(title = "Bar String.")
		String barString;
	}
}
