package tetz42.util;

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

}
