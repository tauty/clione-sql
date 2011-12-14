package tetz42.cello.contents;

import static tetz42.test.Auty.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import tetz42.cello.Context;
import tetz42.cello.ICell;
import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;
import tetz42.cello.header.Header;

public class RowTest {

	private Context context;

	public <T> Row<T> create(Class<T> clazz) {
		Header<T> header = new Header<T>(clazz);
		context = new Context(header);
		return new Row<T>(clazz, context);
	}

	private String dumpRow(List<ICell> cells) {
		StringBuilder sb = new StringBuilder();
		for (ICell cell : cells) {
			if (!cell.isSkipped())
				sb.append(cell.getValue()).append("(Style:").append(
						cell.getStyle()).append(")");
			sb.append("\t");
		}
		return sb.toString();
	}

	@Test
	public void header() {
		Row<Foo> row = create(Foo.class);

		assertThat(context.getHeader().getDepth(), is(3));
		assertEqualsWithFile(context.getHeader().each(1), getClass(),
				"header-bofore1");
		assertEqualsWithFile(dumpRow(context.getHeader().each(1)), getClass(),
				"header-bofore1_dump");
		assertEqualsWithFile(context.getHeader().each(2), getClass(),
				"header-bofore2");
		assertEqualsWithFile(dumpRow(context.getHeader().each(2)), getClass(),
				"header-bofore2_dump");
		assertEqualsWithFile(context.getHeader().each(3), getClass(),
				"header-bofore3");
		assertEqualsWithFile(dumpRow(context.getHeader().each(3)), getClass(),
				"header-bofore3_dump");

		row.get().bar.bazzes.get("keyX").bazInt = 100;
		row.get().bar.bazzes.get("keyX").bazStr = "100";
		row.get().bar.bazzes.get("keyY").bazInt = 1000;
		row.get().bar.bazzes.get("keyY").bazStr = "1000";

		row.get().bars.get("key1").barInt = 200;
		row.get().bars.get("key1").barStr = "200";
		row.get().bars.get("key1").bazzes.get("key1-1").bazInt = 300;
		row.get().bars.get("key1").bazzes.get("key1-1").bazStr = "300";
		row.get().bars.get("key1").bazzes.get("key1-2").bazInt = 400;
		row.get().bars.get("key1").bazzes.get("key1-2").bazStr = "400";

		row.get().bars.get("key2").barInt = 500;
		row.get().bars.get("key2").barStr = "500";
		row.get().bars.get("key2").bazzes.get("key2-1").bazInt = 600;
		row.get().bars.get("key2").bazzes.get("key2-1").bazStr = "600";
		row.get().bars.get("key2").bazzes.get("key2-2").bazInt = 700;
		row.get().bars.get("key2").bazzes.get("key2-2").bazStr = "700";

		assertThat(context.getHeader().getDepth(), is(3));
		assertEqualsWithFile(context.getHeader().each(1), getClass(),
				"header-after1");
		assertEqualsWithFile(dumpRow(context.getHeader().each(1)), getClass(),
				"header-after1_dump");
		assertEqualsWithFile(context.getHeader().each(2), getClass(),
				"header-after2");
		assertEqualsWithFile(dumpRow(context.getHeader().each(2)), getClass(),
				"header-after2_dump");
		assertEqualsWithFile(context.getHeader().each(3), getClass(),
				"header-after3");
		assertEqualsWithFile(dumpRow(context.getHeader().each(3)), getClass(),
				"header-after3_dump");
	}

	@Test
	public void contents() {
		Row<Foo> row = create(Foo.class);

		assertThat(context.getHeader().getDepth(), is(3));
		assertEqualsWithFile(dumpRow(row.each()), getClass(), "contents-bofore");

		row.get().fooInt = 10;
		row.get().fooStr = "10";
		row.get().ignoredInt = 99999999;
		row.get().ignoredStr = "99999999";

		row.get().bar.bazzes.get("keyX").bazInt = 100;
		row.get().bar.bazzes.get("keyX").bazStr = "100";
		row.get().bar.bazzes.get("keyY").bazInt = 1000;
		row.get().bar.bazzes.get("keyY").bazStr = "1000";

		row.get().bars.get("key1").barInt = 200;
		row.get().bars.get("key1").barStr = "200";
		row.get().bars.get("key1").bazzes.get("key1-1").bazInt = 300;
		row.get().bars.get("key1").bazzes.get("key1-1").bazStr = "300";
		row.get().bars.get("key1").bazzes.get("key1-2").bazInt = 400;
		row.get().bars.get("key1").bazzes.get("key1-2").bazStr = "400";

		row.get().bars.get("key2").barInt = 500;
		row.get().bars.get("key2").barStr = "500";
		row.get().bars.get("key2").bazzes.get("key2-1").bazInt = 600;
		row.get().bars.get("key2").bazzes.get("key2-1").bazStr = "600";
		row.get().bars.get("key2").bazzes.get("key2-2").bazInt = 700;
		row.get().bars.get("key2").bazzes.get("key2-2").bazStr = "700";

		assertThat(context.getHeader().getDepth(), is(3));
		assertEqualsWithFile(dumpRow(row.each()), getClass(), "contents-after");
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
