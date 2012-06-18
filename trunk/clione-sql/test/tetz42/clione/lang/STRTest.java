package tetz42.clione.lang;

import static junit.framework.Assert.*;
import static tetz42.clione.SQLManager.*;

import org.junit.Test;

import tetz42.clione.exception.SecurityValidationException;
import tetz42.clione.lang.func.ClioneFunction;

public class STRTest {

	@Test
	public void security_safe() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		Instruction inst = cf.perform(params("P", "alias.T_MST_EMPLOYEE"));
		assertEquals(inst.getReplacement(), "alias.T_MST_EMPLOYEE");
	}

	@Test
	public void security_safe_singlequote() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		Instruction inst = cf.perform(params("P",
				"'I''m not a half the man I used be'"));
		assertEquals(inst.getReplacement(),
				"'I''m not a half the man I used be'");
	}

	@Test
	public void security_safe_prenthesis() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		Instruction inst = cf
				.perform(params("P", "SUBSTR(COL3, 9, LEN(COL3))"));
		assertEquals(inst.getReplacement(), "SUBSTR(COL3, 9, LEN(COL3))");
	}

	@Test
	public void security_safe_multicomment() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		Instruction inst = cf.perform(params("P", "AAA /*+ USE INDEX */ BBB"));
		assertEquals(inst.getReplacement(), "AAA /*+ USE INDEX */ BBB");
	}

	@Test
	public void security_safe_mix() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		Instruction inst = cf.perform(params("P",
				"EXISTS(/* %if(e) ')' */')/*''', 'tako')"));
		assertEquals(inst.getReplacement(),
				"EXISTS(/* %if(e) ')' */')/*''', 'tako')");
	}

	@Test
	public void security_unsafe_linecomment() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		try {
			cf.perform(params("P", "EXISTS(/* %if(e) ')' */')/*''', 'tako') --"));
			fail();
		} catch (SecurityValidationException e) {
			assertEquals(e.getMessage(),
					"Unsafe symbol, '--', is detected.\r\n");
		}
	}

	@Test
	public void security_unsafe_recursivecomment() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		try {
			cf.perform(params("P",
					"EXISTS(/* %if(e) /* tako */')' */')/*''', 'tako')"));
			fail();
		} catch (SecurityValidationException e) {
			assertEquals(e.getMessage(),
					"Recursive comment is not allowed.\r\n");
		}
	}

	@Test
	public void security_unsafe_commentNotClosed() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		try {
			cf.perform(params("P", "EXISTS(/* %if(e)  tako ')' ), 'tako')"));
			fail();
		} catch (SecurityValidationException e) {
			assertEquals(e.getMessage(), "Too much '/*'.\r\n");
		}
	}

	@Test
	public void security_unsafe_toomanyCloseComment() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		try {
			cf.perform(params("P", "EXISTS(/* %if(e) */ tako ')' , 'tako')*/"));
			fail();
		} catch (SecurityValidationException e) {
			assertEquals(e.getMessage(), "Too much '*/'.\r\n");
		}
	}

	@Test
	public void security_unsafe_toomuchOpenParenthesis() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		try {
			cf.perform(params("P", "EXISTS( %if(e)(  tako ')' , 'tako')"));
			fail();
		} catch (SecurityValidationException e) {
			assertEquals(e.getMessage(), "Too much '('.\r\n");
		}
	}

	@Test
	public void security_unsafe_toomuchCloseParenthesis() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		try {
			cf.perform(params("P", "EXISTS( %if(e)(  tako '(') , )'tako')"));
			fail();
		} catch (SecurityValidationException e) {
			assertEquals(e.getMessage(), "Too much ')'.\r\n");
		}
	}

	@Test
	public void security_unsafe_unmuchString() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!(P)");
		try {
			cf.perform(params("P", "EXISTS( %if(e)(  tako '('') , )'tako')"));
			fail();
		} catch (SecurityValidationException e) {
			assertEquals(e.getMessage(), "Unmatch String literal: [']\r\n");
		}
	}

	@Test
	public void str_nocheck() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%STR!!(P)");
		Instruction inst = cf.perform(params("P",
				"EXISTS( %if(e)(  tako '('') , )'tako')"));
		assertEquals(inst.replacement, "EXISTS( %if(e)(  tako '('') , )'tako')");
	}
}
