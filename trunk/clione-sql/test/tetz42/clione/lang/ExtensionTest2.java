package tetz42.clione.lang;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import org.junit.Test;

import tetz42.clione.exception.ImpossibleToCompareException;
import tetz42.clione.lang.func.ClioneFunction;

public class ExtensionTest2 {
	@Test
	public void eq_with_string_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %eq(PARAM, 'tako') 8");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "eq_with_string_false");
	}

	@Test
	public void eq_with_string_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %eq(PARAM, 'tako') 8");
		Instruction instruction = cf.perform(params("PARAM", "tako"));
		assertEqualsWithFile(instruction, getClass(), "eq_with_string_true");
	}

	@Test
	public void eq_with_str_and_int_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %eq(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", "tako"));
		assertEqualsWithFile(instruction, getClass(),
				"eq_with_str_and_int_false");
	}

	@Test
	public void eq_with_int_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %eq(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 10));
		assertEqualsWithFile(instruction, getClass(), "eq_with_int_false");
	}

	@Test
	public void eq_with_int_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %eq(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 8));
		assertEqualsWithFile(instruction, getClass(), "eq_with_int_true");
	}

	@Test
	public void gt_with_int_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %gt(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 10));
		assertEqualsWithFile(instruction, getClass(), "gt_with_int_true");
	}

	@Test
	public void gt_with_int_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %gt(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 8));
		assertEqualsWithFile(instruction, getClass(), "gt_with_int_false");
	}

	@Test
	public void gt_with_int_false2() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %gt(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 4));
		assertEqualsWithFile(instruction, getClass(), "gt_with_int_false");
	}

	@Test(expected = ImpossibleToCompareException.class)
	public void gt_with_int_and_string() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %gt(PARAM, '8') 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 4));
		assertEqualsWithFile(instruction, getClass(), "gt_with_int_and_string");
	}

	@Test(expected = ImpossibleToCompareException.class)
	public void gt_with_string_and_int() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %gt(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", "8"));
		assertEqualsWithFile(instruction, getClass(), "gt_with_int_and_string");
	}

	@Test
	public void ge_with_int_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %ge(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 8));
		assertEqualsWithFile(instruction, getClass(), "ge_with_int_true");
	}

	@Test
	public void ge_with_int_true2() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %ge(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 10));
		assertEqualsWithFile(instruction, getClass(), "ge_with_int_true2");
	}

	@Test
	public void ge_with_int_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %ge(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 7));
		assertEqualsWithFile(instruction, getClass(), "ge_with_int_false");
	}

	@Test
	public void lt_with_int_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %lt(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 7));
		assertEqualsWithFile(instruction, getClass(), "lt_with_int_true");
	}

	@Test
	public void lt_with_int_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %lt(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 8));
		assertEqualsWithFile(instruction, getClass(), "lt_with_int_false");
	}

	@Test
	public void lt_with_int_false2() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %lt(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 9));
		assertEqualsWithFile(instruction, getClass(), "lt_with_int_false");
	}

	@Test(expected = ImpossibleToCompareException.class)
	public void lt_with_int_and_string() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %lt(PARAM, '8') 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 4));
		assertEqualsWithFile(instruction, getClass(), "gt_with_int_and_string");
	}

	@Test(expected = ImpossibleToCompareException.class)
	public void lt_with_string_and_int() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %lt(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", "8"));
		assertEqualsWithFile(instruction, getClass(), "gt_with_int_and_string");
	}

	@Test
	public void le_with_int_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %le(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 8));
		assertEqualsWithFile(instruction, getClass(), "le_with_int_true");
	}

	@Test
	public void le_with_int_true2() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %le(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 7));
		assertEqualsWithFile(instruction, getClass(), "le_with_int_true2");
	}

	@Test
	public void le_with_int_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %le(PARAM, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM", 9));
		assertEqualsWithFile(instruction, getClass(), "le_with_int_false");
	}

	@Test
	public void eq_many_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %le(PARAM1, PARAM2, PARAM3, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", 8).$("PARAM2", 8)
				.$("PARAM3", 8));
		assertEqualsWithFile(instruction, getClass(), "eq_many_true");
	}

	@Test
	public void eq_many_true2() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %le(PARAM1, PARAM2, PARAM3, 'ika') 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", "ika").$(
				"PARAM2", "ika").$("PARAM3", "ika"));
		assertEqualsWithFile(instruction, getClass(), "eq_many_true");
	}

	@Test
	public void eq_many_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %le(PARAM1, PARAM2, PARAM3, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", 8).$("PARAM2", 9)
				.$("PARAM3", 8));
		assertEqualsWithFile(instruction, getClass(), "eq_many_false");
	}

	@Test
	public void gt_many_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %gt(PARAM1, PARAM2, PARAM3, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", 64).$("PARAM2",
				32).$("PARAM3", 16));
		assertEqualsWithFile(instruction, getClass(), "gt_many_true");
	}

	@Test
	public void ge_many_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %ge(PARAM1, PARAM2, PARAM3, 'ika') 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", "iko").$(
				"PARAM2", "ike").$("PARAM3", "ika"));
		assertEqualsWithFile(instruction, getClass(), "ge_many_true");
	}

	@Test
	public void gt_many_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %gt(PARAM1, PARAM2, PARAM3, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", 8).$("PARAM2", 7)
				.$("PARAM3", 6));
		assertEqualsWithFile(instruction, getClass(), "gt_many_false");
	}

	@Test
	public void lt_many_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %lt(PARAM1, PARAM2, PARAM3, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", 1).$("PARAM2", 2)
				.$("PARAM3", 4));
		assertEqualsWithFile(instruction, getClass(), "lt_many_true");
	}

	@Test
	public void le_many_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %le('ika', PARAM1, PARAM2, PARAM3, 'iku') 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", "ika").$(
				"PARAM2", "ike").$("PARAM3", "iko"));
		assertEqualsWithFile(instruction, getClass(), "le_many_true");
	}

	@Test
	public void lt_many_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %lt(PARAM1, PARAM2, PARAM3, 8) 'tako'");
		Instruction instruction = cf.perform(params("PARAM1", 6)
				.$("PARAM2", 7).$("PARAM3", 8));
		assertEqualsWithFile(instruction, getClass(), "lt_many_false");
	}

}
