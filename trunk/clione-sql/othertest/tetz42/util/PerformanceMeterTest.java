package tetz42.util;

import java.util.ArrayList;
import java.util.LinkedList;
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

		perform(new ArrayList<Integer>());
		perform(new LinkedList<Integer>());

		PerformanceMeter.show();
	}

	private void perform(List<Integer> list) {
		PerformanceMeter.start("testMemory - "
				+ list.getClass().getSimpleName());
		for (int i = 0; i < 100000; i++) {
			list.add(i);
		}
		PerformanceMeter.end("testMemory - " + list.getClass().getSimpleName());
	}
}
