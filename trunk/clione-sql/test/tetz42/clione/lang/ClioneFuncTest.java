package tetz42.clione.lang;

import static org.junit.Assert.*;
import static tetz42.test.Util.*;

import org.junit.Test;

import static tetz42.clione.SQLManager.*;
import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;

public class ClioneFuncTest {
	
	@Test
	public void param(){
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse("PARAM");
		Instruction instruction = cf.perform(params("PARAM", 100));
		assertEqualsWithFile(instruction, getClass(), "param");
	}

	@Test
	public void param_bind_list(){
		ClioneFunction cf = ClioneFuncFactory.get("ClioneFuncTest").parse("PARAM");
		Instruction instruction = cf.perform(params("PARAM", 100));
		assertEqualsWithFile(instruction, getClass(), "param");
	}

}
