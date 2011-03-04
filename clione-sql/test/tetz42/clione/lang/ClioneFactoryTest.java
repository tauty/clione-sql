package tetz42.clione.lang;

import static tetz42.util.ObjDumper4j.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ClioneFactoryTest {

	@Test
	public void testParseByEmpty() {
		Clione clione = ClioneFactory.get().parse("");
		assertNull(clione);
	}

	@Test
	public void testParseByNormal() {
		Clione clione = ClioneFactory.get().parse("$!KEY");
		System.out.println(dumper(clione));
		assertNull(clione);
	}

}
