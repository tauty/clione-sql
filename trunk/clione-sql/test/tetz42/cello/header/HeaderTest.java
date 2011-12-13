package tetz42.cello.header;

import static tetz42.test.Auty.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;
import tetz42.cello.contents.CellUnitMap;

public class HeaderTest {

	public static <T> Header<T> create(Class<T> clazz) {
		return new Header<T>(clazz);
	}

	@Test
	public void test() throws Exception {
		Header<Foo> header = create(Foo.class);
		assertThat(header.get("fooInt").getDepth(), is(1));
		assertThat(header.get("fooInt").getTitle(), is("foo:int"));
		assertThat(header.get("fooInt").getName(), is("fooInt"));
		assertThat(header.get("fooInt").getWidth(), is(1));

		assertThat(header.get("fooStr").getDepth(), is(1));
		assertThat(header.get("fooStr").getTitle(), is("foo:String"));
		assertThat(header.get("fooStr").getName(), is("foo:anotherName"));
		assertThat(header.get("fooStr").getWidth(), is(2));

		assertThat(header.get("bar", "barInt").getDepth(), is(2));
		assertThat(header.get("bar", "barInt").getTitle(), is("bar:int"));
		assertThat(header.get("bar", "barInt").getName(), is("bar:anotherName"));
		assertThat(header.get("bar", "barInt").getWidth(), is(4));

		assertThat(header.get("bar", "barStr").getDepth(), is(2));
		assertThat(header.get("bar", "barStr").getTitle(), is("bar:String"));
		assertThat(header.get("bar", "barStr").getName(), is("barStr"));
		assertThat(header.get("bar", "barStr").getWidth(), is(5));

		assertThat(header.getDepth(), is(3));

		assertEqualsWithFile(header, getClass(), "test");
	}

}

class Foo {

	@HeaderDef(title = "foo:int", width = 1)
	int fooInt;

	@HeaderDef(title = "foo:String", width = 2, name="foo:anotherName")
	String fooStr;

	@HeaderDef(title = "bar:Bar", width = 3)
	Bar bar;

	@EachCellDef(width = 110)
	CellUnitMap<Bar> bars = CellUnitMap.create(Bar.class);

	int ignoredInt;

	String ignoredStr;
}

class Bar {
	@HeaderDef(title = "bar:int", width = 4, name="bar:anotherName")
	int barInt;

	@HeaderDef(title = "bar:String", width = 5)
	String barStr;

	@EachHeaderDef(title = "baz:CellUnitMap", width = 6)
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
