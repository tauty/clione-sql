package tetz42.cello.contents;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.test.Auty.*;

import java.util.List;

import org.junit.Test;

import tetz42.cellom.Context;
import tetz42.cellom.ICell;
import tetz42.cellom.annotation.EachBody;
import tetz42.cellom.annotation.EachHeader;
import tetz42.cellom.annotation.Header;
import tetz42.cellom.body.CelloMap;
import tetz42.cellom.body.Row;
import tetz42.cellom.header.HeaderManager;

public class RowTest {

	private Context<?> context;

	public <T> Row<T> create(Class<T> clazz) {
		HeaderManager<T> header = new HeaderManager<T>(clazz);
		Context<T> context = new Context<T>(clazz, header);
		this.context = context;
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
