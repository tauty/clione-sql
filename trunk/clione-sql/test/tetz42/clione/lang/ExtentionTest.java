package tetz42.clione.lang;

import static org.junit.Assert.*;
import static tetz42.clione.SQLManager.*;
import static tetz42.test.Util.*;

import java.util.Arrays;

import org.junit.Test;

import tetz42.clione.exception.ParameterNotFoundException;
import tetz42.clione.lang.func.ClioneFunction;

public class ExtentionTest {

	@Test
	public void if_true() {
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse(
				"%IF PARAM");
		Instruction instruction = cf.perform(params("PARAM", true));
		assertEqualsWithFile(instruction, getClass(), "if_true");
	}

}
