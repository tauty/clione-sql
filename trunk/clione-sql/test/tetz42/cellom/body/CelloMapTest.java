package tetz42.cellom.body;

import org.junit.Test;

import tetz42.cellom.TableManager;
import tetz42.cellom.annotation.EachHeader;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.test.Auty.*;


public class CelloMapTest {

	@Test
	public void test() throws Exception{
		TableManager<Foo> tm = TableManager.create(Foo.class);
		tm.newRow();
		tm.row().get().smap.get("tako");
		tm.row().get().smap.get("ika");
		tm.row().get().smap.get("namako");

		tm.row().get().smap.set("ika", "squid");

		assertEqualsWithFile(tm.toString(), getClass(), "first-test");

		tm.row().get().fmap.get("test").ssmap.get("tako");
		tm.row().get().fmap.get("test").ssmap.get("ika");
		tm.row().get().fmap.get("test").ssmap.get("namako");

		tm.row().get().fmap.get("test").ssmap.set("ika", "squid");

		assertEqualsWithFile(tm.toString(), getClass(), "second-test");
	}

}

class Foo{
	@EachHeader
	CelloMap<String> smap = CelloMap.create(String.class);

	@EachHeader
	CelloMap<FooFoo> fmap = CelloMap.create(FooFoo.class);
}

class FooFoo{
	@EachHeader
	CelloMap<String> ssmap = CelloMap.create(String.class);
}