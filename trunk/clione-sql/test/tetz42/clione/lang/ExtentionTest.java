package tetz42.clione.lang;

import static org.junit.Assert.*;
import static tetz42.clione.SQLManager.*;
import static tetz42.test.Util.*;

import java.util.Arrays;

import org.junit.Test;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.exception.ParameterNotFoundException;
import tetz42.clione.lang.func.ClioneFunction;

public class ExtentionTest {

	@Test
	public void if_true() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF PARAM");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "if_true");
	}

	@Test
	public void if_true_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF PARAM :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "if_true_with_sql");
	}

	@Test
	public void if_params_alltrue_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF(PARAM1 PARAM2) :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM1", "PARAM2"));
		assertEqualsWithFile(instruction, getClass(),
				"if_params_alltrue_with_sql");
	}

	@Test
	public void if_params_onetrue_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF(PARAM1 PARAM2) :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM2"));
		assertEqualsWithFile(instruction, getClass(),
				"if_params_onetrue_with_sql");
	}

	@Test
	public void if_params_allfalse_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF(PARAM1 PARAM2) :ASC");
		Instruction instruction = cf.perform(paramsOn());
		assertEqualsWithFile(instruction, getClass(),
				"if_params_allfalse_with_sql");
	}

	@Test
	public void if_false() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF PARAM");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "if_false");
	}

	@Test
	public void if_false_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF PARAM :ASC");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "if_false_with_sql");
	}

	@Test
	public void if_no_param() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF ");
		try {
			cf.perform(params());
			fail();
		} catch (ClioneFormatException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void ifln_true() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IFLN PARAM");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "ifln_true");
	}

	@Test
	public void ifln_true_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IFLN PARAM :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "ifln_true_with_sql");
	}

	@Test
	public void ifln_false() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IFLN PARAM");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "ifln_false");
	}

	@Test
	public void ifln_false_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IFLN PARAM :ASC");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "ifln_false_with_sql");
	}

	@Test
	public void ifln_no_param() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IFLN ");
		try {
			cf.perform(params());
			fail();
		} catch (ClioneFormatException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void l_param_exsists_forwardmatch() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%L PARAM '%'");
		Instruction instruction = cf.perform(params("PARAM", "tako"));
		assertEqualsWithFile(instruction, getClass(),
				"l_param_exsists_forwardmatch");
	}

	@Test
	public void l_param_exsists_partmatch() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%L '%' PARAM '%'");
		Instruction instruction = cf.perform(params("PARAM", "tako"));
		assertEqualsWithFile(instruction, getClass(),
				"l_param_exsists_partmatch");
	}

	@Test
	public void l_param_exsists_partmatch2() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%L '%' PARAM '%'");
		Instruction instruction = cf.perform(params("PARAM", "tako_100%!"));
		assertEqualsWithFile(instruction, getClass(),
				"l_param_exsists_partmatch2");
	}

	@Test
	public void l_params_exsists_partmatch() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%L '%' PARAM1 '_' PARAM2 '%'");
		Instruction instruction = cf.perform(params("PARAM1", "#100%_").$(
				"PARAM2", "#200%_"));
		assertEqualsWithFile(instruction, getClass(),
				"l_params_exsists_partmatch");
	}

}
