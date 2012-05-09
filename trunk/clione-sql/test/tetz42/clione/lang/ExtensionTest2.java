package tetz42.clione.lang;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import org.junit.Test;

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

	// TODO consider about this case.
	// @Test
	// public void eq_with_str_and_int_false() {
	// ClioneFunction cf =
	// ClioneFuncFactory.get().parse("%if %eq(PARAM, 8) 'tako'");
	// Instruction instruction = cf.perform(params("PARAM", "tako"));
	// assertEqualsWithFile(instruction, getClass(),
	// "eq_with_str_and_int_false");
	// }

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

}
