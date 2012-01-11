/*
 * Copyright 2010 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tetz42.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class provides timer function easy to use.
 * <p>
 * 
 * [sample]<br>
 * StopWatch sw = new StopWatch();<br>
 * sw.start("key1");<br>
 * &emsp;&emsp; :<br>
 * sw.start("key2");<br>
 * &emsp;&emsp; :<br>
 * sw.end("key1");<br>
 * &emsp;&emsp; :<br>
 * sw.start("key1");<br>
 * &emsp;&emsp; :<br>
 * sw.end("key1");<br>
 * sw.end("key2");<br>
 * <br>
 * System.out.println(sw);<br>
 * <br>
 * - result<br>
 * key1(140msec, 2time, average:70.0msec)<br>
 * key2(100msec, 1time, average:100.0msec)<br>
 * Summary:150msec<br>
 * 
 * @version 1.0
 * @author tetz
 */
public class PerformanceMeter {

	public static enum OutputTiming {
		ALL, END, SHOW
	}

	public static final String CRLF = System.getProperty("line.separator");

	private static OutputStream out = null;
	private static OutputTiming oc = null;

	public void test() {
		PerformanceMeter.init(System.out, OutputTiming.ALL);

		PerformanceMeter.start("test");
		// do process
		PerformanceMeter.start("inside-test");
		// do process
		PerformanceMeter.end("inside-end");
		// do process
		PerformanceMeter.end("test");

		PerformanceMeter.show();
	}

	public static void init(OutputStream os, OutputTiming o) {
		out = os;
		oc = o;
	}

	private static Map<String, Long> startMap = new HashMap<String, Long>();
	private static Map<String, Unit> resultMap = new LinkedHashMap<String, Unit>();

	/**
	 * Starts the stop watch.
	 * 
	 * @param key
	 *            - key for mapping 'start' and 'end'
	 */
	public static void start(String key) {
		startMap.put(key, System.nanoTime());
	}

	/**
	 * Ends the stop watch.
	 * 
	 * @param key
	 *            - key for mapping 'start' and 'end'
	 */
	public static void end(String key) {
		Unit unit = resultMap.get(key);
		if (unit == null)
			resultMap.put(key, unit = new Unit());
		long elapsed = System.nanoTime() - startMap.remove(key);
		unit.add(elapsed);
		if (out != null && oc != null && oc != OutputTiming.SHOW) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Performance meter] ").append(key).append(": ")
					.append(elapsed).append("[nano secs]");
			println(sb);
		}
	}

	private static void println(StringBuilder sb) {
		try {
			out.write(sb.append(CRLF).toString().getBytes());
		} catch (IOException ignore) {
		}
	}

	public static void show() {
		if (out != null && oc != null && oc != OutputTiming.END) {
			StringBuilder sb = new StringBuilder("[Performance meter] Results:")
					.append(CRLF);
			for (Map.Entry<String, Unit> e : resultMap.entrySet()) {
				sb.append(e.getKey()).append(e.getValue());
			}
			println(sb);
		}
	}

	private static class Unit {
		long sum_msec = 0;
		int time = 0;

		Unit add(long msec) {
			sum_msec += msec;
			time++;
			return this;
		}

		@Override
		public String toString() {
			double ave = sum_msec / time;
			return new StringBuilder().append("(").append(sum_msec)
					.append("[nano secs], ").append(time)
					.append("time, average:").append(ave)
					.append("[nano secs])").append(CRLF).toString();
		}
	}
}
