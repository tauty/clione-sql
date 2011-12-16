package tetz42.cello;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.test.Auty.*;

import org.junit.Test;

import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;
import tetz42.cello.contents.Cell;
import tetz42.cello.contents.CellUnitMap;
import tetz42.cello.header.HeaderCell;

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

	@Test
	public void pre_define() {
		TableManager<Foo> tm = TableManager.create(Foo.class);
		assertEqualsWithFile(tm.toString(), getClass(), "pre_define-bofore");

		// define columns and its order of CellUnitMap Elements
		tm.def().get().bars.get("key2").bazzes.get("key2-2");
		tm.def().get().bars.get("key2").bazzes.get("key2-1");
		tm.def().get().bars.get("key1").bazzes.get("key1-2");
		tm.def().get().bars.get("key1").bazzes.get("key1-1");
		tm.def().get().bars.get("key0").bazzes.get("key0-2");
		tm.def().get().bars.get("key0").bazzes.get("key0-1");
		tm.def().get().bar.bazzes.get("keyZ");
		tm.def().get().bar.bazzes.get("keyY");
		tm.def().get().bar.bazzes.get("keyX");

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

		assertEqualsWithFile(tm.toString(), getClass(), "pre_define-after");
	}

	@Test
	public void query() {
		TableManager<Foo> tm = TableManager.create(Foo.class);

		// define columns and its order of CellUnitMap Elements
		tm.def().get().bars.get("key2").bazzes.get("key2-2");
		tm.def().get().bars.get("key2").bazzes.get("key2-1");
		tm.def().get().bars.get("key1").bazzes.get("key1-2");
		tm.def().get().bars.get("key1").bazzes.get("key1-1");
		tm.def().get().bars.get("key0").bazzes.get("key0-2");
		tm.def().get().bars.get("key0").bazzes.get("key0-1");
		tm.def().get().bar.bazzes.get("keyZ");
		tm.def().get().bar.bazzes.get("keyY");
		tm.def().get().bar.bazzes.get("keyX");

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

		// 1st
		assertThat(tm.row().get().bars.get("key1").bazzes.get("key1-1").bazInt,
				is(301));
		assertEqualsWithFile(tm.toString(), getClass(), "query-1st_no_query");

		// 2nd - update some cells
		for (Cell<Integer> cell : tm.getByQuery(Integer.class,
				"*|bars|*|bazzes|*|bazInt")) {
			System.out.println(cell.get());
			cell.add(1000);
		}
		assertThat(tm.row().get().bars.get("key1").bazzes.get("key1-1").bazInt,
				is(1301));
		assertEqualsWithFile(tm.toString(), getClass(),
				"query-2nd_bazInt_Add1000");

		// 3rd - remove some cells
		assertFalse(tm.header().get("bars", "key1", "bazzes", "key1-1",
				"bazStr").isRemoved());
		for (HeaderCell hcell : tm.header()
				.getByQuery("bars|*|bazzes|*|bazStr")) {
			System.out.println("title:" + hcell.getTitle() + ", name:"
					+ hcell.getName());
			hcell.remove();
		}
		assertTrue(tm.header()
				.get("bars", "key1", "bazzes", "key1-1", "bazStr").isRemoved());
		assertEqualsWithFile(tm.toString(), getClass(),
				"query-3rd_bazStr_removed");
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
