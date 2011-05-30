package tetz42.clione.lang;

import static org.junit.Assert.*;
import static tetz42.test.Util.*;

import java.util.Arrays;

import org.junit.Test;

import static tetz42.clione.SQLManager.*;
import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;

public class ClioneFuncTest {

	@Test
	public void param() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"PARAM");
		Instruction instruction = cf.perform(params("PARAM", 100));
		assertEqualsWithFile(instruction, getClass(), "param");
	}

	@Test
	public void param_bind_list() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"PARAM");
		Instruction instruction = cf.perform(params("PARAM",
				Arrays.asList(1, 10, 100)));
		assertEqualsWithFile(instruction, getClass(), "param_bind_list");
	}

	@Test
	public void param_bind_ary() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"PARAM");
		Instruction instruction = cf.perform(params("PARAM", new Object[] {
				100, 200, 300, 400, 500 }));
		assertEqualsWithFile(instruction, getClass(), "param_bind_ary");
	}

	@Test
	public void param_bind_many() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"PARAM1 PARAM2 PARAM3 PARAM4");
		Instruction instruction = cf.perform(params("PARAM1", 1)
				.$("PARAM2", 10).$("PARAM4", 100));
		assertEqualsWithFile(instruction, getClass(), "param_bind_many");
		assertEqualsWithFile(instruction.merge(), getClass(),
				"param_bind_many_merged");
	}

	@Test
	public void doller_bind_null() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"$PARAM");
		Instruction instruction = cf.perform(params("AAA", null));
		assertEqualsWithFile(instruction, getClass(), "doller_bind_null");
	}

	@Test
	public void dollers_bind_null() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"$PARAM1 $PARAM2");
		Instruction instruction = cf.perform(params("PARAM1", "tako"));
		assertEqualsWithFile(instruction, getClass(), "dollers_bind_null");
		assertEqualsWithFile(instruction.merge(), getClass(), "dollers_bind_null_merged");
	}

}
