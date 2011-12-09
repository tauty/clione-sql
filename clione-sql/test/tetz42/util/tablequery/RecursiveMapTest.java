package tetz42.util.tablequery;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class RecursiveMapTest {

	@Test
	public void zero() throws Exception {
		RecursiveMap<String> rec = new RecursiveMap<String>();
		rec.setValue("zero");
		assertThat(rec.getValue(), is("zero"));
	}

	@Test
	public void one() throws Exception {
		RecursiveMap<String> rec = new RecursiveMap<String>();
		rec.get("0").setValue("one");
		assertThat(rec.get("0").getValue(), is("one"));
	}

	@Test
	public void two() throws Exception {
		RecursiveMap<String> rec = new RecursiveMap<String>();
		rec.get("0").get("1").setValue("two");
		assertThat(rec.get("0").get("1").getValue(), is("two"));
	}

	@Test
	public void two_two() throws Exception {
		RecursiveMap<String> rec = new RecursiveMap<String>();
		rec.get("東京").get("大宮").get("宇都宮").setValue("餃子");
		rec.get("東京").get("大宮").get("高崎").setValue("サル山");
		assertThat(rec.getValue("東京", "大宮", "宇都宮"), is("餃子"));
		assertThat(rec.getValue("東京", "大宮", "高崎"), is("サル山"));
	}

	@Test
	public void useGet() throws Exception {
		RecursiveMap<String> rec = new RecursiveMap<String>();
		rec.get("東京", "大宮", "宇都宮").setValue("餃子");
		rec.get("東京", "大宮", "高崎").setValue("サル山");
		assertThat(rec.getValue("東京", "大宮", "宇都宮"), is("餃子"));
		assertThat(rec.getValue("東京", "大宮", "高崎"), is("サル山"));
		assertThat(rec.get("東京", "大宮", "宇都宮").getValue(), is("餃子"));
		assertThat(rec.get("東京", "大宮", "高崎").getValue(), is("サル山"));
	}
}
