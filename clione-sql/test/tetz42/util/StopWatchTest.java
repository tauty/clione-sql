package tetz42.util;

import org.junit.Test;


public class StopWatchTest {
	
	@Test
	public void test() throws Exception{
		StopWatch.init(System.out);
		
		StopWatch.start("test");
		Thread.sleep(10);
		StopWatch.end("test");
		
		StopWatch.show();
	}
	
}
