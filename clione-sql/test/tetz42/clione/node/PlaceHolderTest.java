package tetz42.clione.node;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Util.*;

import org.junit.Test;

import tetz42.clione.lang.Instruction;

public class PlaceHolderTest {

	@Test
	public void param_null() {
		Instruction inst = new PlaceHolder("AAA", "= 'AAA'").perform(params());
		assertEqualsWithFile(inst, getClass(), "param_null");
	}

	@Test
	public void param_one() {
		Instruction inst = new PlaceHolder("AAA", "IS NULL").perform(params(
				"AAA", "value"));
		assertEqualsWithFile(inst, getClass(), "param_one");
	}

	@Test
	public void param_many() {
		Instruction inst = new PlaceHolder("AAA", "In ('AAA', 'BBB', 'CCC')").perform(params(
				"AAA", new String[] { "value1", "value2", "value3", "value4",
						"value5", "value6", "value7", "value8" }));
		assertEqualsWithFile(inst, getClass(), "param_many");
	}

	@Test
	public void param_useInBack() {
		Instruction inst = new PlaceHolder("?AAA", "= 'AAA'").perform(params());
		assertEqualsWithFile(inst, getClass(), "param_useInBack");
	}

}
