package tetz42.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PerformanceMeterTest {

	@Test
	public void test() throws Exception {

		PerformanceMeter.start("test");
		Thread.sleep(10);
		PerformanceMeter.end("test");

		PerformanceMeter.start("test");
		for (int i = 0; i < 10; i++) {
			PerformanceMeter.start();
			Thread.sleep(10);
			PerformanceMeter.end();
		}
		PerformanceMeter.end("test");

		PerformanceMeter.show();
	}

	@Test
	public void testMemory() throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		// List<Integer> list = new LinkedList<Integer>();

		PerformanceMeter.start("testMemory");
		for (int i = 0; i < 10000; i++) {
			list.add(i);
		}
		PerformanceMeter.end("testMemory");

		PerformanceMeter.show();
	}
}
