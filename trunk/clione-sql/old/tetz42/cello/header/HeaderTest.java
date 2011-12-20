package tetz42.cello.header;

import static tetz42.test.Auty.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import tetz42.cello.annotation.EachContentsDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;
import tetz42.cello.contents.CelloMap;

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
		assertThat(header.get("fooStr").getName(), is("fooStr"));
		assertThat(header.get("fooStr").getWidth(), is(2));

		assertThat(header.get("bar", "barInt").getDepth(), is(2));
		assertThat(header.get("bar", "barInt").getTitle(), is("bar:int"));
		assertThat(header.get("bar", "barInt").getName(), is("barInt"));
		assertThat(header.get("bar", "barInt").getWidth(), is(4));

		assertThat(header.get("bar", "barStr").getDepth(), is(2));
		assertThat(header.get("bar", "barStr").getTitle(), is("bar:String"));
		assertThat(header.get("bar", "barStr").getName(), is("barStr"));
		assertThat(header.get("bar", "barStr").getWidth(), is(5));

		assertThat(header.getDepth(), is(3));

		assertEqualsWithFile(header, getClass(), "test");
	}

	@Test
	public void calcCellSize() throws Exception {
		Header<Foo> header = create(Foo.class);
		header.calcCellSize();

		assertThat(header.get("fooInt").getDepth(), is(1));
		assertThat(header.get("fooInt").getTitle(), is("foo:int"));
		assertThat(header.get("fooInt").getName(), is("fooInt"));
		assertThat(header.get("fooInt").getWidth(), is(1));
		assertThat(header.get("fooInt").getSize(), is(1));

		assertThat(header.get("fooStr").getDepth(), is(1));
		assertThat(header.get("fooStr").getTitle(), is("foo:String"));
		assertThat(header.get("fooStr").getName(), is("fooStr"));
		assertThat(header.get("fooStr").getWidth(), is(2));
		assertThat(header.get("fooStr").getSize(), is(1));

		assertThat(header.get("bar", "barInt").getDepth(), is(2));
		assertThat(header.get("bar", "barInt").getTitle(), is("bar:int"));
		assertThat(header.get("bar", "barInt").getName(), is("barInt"));
		assertThat(header.get("bar", "barInt").getWidth(), is(4));
		assertThat(header.get("bar", "barInt").getSize(), is(1));

		assertThat(header.get("bar", "barStr").getDepth(), is(2));
		assertThat(header.get("bar", "barStr").getTitle(), is("bar:String"));
		assertThat(header.get("bar", "barStr").getName(), is("barStr"));
		assertThat(header.get("bar", "barStr").getWidth(), is(5));
		assertThat(header.get("bar", "barStr").getSize(), is(1));

		assertThat(header.get("bar").getSize(), is(2));

		assertThat(header.get().getSize(), is(4));

		assertThat(header.getDepth(), is(3));

		assertEqualsWithFile(header, getClass(), "test");

	}

	@Test
	public void each() throws Exception {
		Header<Foo> header = create(Foo.class);
		assertThat(header.getDepth(), is(3));
		assertEqualsWithFile(header.each(1), getClass(), "each1");
		assertEqualsWithFile(header.each(2), getClass(), "each2");
		assertEqualsWithFile(header.each(3), getClass(), "each3");
	}
}

class Foo {

	@HeaderDef(title = "foo:int", width = 1)
	int fooInt;

	@HeaderDef(title = "foo:String", width = 2)
	String fooStr;

	@HeaderDef(title = "bar:Bar", width = 3)
	Bar bar;

	@EachContentsDef
	CelloMap<Bar> bars = CelloMap.create(Bar.class);

	int ignoredInt;

	String ignoredStr;
}

class Bar {
	@HeaderDef(title = "bar:int", width = 4)
	int barInt;

	@HeaderDef(title = "bar:String", width = 5)
	String barStr;

	@EachHeaderDef(width = 6)
	CelloMap<Baz> bazzes = CelloMap.create(Baz.class);

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
