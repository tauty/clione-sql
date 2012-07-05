package tetz42.clione.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.clione.SQLManager.*;

import org.junit.Test;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;

public class SQLTest {

	@Test
	public void normal() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%SQL!(P)");
		Instruction inst = cf.perform(paramsOn("isBar").$("P",
				"FROM /* %if isBar 'TABLE_BAR' */TABLE_FOO"));
		assertEquals(inst.replacement, "FROM TABLE_BAR");
	}

	@Test
	public void normal2() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%SQL!(P)");
		Instruction inst = cf.perform(params("Q", "ID000").$("P",
				"WHERE id /* Q */= 'ID999'"));
		assertThat(inst.replacement, is("WHERE id  =  ?"));
		assertThat(inst.params.get(0), is((Object) "ID000"));
	}

	@Test
	public void exception() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%SQL!(P)");
		try {
			cf.perform(paramsOn("isBar").$("P",
					"FROM /* %if isBar 'TABLE_BAR' +/TABLE_FOO"));
			fail();
		} catch (ClioneFormatException e) {
			assertThat(
					e.getMessage(),
					is("SQL Format Error: too much '/*'\r\n"
							+ "Java String passed as parameter, line number:1"));
		}
	}
}
