package tetz42.cello;

import static tetz42.test.Auty.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import tetz42.cellom.RecursiveMap;

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

	@Test
	public void useGet_list() throws Exception {
		RecursiveMap<String> rec = new RecursiveMap<String>();
		rec.get(Arrays.asList("東京", "大宮", "宇都宮")).setValue("餃子");
		rec.get(Arrays.asList("東京", "大宮", "高崎")).setValue("サル山");
		assertThat(rec.getValue(Arrays.asList("東京", "大宮", "宇都宮")), is("餃子"));
		assertThat(rec.getValue(Arrays.asList("東京", "大宮", "高崎")), is("サル山"));
		assertThat(rec.get(Arrays.asList("東京", "大宮", "宇都宮")).getValue(),
				is("餃子"));
		assertThat(rec.get(Arrays.asList("東京", "大宮", "高崎")).getValue(),
				is("サル山"));
	}

	@Test
	public void keys() throws Exception {
		RecursiveMap<Integer> rec = new RecursiveMap<Integer>();
		RecursiveMap<Integer> tmp = rec;
		for (int i = 0; i < 10; i++) {
			tmp.setValue(i);
			tmp = tmp.get("" + i);
			assertEqualsWithFile(tmp.keys(), getClass(), "keys" + i);
		}
		assertEqualsWithFile(rec, getClass(), "keys");
	}
}
