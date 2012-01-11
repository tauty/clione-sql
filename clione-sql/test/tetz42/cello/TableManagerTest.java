package tetz42.cello;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.test.Auty.*;

import java.util.List;

import org.junit.Test;

import tetz42.cellom.TableManager;
import tetz42.cellom.annotation.EachBody;
import tetz42.cellom.annotation.EachHeader;
import tetz42.cellom.annotation.Header;
import tetz42.cellom.body.Cell;
import tetz42.cellom.body.CelloMap;
import tetz42.cellom.header.HeaderCell;

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

		tm.hooter().get().fooInt = 10000;
		tm.hooter().get().fooStr = "10000";

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

		tm.hooter().get().bars.get("key1").barInt = 20001;
		tm.hooter().get().bars.get("key1").barStr = "20001";

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

		tm.hooter().get().fooInt = 10000;
		tm.hooter().get().fooStr = "10000";

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

		tm.hooter().get().bars.get("key1").barInt = 20001;
		tm.hooter().get().bars.get("key1").barStr = "20001";

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

		tm.hooter().get().fooInt = 10000;
		tm.hooter().get().fooStr = "10000";

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

		tm.hooter().get().bars.get("key1").barInt = 20001;
		tm.hooter().get().bars.get("key1").barStr = "20001";

		// 1st
		System.out.println("-------------------------------------------------");
		assertThat(tm.row().get().bars.get("key1").bazzes.get("key1-1").bazInt,
				is(301));
		assertEqualsWithFile(tm.toString(), getClass(), "query-1st_no_query");

		// 2nd - update some cells
		System.out.println("-------------------------------------------------");
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
		System.out.println("-------------------------------------------------");
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

		// 4th - update some cells with terminate query
		System.out.println("-------------------------------------------------");
		assertThat(tm.row().get().fooInt, is(101));
		for (Cell<Integer> cell : tm.getByQuery(Integer.class,
				"*|@fooInt,bars|*|@barInt,bazzes|*|bazInt")) {
			cell.add(90000);
		}
		assertThat(tm.row().get().fooInt, is(90101));
		assertEqualsWithFile(tm.toString(), getClass(),
				"query-4th_someInt_Add10000");

		// 5th - get by number query
		System.out.println("-------------------------------------------------");
		assertThat(tm.row().get().bars.get("key2").bazzes.get("key2-1").bazInt,
				is(91601));
		List<Cell<Integer>> list = tm.getByQuery(".|bars|[0]|bazzes|[1]|bazInt");
		assertThat(list.size(), is(1));
		assertThat(list.get(0).get(), is(91601));
	}
}

class Foo {

	@Header(title = "foo:int", width = 1)
	int fooInt;

	@Header(title = "foo:String", width = 2)
	String fooStr;

	@Header(title = "bar:Bar", width = 3)
	Bar bar;

	@EachBody
	CelloMap<Bar> bars = CelloMap.create(Bar.class);

	int ignoredInt;

	String ignoredStr;
}

class Bar {
	@Header(title = "bar:int", width = 4)
	int barInt;

	@Header(title = "bar:String", width = 5)
	String barStr;

	@EachHeader(width = 6)
	CelloMap<Baz> bazzes = CelloMap.create(Baz.class);

	int ignoredInt;

	String ignoredStr;
}

class Baz {
	@Header(title = "baz:int", width = 7)
	int bazInt;

	@Header(title = "baz:String", width = 8)
	String bazStr;

	int ignoredInt;

	String ignoredStr;
}
