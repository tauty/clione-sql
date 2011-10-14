package tetz42.clione.lang;

import static org.junit.Assert.*;
import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.util.Arrays;

import org.junit.Test;

import tetz42.clione.exception.ParameterNotFoundException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.util.ObjDumper4j;

public class ClioneFuncTest {

	@Test
	public void param() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("PARAM");
		Instruction instruction = cf.perform(params("PARAM", 100));
		assertEqualsWithFile(instruction, getClass(), "param");
	}

	@Test
	public void param_bind_list() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("PARAM");
		Instruction instruction = cf.perform(params("PARAM", Arrays.asList(1,
				10, 100)));
		assertEqualsWithFile(instruction, getClass(), "param_bind_list");
	}

	@Test
	public void param_bind_ary() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("PARAM");
		Instruction instruction = cf.perform(params("PARAM", new Object[] {
				100, 200, 300, 400, 500 }));
		assertEqualsWithFile(instruction, getClass(), "param_bind_ary");
	}

	@Test
	public void param_bind_many() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"PARAM1 PARAM2 PARAM3 PARAM4");
		Instruction instruction = cf.perform(params("PARAM1", 1)
				.$("PARAM2", 10).$("PARAM4", 100));
		assertEqualsWithFile(instruction, getClass(), "param_bind_many");
		assertEqualsWithFile(instruction.merge(), getClass(),
				"param_bind_many_merged");
	}

	@Test
	public void doller_bind_null() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("$PARAM");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "doller_bind_null");
	}

	@Test
	public void dollers_bind_null() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("$PARAM1 $PARAM2");
		Instruction instruction = cf.perform(params("PARAM1", "tako"));
		assertEqualsWithFile(instruction, getClass(), "dollers_bind_null");
		assertEqualsWithFile(instruction.merge(), getClass(),
				"dollers_bind_null_merged");
	}

	@Test
	public void doller_not_bind_null() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("$!PARAM");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "doller_not_bind_null");
	}

	@Test
	public void doller_not_bind_value() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("$!PARAM");
		Instruction instruction = cf.perform(params("PARAM", "value"));
		assertEqualsWithFile(instruction, getClass(), "doller_not_bind_value");
	}

	@Test
	public void hatenas_bind_first() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"?PARAM1 ?PARAM2 ?PARAM3");
		Instruction instruction = cf.perform(params("PARAM1", "tako"));
		assertEqualsWithFile(instruction, getClass(), "hatenas_bind_first");
		instruction = cf.perform(params("PARAM1", "tako").$("PARAM2", "ika").$(
				"PARAM3", "namako"));
		assertEqualsWithFile(instruction, getClass(), "hatenas_bind_first");
	}

	@Test
	public void hatenas_bind_last() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"?PARAM1 ?PARAM2 ?PARAM3");
		Instruction instruction = cf.perform(params("PARAM3", "namako"));
		assertEqualsWithFile(instruction, getClass(), "hatenas_bind_last");
	}

	@Test
	public void hatenas_bind_none() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"?PARAM1 ?PARAM2 ?PARAM3");
		Instruction instruction = cf.perform(params("TEKITOU", "namako"));
		assertEqualsWithFile(instruction, getClass(), "hatenas_bind_none");
	}

	@Test
	public void amper_bind_one() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("&PARAM");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "amper_bind_one");
	}

	@Test
	public void amper_bind_none() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("&PARAM");
		Instruction instruction = cf.perform(paramsOn());
		assertEqualsWithFile(instruction, getClass(), "amper_bind_none");
	}

	@Test
	public void parenthesises_have_three_param() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"(PARAM1 PARAM2, PARAM3)");
		Instruction instruction = cf.perform(paramsOn());
		assertEqualsWithFile(instruction, getClass(),
				"parenthesises_have_three_param");
	}

	@Test
	public void parenthesises_have_three_param_bind_all() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"(PARAM1 PARAM2, PARAM3)");
		Instruction instruction = cf.perform(params("PARAM1", "tako").$(
				"PARAM2", "ika").$("PARAM3", "namako"));
		assertEqualsWithFile(instruction, getClass(),
				"parenthesises_have_three_param_bind_all");
	}

	@Test
	public void atmark_bind_one() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"(PARAM1 PARAM2, @PARAM3)");
		Instruction instruction = cf.perform(params("PARAM1", "tako").$(
				"PARAM3", "namako"));
		assertEqualsWithFile(instruction, getClass(), "atmark_bind_one");
	}

	@Test
	public void atmark_bind_none() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"(PARAM1 PARAM2, @PARAM3)");
		try {
			cf.perform(params("PARAM1", "tako").$("PARAM2", "namako"));
			fail();
		} catch (ParameterNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void doublequote() {
		ClioneFunction cf = ClioneFuncFactory
				.get()
				.parse(
						"\"SELECT * FROM FOO WHERE ID = /* ID */ AND NAME = /* NAME */ \"");
		System.out.println(ObjDumper4j.dumper(cf));
		Instruction inst = cf.perform(params("NAME", "TAKAKO").$("ID", 100));
		assertEqualsWithFile(inst, getClass(), "doublequote");
	}

	@Test
	public void pipe() {
		ClioneFunction cf = ClioneFuncFactory
				.get()
				.parse(
						"|SELECT TEL, '\\' FROM FOO WHERE ID = /* ID */ AND NAME = /* NAME */");
		System.out.println(ObjDumper4j.dumper(cf));
		Instruction inst = cf.perform(params("NAME", "TAKAKO").$("ID", 100));
		assertEqualsWithFile(inst, getClass(), "semicolon");
	}

	@Test
	public void colon() {
		ClioneFunction cf = ClioneFuncFactory
				.get()
				.parse(
						":SELECT * FROM FOO WHERE ID = /\\* ID *\\/ AND NAME = /\\* NAME *\\/");
		Instruction inst = cf.perform(params("NAME", "TAKAKO").$("ID", 100));
		System.out.println(ObjDumper4j.dumper(cf));
		assertEqualsWithFile(inst, getClass(), "colon");
	}

	@Test
	public void singlequote() {
		ClioneFunction cf = ClioneFuncFactory
				.get()
				.parse(
						"'SELECT * FROM FOO WHERE ID = /* ID */ AND NAME = /* NAME */ '");
		Instruction inst = cf.perform(params("NAME", "TAKAKO").$("ID", 100));
		assertEqualsWithFile(inst, getClass(), "singlequote");
	}

	@Test
	public void singlequote_escape() {
		ClioneFunction cf = ClioneFuncFactory
				.get()
				.parse(
						"'SELECT tel, ''\\\\'' FROM FOO WHERE ID = /\\* ID *\\/ AND NAME = /* NAME */ '");
		Instruction inst = cf.perform(params("NAME", "TAKAKO").$("ID", 100));
		assertEqualsWithFile(inst, getClass(), "singlequote_escape");
	}
}
