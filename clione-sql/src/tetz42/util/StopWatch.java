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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class StopWatch {

	public static enum OutputTiming {
		ALL, END, SHOW
	}

	private static final String APP_NAME = "[StopWatch]";
	private static final String CRLF = System.getProperty("line.separator");
	private static final String DEFAULT_KEY = "no-specified-key";

	private static volatile OutputStream out = null;
	private static volatile OutputTiming oc = null;

	static {
	}

	public void test() {
		StopWatch.init(System.out);

		StopWatch.start("test");
		// do process
		StopWatch.start("inside-test");
		// do process
		StopWatch.end("inside-end");
		// do process
		StopWatch.end("test");

		StopWatch.show();
	}

	public static void init(OutputStream os) {
		init(os, OutputTiming.ALL);
	}

	public static void init(OutputStream os, OutputTiming o) {
		out = os;
		oc = o;
	}

	private static final ThreadLocal<Map<String, Interval>> intervalMapLocal = new ThreadLocal<Map<String, Interval>>() {

		@Override
		protected Map<String, Interval> initialValue() {
			return new ReadyMap();
		}
	};

	@SuppressWarnings("serial")
	private static final Map<String, Interval> intervalMapGlobal = new ConcurrentReadyMap<Interval>() {

		@Override
		protected Interval newValue(String key) {
			return new Interval(key);
		}
	};

	@SuppressWarnings("serial")
	private static final Map<String, Summary> summaryMap = new ConcurrentReadyMap<Summary>() {

		@Override
		protected Summary newValue(String key) {
			return new Summary(key);
		}
	};

	public static Interval startLocal(String key) {
		return isInvalid() ? null : intervalMapLocal.get().get(key).start();
	}

	public static Interval endLocal(String key) {
		return isInvalid() ? null : intervalMapLocal.get().get(key).end();
	}

	public static Interval startLocal() {
		return startLocal(DEFAULT_KEY);
	}

	public static Interval endLocal() {
		return endLocal(DEFAULT_KEY);
	}

	public static Interval startGlobal(String key) {
		return isInvalid() ? null : intervalMapGlobal.get(key).start();
	}

	public static Interval endGlobal(String key) {
		return isInvalid() ? null : intervalMapGlobal.get(key).end();
	}

	public static Interval startGlobal() {
		return startGlobal(DEFAULT_KEY);
	}

	public static Interval endGlobal() {
		return endGlobal(DEFAULT_KEY);
	}

	public static Interval start(String key) {
		return startLocal(key);
	}

	public static Interval end(String key) {
		return endLocal(key);
	}

	public static Interval start() {
		return start(DEFAULT_KEY);
	}

	public static Interval end() {
		return end(DEFAULT_KEY);
	}

	private static boolean isInvalid() {
		return !isOutputOK(OutputTiming.ALL);
	}

	private static boolean isOutputOK(OutputTiming timing) {
		return out != null && oc != null
				&& (oc == OutputTiming.ALL || oc == timing);
	}

	private static void println(StringBuilder sb) {
		try {
			out.write((APP_NAME + sb.append(CRLF)).getBytes());
		} catch (IOException ignore) {
		}
	}

	private static void println(String s) {
		try {
			out.write((APP_NAME + s + CRLF).getBytes());
		} catch (IOException ignore) {
		}
	}

	public static void show() {
		if (isOutputOK(OutputTiming.SHOW)) {
			StringBuilder sb = new StringBuilder("[Result]").append(CRLF);
			for (Map.Entry<String, Summary> e : summaryMap.entrySet()) {
				sb.append("\t").append(e.getValue()).append(CRLF);
			}
			println(sb);
		}
	}

	public static class Interval {
		long start_time = Long.MIN_VALUE;
		final String key;

		private Interval(String key) {
			this.key = key;
		}

		public Interval start() {
			if (this.start_time != Long.MIN_VALUE) {
				println("[WARNING] start() is called without calling end().");
			}
			this.start_time = System.nanoTime();
			return this;
		}

		public Interval end() {
			if (this.start_time == Long.MIN_VALUE) {
				println("[WARNING] end() is called without calling start().");
			}
			long elapsed = System.nanoTime() - this.start_time;
			this.start_time = Long.MIN_VALUE;
			summaryMap.get(key).add(elapsed);
			if (isOutputOK(OutputTiming.END)) {
				println("[" + key + "] Elapsed time is " + elapsed
						+ "(nano secs).");
			}
			return this;
		}
	}

	private static class Summary {
		final String key;
		long sum_nano_secs = 0;
		int time = 0;

		public Summary(String key) {
			this.key = key;
		}

		Summary add(long nano_sec) {
			sum_nano_secs += nano_sec;
			time++;
			return this;
		}

		@Override
		public String toString() {
			double ave = sum_nano_secs / time;
			return new StringBuilder("[Summary] ").append(key).append(" - ")
					.append(sum_nano_secs).append("(nano secs), ").append(time)
					.append("time, average:").append(ave).append("(nano secs)")
					.append(CRLF).toString();
		}
	}

	@SuppressWarnings("serial")
	private static class ReadyMap extends HashMap<String, Interval> {

		@Override
		public Interval get(Object key) {
			Interval interval = super.get(key);
			String skey = String.valueOf(key);
			if (interval == null)
				this.put(skey, interval = new Interval(skey));
			return interval;
		}
	}

	@SuppressWarnings("serial")
	private abstract static class ConcurrentReadyMap<V> extends
			ConcurrentHashMap<String, V> {

		@Override
		public V get(Object key) {
			V interval = super.get(key);
			if (interval == null) {
				String skey = String.valueOf(key);
				V putted = putIfAbsent(skey, interval = newValue(skey));
				if (putted != null)
					interval = putted;
			}
			return interval;
		}

		protected abstract V newValue(String key);
	}
}
