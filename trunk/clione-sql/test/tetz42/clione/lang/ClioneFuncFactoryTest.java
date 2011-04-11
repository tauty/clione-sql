package tetz42.clione.lang;

import static org.junit.Assert.*;
import static tetz42.test.Util.*;

import org.junit.Test;

public class ClioneFuncFactoryTest {

	@Test
	public void testParseByEmpty() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse("");
		assertNull(clione);
	}

	@Test
	public void param() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse("KEY");
		assertEqualsWithFile(clione, getClass(), "param");
	}

	@Test
	public void param_literal() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"KEY :LITERAL");
		assertEqualsWithFile(clione, getClass(), "param_literal");
	}

	@Test
	public void param_escaped_literal() {
		ClioneFunction clione = ClioneFuncFactory
				.get("FromTest")
				.parse(
						"KEY :/* INNER_PARAM *\\/'tako' IN('\\\\100', '\\\\200', '\\\\nec')");
		assertEqualsWithFile(clione, getClass(), "param_escaped_literal");
	}

	@Test
	public void param_doller() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"$KEY :LITERAL");
		assertEqualsWithFile(clione, getClass(), "param_doller");
		clione = ClioneFuncFactory.get("FromTest").parse("$!KEY PARAM");
		assertEqualsWithFile(clione, getClass(), "param_doller2");
	}

	@Test
	public void param_default() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse("?KEY");
		assertEqualsWithFile(clione, getClass(), "param_default");
	}

	@Test
	public void param_default_with_param() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"?KEY PARAM");
		assertEqualsWithFile(clione, getClass(), "param_default_with_param");
	}

	@Test
	public void param_require() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse("@KEY");
		assertEqualsWithFile(clione, getClass(), "param_require");
	}

	@Test
	public void param_part() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"#KEY :LITERAL");
		assertEqualsWithFile(clione, getClass(), "param_part");
	}

	@Test
	public void param_amper() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"&KEY :LITERAL");
		assertEqualsWithFile(clione, getClass(), "param_amper");
	}

	@Test
	public void param_quote() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"&KEY 'tako'");
		assertEqualsWithFile(clione, getClass(), "param_quote");
	}

	@Test
	public void param_escaped_quote() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"&KEY 'It''s alright!'");
		assertEqualsWithFile(clione, getClass(), "param_escaped_quote");
	}

	@Test
	public void param_doublequote() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"&KEY \"tako\"");
		assertEqualsWithFile(clione, getClass(), "param_doublequote");
	}

	@Test
	public void param_escaped_doublequote() {
		ClioneFunction clione = ClioneFuncFactory.get("FromTest").parse(
				"&KEY \"She said, \"\"You don't understand myself.\"\"\"");
		assertEqualsWithFile(clione, getClass(), "param_escaped_doublequote");
	}
}
