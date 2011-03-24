package tetz42.clione.lang;

import static tetz42.util.ObjDumper4j.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ClioneFactoryTest {

	@Test
	public void testParseByEmpty() {
		final String src = "";
		System.out.println("src = " + src);
		Clione clione = ClioneFactory.get().parse(src);
		assertNull(clione);
	}

	@Test
	public void param() {
		final String src = "KEY";
		System.out.println("src = " + src);
		Clione clione = ClioneFactory.get().parse(src);
		System.out.println(dumper(clione));
		assertNotNull(clione);
	}

	@Test
	public void param_literal() {
		final String src = "KEY :LITERAL";
		System.out.println("src = " + src);
		Clione clione = ClioneFactory.get().parse(src);
		System.out.println(dumper(clione));
		assertNotNull(clione);
	}

	@Test
	public void param_doller() {
		final String src = "$KEY :LITERAL";
		System.out.println("src = " + src);
		Clione clione = ClioneFactory.get().parse(src);
		System.out.println(dumper(clione));
		assertNotNull(clione);
	}

}
