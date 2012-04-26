package tetz42.cellom.parsar.queue;

import static org.junit.Assert.*;
import static tetz42.test.Auty.*;
import static tetz42.util.Util.*;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;

import tetz42.cellom.parsar.queue.entity.InitialTest;
import tetz42.cellom.parsar.queue.entity.StrAndInt;
import tetz42.cellom.parsar.queue.entity.SubStrAndInt;

public class QueueParsarTest {

	@Test
	public void initialTest() {
		InitialTest init = QueueParsar.parse(InitialTest.class,
				InitialTest.TEST_STR);
		assertEqualsWithFile(init, getClass(), "initialTest");
	}

	@Test
	public void strAndInt() {
		StrAndInt si = QueueParsar.parse(StrAndInt.class, StrAndInt.TEST_STR);
		assertEqualsWithFile(si, getClass(), "strAndInt");
	}

	@Test
	public void short_strAndInt() {
		String s = StrAndInt.TEST_STR;
		StrAndInt si = QueueParsar.parse(StrAndInt.class, s.substring(0, s
				.length() - 1));
		assertNull(si);
	}

	@Test
	public void inputStream() {
		StrAndInt si = QueueParsar.parse(StrAndInt.class,
				new ByteArrayInputStream(StrAndInt.TEST_STR.getBytes()));
		assertEqualsWithFile(si, getClass(), "strAndInt");
	}

	@Test
	public void short_inputStream() {
		String s = StrAndInt.TEST_STR;
		StrAndInt si = QueueParsar.parse(StrAndInt.class,
				new ByteArrayInputStream(s.substring(0, s.length() - 1)
						.getBytes()));
		assertNull(si);
	}

	@Test
	public void short_childFieldNull() {
		String s = StrAndInt.TEST_STR;
		StrAndInt si = QueueParsar.parse(StrAndInt.class,
				new ByteArrayInputStream(s.substring(0, 15).getBytes()));
		assertNull(si);
	}

	@Test
	public void parseAll() {
		String s = mkStringByCRLF(StrAndInt.TEST_STR,
				StrAndInt.TEST_STR, "\r\n", StrAndInt.TEST_STR, "\r\n", "\r\n");
		List<StrAndInt> list = QueueParsar.parseAll(StrAndInt.class,
				new ByteArrayInputStream(s.getBytes()));
		assertEqualsWithFile(list, getClass(), "parseAll");
	}

	@Test
	public void override() {
		StrAndInt si = QueueParsar.parse(SubStrAndInt.class,
				new ByteArrayInputStream(SubStrAndInt.TEST_STR.getBytes()));
		assertEqualsWithFile(si, getClass(), "override");
	}

}
