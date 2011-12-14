package tetz42.cello;

import static tetz42.test.Auty.*;

import org.junit.Test;

import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;
import tetz42.cello.contents.CellUnitMap;

public class TableManagerTest {

	@Test
	public void test() {
		TableManager<Foo> tm = TableManager.create(Foo.class);
		assertEqualsWithFile(tm.toString(), getClass(), "test-bofore");

		tm.newRow();
		tm.row().get().fooInt = 100;
		tm.row().get().bar.bazzes.get("keyX").bazInt = 100;
		tm.row().get().bar.bazzes.get("keyX").bazStr = "100";
		tm.row().get().bar.bazzes.get("keyY").bazInt = 1000;
		tm.row().get().bar.bazzes.get("keyY").bazStr = "1000";

		tm.row().get().bars.get("key1").barInt = 200;
		tm.row().get().bars.get("key1").barStr = "200";
		tm.row().get().bars.get("key1").bazzes.get("key1-1").bazInt = 300;
		tm.row().get().bars.get("key1").bazzes.get("key1-1").bazStr = "300";
		tm.row().get().bars.get("key1").bazzes.get("key1-2").bazInt = 400;
		tm.row().get().bars.get("key1").bazzes.get("key1-2").bazStr = "400";

		tm.row().get().bars.get("key2").barInt = 500;
		tm.row().get().bars.get("key2").barStr = "500";
		tm.row().get().bars.get("key2").bazzes.get("key2-1").bazInt = 600;
		tm.row().get().bars.get("key2").bazzes.get("key2-1").bazStr = "600";
		tm.row().get().bars.get("key2").bazzes.get("key2-2").bazInt = 700;
		tm.row().get().bars.get("key2").bazzes.get("key2-2").bazStr = "700";

		tm.tail().get().fooInt = 10000;
		tm.tail().get().fooStr = "10000";

		// TODO TableManager should be performed correctly even if the code block below is removed.
		tm.tail().get().bar.bazzes.get("keyX");
		tm.tail().get().bar.bazzes.get("keyY");
		tm.tail().get().bars.get("key1").bazzes.get("key1-1");
		tm.tail().get().bars.get("key1").bazzes.get("key1-2");
		tm.tail().get().bars.get("key2").bazzes.get("key2-1");
		tm.tail().get().bars.get("key2").bazzes.get("key2-2");

		tm.newRow();
		tm.row().get().fooInt = 101;
		tm.row().get().bar.bazzes.get("keyX").bazInt = 101;
		tm.row().get().bar.bazzes.get("keyX").bazStr = "101";
		tm.row().get().bar.bazzes.get("keyY").bazInt = 1001;
		tm.row().get().bar.bazzes.get("keyY").bazStr = "1001";

		tm.row().get().bars.get("key1").barInt = 201;
		tm.row().get().bars.get("key1").barStr = "201";
		tm.row().get().bars.get("key1").bazzes.get("key1-1").bazInt = 301;
		tm.row().get().bars.get("key1").bazzes.get("key1-1").bazStr = "301";
		tm.row().get().bars.get("key1").bazzes.get("key1-2").bazInt = 401;
		tm.row().get().bars.get("key1").bazzes.get("key1-2").bazStr = "401";

		tm.row().get().bars.get("key2").barInt = 501;
		tm.row().get().bars.get("key2").barStr = "501";
		tm.row().get().bars.get("key2").bazzes.get("key2-1").bazInt = 601;
		tm.row().get().bars.get("key2").bazzes.get("key2-1").bazStr = "601";
		tm.row().get().bars.get("key2").bazzes.get("key2-2").bazInt = 701;
		tm.row().get().bars.get("key2").bazzes.get("key2-2").bazStr = "701";

		tm.tail().get().bars.get("key1").barInt = 20001;
		tm.tail().get().bars.get("key1").barStr = "20001";

		assertEqualsWithFile(tm.toString(), getClass(), "test-after");
	}
}

class Foo {

	@HeaderDef(title = "foo:int", width = 1)
	int fooInt;

	@HeaderDef(title = "foo:String", width = 2)
	String fooStr;

	@HeaderDef(title = "bar:Bar", width = 3)
	Bar bar;

	@EachCellDef
	CellUnitMap<Bar> bars = CellUnitMap.create(Bar.class);

	int ignoredInt;

	String ignoredStr;
}

class Bar {
	@HeaderDef(title = "bar:int", width = 4)
	int barInt;

	@HeaderDef(title = "bar:String", width = 5)
	String barStr;

	@EachHeaderDef(width = 6)
	CellUnitMap<Baz> bazzes = CellUnitMap.create(Baz.class);

	int ignoredInt;

	String ignoredStr;
}

class Baz {
	@HeaderDef(title = "baz:int", width = 7)
	int bazInt;

	@HeaderDef(title = "baz:String", width = 8)
	String bazStr;

	int ignoredInt;

	String ignoredStr;
}
