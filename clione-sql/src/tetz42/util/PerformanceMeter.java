/*
 * Copyright 2012 tetsuo.ohta[at]gmail.com
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
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides easy way to measurement performance.
 * <p>
 *
 * @version 1.0
 * @author tetz
 */
public class PerformanceMeter {

	public static enum OutputTiming {
		ALL, END, SHOW
	}

	private static final String APP_NAME = "[PerformanceMeter]";
	private static final String CRLF = System.getProperty("line.separator");
	private static final String DEFAULT_KEY = "****";

	private static volatile OutputStream out = null;
	private static volatile OutputTiming oc = null;

	static {
		try {
			ResourceBundle bundle = ResourceBundle
					.getBundle("performance_meter");
			if ("true".equals(bundle.getString("valid").toLowerCase())) {
				init(System.out, OutputTiming.ALL);
			}
		} catch (MissingResourceException ignore) {
		}
	}

	private static final ThreadLocal<StopWatchMap> intervalMapLocal = new ThreadLocal<StopWatchMap>() {

		@Override
		protected StopWatchMap initialValue() {
			return new StopWatchMap();
		}
	};

	private static final ConcurrentSummaryMap summaryMap = new ConcurrentSummaryMap();

	public static void init(OutputStream os) {
		init(os, OutputTiming.ALL);
	}

	public static void init(OutputStream os, OutputTiming o) {
		out = os;
		oc = o;
	}

	public static StopWatch get() {
		return registAndGet(DEFAULT_KEY);
	}

	public static StopWatch get(String key) {
		return isInvalid() ? NULL_WATCH : new StopWatch(key);
	}

	public static StopWatch registAndGet(String key) {
		return isInvalid() ? NULL_WATCH : intervalMapLocal.get().get(key);
	}

	public static void start(String key) {
		registAndGet(key).start();
	}

	public static void start() {
		start(DEFAULT_KEY);
	}

	public static void startQuiet(String key) {
		registAndGet(key).startQuiet();
	}

	public static void startQuiet() {
		startQuiet(DEFAULT_KEY);
	}

	public static void end(String key) {
		registAndGet(key).end();
	}

	public static void end() {
		end(DEFAULT_KEY);
	}

	public static void endQuiet(String key) {
		registAndGet(key).endQuiet();
	}

	public static void endQuiet() {
		endQuiet(DEFAULT_KEY);
	}

	public static void stop(String key) {
		registAndGet(key).stop();
	}

	public static void stop() {
		stop(DEFAULT_KEY);
	}

	public static void stopQuiet(String key) {
		registAndGet(key).stopQuiet();
	}

	public static void stopQuiet() {
		stopQuiet(DEFAULT_KEY);
	}

	public static void show() {
		if (isOutputOK(OutputTiming.SHOW)) {
			StringBuilder sb = new StringBuilder("[Summary]").append(CRLF);
			for (Map.Entry<String, Summary> e : summaryMap.entrySet()) {
				sb.append("\t").append(e.getValue()).append(CRLF);
			}
			println(sb);
		}
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

	public static class StopWatch {
		long start_time = Long.MIN_VALUE;
		long start_memory = Long.MIN_VALUE;
		final String key;

		private StopWatch(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public void start() {
			start(false);
		}

		public void startQuiet() {
			start(true);
		}

		private void start(boolean isQuiet) {
			if (this.start_time != Long.MIN_VALUE) {
				if (!isQuiet)
					println("[WARNING] start() is called without calling end(). key = "
							+ this.key);
			}
			this.start_time = System.nanoTime();
			this.start_memory = usedMemory();
		}

		public void stop() {
			terminate(false, false);
		}

		public void stopQuiet() {
			terminate(true, false);
		}

		public void end() {
			terminate(false, true);
		}

		public void endQuiet() {
			terminate(false, true);
		}

		private void terminate(boolean isQuiet, boolean isOutputRequired) {
			if (this.start_time == Long.MIN_VALUE) {
				if (!isQuiet)
					println("[WARNING] end() is called without calling start(). key = "
							+ this.key);
			}
			long elapsed = System.nanoTime() - this.start_time;
			this.start_time = Long.MIN_VALUE;
			summaryMap.get(key).add(elapsed);
			if (isOutputRequired && isOutputOK(OutputTiming.END)) {
				println("[" + key + "] Elapsed time is "
						+ ((double) elapsed / 1000000)
						+ "(ms), Used memory is "
						+ ((double) (usedMemory() - start_memory) / 1000)
						+ "(KB).");
			}
		}

		private long usedMemory() {
			return Runtime.getRuntime().totalMemory()
					- Runtime.getRuntime().freeMemory();
		}
	}

	private static final StopWatch NULL_WATCH = new StopWatch(DEFAULT_KEY) {

		@Override
		public void end() {
		}

		@Override
		public void endQuiet() {
		}

		@Override
		public void start() {
		}

		@Override
		public void startQuiet() {
		}

		@Override
		public void stop() {
		}

		@Override
		public void stopQuiet() {
		}
	};

	public static class Summary {
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
			return new StringBuilder("[").append(key).append("]: ").append(
					(double) sum_nano_secs / 1000000)
					.append("(ms), performed ").append(time).append(
							" times, average ").append(ave / 1000000).append(
							"(ms)").toString();
		}
	}

	@SuppressWarnings("serial")
	private static class StopWatchMap extends HashMap<String, StopWatch> {

		@Override
		public StopWatch get(Object key) {
			StopWatch interval = super.get(key);
			String skey = String.valueOf(key);
			if (interval == null)
				this.put(skey, interval = new StopWatch(skey));
			return interval;
		}
	}

	@SuppressWarnings("serial")
	private static class ConcurrentSummaryMap extends
			ConcurrentHashMap<String, Summary> {

		@Override
		public Summary get(Object key) {
			Summary interval = super.get(key);
			if (interval == null) {
				String skey = String.valueOf(key);
				Summary putted = putIfAbsent(skey, interval = new Summary(skey));
				if (putted != null)
					interval = putted;
			}
			return interval;
		}

	}
}
