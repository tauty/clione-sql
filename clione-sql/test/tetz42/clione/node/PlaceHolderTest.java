package tetz42.clione.node;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.util.ArrayList;

import org.junit.Test;

import tetz42.clione.lang.ContextUtil;
import tetz42.clione.lang.Instruction;

public class PlaceHolderTest {

	@Test
	public void param_null() {
		// Instruction inst = new PlaceHolder("AAA", "= 'AAA'", 0)
		// .perform(params());
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "AAA", true, "= ", new StrNode("'AAA'"));
		Instruction inst = holder.perform(params());
		assertEqualsWithFile(inst, getClass(), "param_null");
	}

	@Test
	public void param_one() {

		// Instruction inst = new PlaceHolder("AAA", "IS NULL",
		// 0).perform(params(
		// "AAA", "value"));
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "AAA", true, "IS ", new StrNode("NULL"));
		Instruction inst = holder.perform(params("AAA", "value"));
		assertEqualsWithFile(inst, getClass(), "param_one");
	}

	@Test
	public void param_many() {
		// Instruction inst = new PlaceHolder("AAA", "In ('AAA', 'BBB', 'CCC')",
		// 0)
		// .perform(params("AAA", new String[] { "value1", "value2",
		// "value3", "value4", "value5", "value6", "value7",
		// "value8" }));
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "AAA", true, "IN ",
				new StrNode("('AAA', 'BBB', 'CCC')"));
		Instruction inst = holder.perform(params("AAA", new String[] {
				"value1", "value2", "value3", "value4", "value5", "value6",
				"value7", "value8" }));
		assertEqualsWithFile(inst, getClass(), "param_many");
	}

	@Test
	public void param_useInBack() {
		// Instruction inst = new PlaceHolder("?AAA", "= 'AAA'", 0)
		// .perform(params());
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "?AAA", true, "= ", new StrNode("'AAA'"));
		Instruction inst = holder.perform(params());
		assertEqualsWithFile(inst, getClass(), "param_useInBack");
	}

	@Test
	public void param_2000() {
		// Instruction inst = new PlaceHolder("?AAA", "= 'AAA'", 0)
		// .perform(params());
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "?AAA", true, "= ", new StrNode("'AAA'"));
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 2000; i++) {
			list.add("value" + i);
		}

		// oracle
		ContextUtil.setProductName("oracle");
		Instruction inst = holder.perform(params("AAA", list));
		assertEqualsWithFile(inst, getClass(), "param_2000");
	}

	@Test
	public void param_2001() {
		// Instruction inst = new PlaceHolder("?AAA", "= 'AAA'", 0)
		// .perform(params());
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "?AAA", true, "= ", new StrNode("'AAA'"));
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 2001; i++) {
			list.add("value" + i);
		}

		// oracle
		ContextUtil.setProductName("oracle");
		Instruction inst = holder.perform(params("AAA", list));
		assertEqualsWithFile(inst, getClass(), "param_2001");
	}

	@Test
	public void param_over1000() {
		// Instruction inst = new PlaceHolder("?AAA", "= 'AAA'", 0)
		// .perform(params());
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "?AAA", true, "= ", new StrNode("'AAA'"));
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 3333; i++) {
			list.add("value" + i);
		}

		// oracle
		ContextUtil.setProductName("oracle");
		Instruction inst = holder.perform(params("AAA", list));
		assertEqualsWithFile(inst, getClass(), "param_over1000");
	}

	@Test
	public void param_over1000_not() {
		// Instruction inst = new PlaceHolder("?AAA", "= 'AAA'", 0)
		// .perform(params());
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "?AAA", false, "<> ", new StrNode("'AAA'"));
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 3333; i++) {
			list.add("value" + i);
		}

		// oracle
		ContextUtil.setProductName("oracle");
		Instruction inst = holder.perform(params("AAA", list));
		assertEqualsWithFile(inst, getClass(), "param_over1000_not");
	}

	@Test
	public void param10_like() {
		// Instruction inst = new PlaceHolder("?AAA", "= 'AAA'", 0)
		// .perform(params());
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "?AAA", true, "LIKE ", new StrNode("'AAA'"));
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			list.add("value" + i);
		}

		// oracle
		Instruction inst = holder.perform(params("AAA", list));
		assertEqualsWithFile(inst, getClass(), "param10_like");
	}
	
	@Test
	public void param10_like_useL() {
		// Instruction inst = new PlaceHolder("?AAA", "= 'AAA'", 0)
		// .perform(params());
		ConditionPlaceHolder holder = new ConditionPlaceHolder(new StrNode(
				"ID "), "%L ?AAA, '%'", true, "LIKE ", new StrNode("'AAA'"));
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			list.add("val_" + i);
		}

		// oracle
		Instruction inst = holder.perform(params("AAA", list));
		assertEqualsWithFile(inst, getClass(), "param10_like_useL");
	}
}
