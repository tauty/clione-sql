package tetz42.cellom.parsar.queue;

import static tetz42.test.Auty.*;
import org.junit.Test;

import tetz42.cellom.parsar.queue.annotation.StrAndInt;
import tetz42.cellom.parsar.queue.entity.InitialTest;

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

}
