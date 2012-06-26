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

	private static final String APP_NAME = "[PerformanceMeter]";
	private static final String CRLF = System.getProperty("line.separator");
	private static final String DEFAULT_KEY = "****";

	private static volatile OutputStream out = null;

	static {
		try {
			ResourceBundle bundle = ResourceBundle
					.getBundle("performance_meter");
			if ("true".equals(bundle.getString("valid").toLowerCase())) {
				init(System.out);
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

	/**
	 * Initialization.
	 * @param os OutputStream
	 */
	public static void init(OutputStream os) {
		out = os;
	}

	/**
	 * Get stop watch object.
	 * @return stop watch object.
	 */
	public static StopWatch get() {
		return get(DEFAULT_KEY);
	}

	/**
	 * Get stop watch object.
	 * @param key key
	 * @return stop watch object.
	 */
	public static StopWatch get(String key) {
		return isInvalid() ? NULL_WATCH : new StopWatch(key);
	}

	/**
	 * Start the stop watch specified by the passed key.
	 * @param key key
	 * @return stop watch object started.
	 */
	public static StopWatch start(String key) {
		return registAndGet(key).start();
	}

	/**
	 * Start the stop watch.
	 * @return stop watch object started.
	 */
	public static StopWatch start() {
		return start(DEFAULT_KEY);
	}

	/**
	 * Start the stop watch specified by the passed key without warning.
	 * @param key key
	 * @return stop watch object started.
	 */
	public static StopWatch startQuiet(String key) {
		return registAndGet(key).startQuiet();
	}

	/**
	 * Start the stop watch without without warning.
	 * @return stop watch object started.
	 */
	public static StopWatch startQuiet() {
		return startQuiet(DEFAULT_KEY);
	}

	/**
	 * Stop the stop watch specified by the passed key.
	 * @param key key
	 */
	public static void end(String key) {
		registAndGet(key).end();
	}

	/**
	 * Stop the stop watch.
	 */
	public static void end() {
		end(DEFAULT_KEY);
	}

	/**
	 * Stop the stop watch specified by the passed key without warning.
	 * @param key key
	 */
	public static void endQuiet(String key) {
		registAndGet(key).endQuiet();
	}

	/**
	 * Stop the stop watch without without warning.
	 */
	public static void endQuiet() {
		endQuiet(DEFAULT_KEY);
	}

	/**
	 * Stop the stop watch specified by the passed key without audit log.
	 * @param key key
	 */
	public static void stop(String key) {
		registAndGet(key).stop();
	}

	/**
	 * Stop the stop watch without audit log.
	 */
	public static void stop() {
		stop(DEFAULT_KEY);
	}

	/**
	 * Stop the stop watch specified by the passed key without audit log and warning.
	 * @param key key
	 */
	public static void stopQuiet(String key) {
		registAndGet(key).stopQuiet();
	}

	/**
	 * Stop the stop watch without audit log and warning.
	 */
	public static void stopQuiet() {
		stopQuiet(DEFAULT_KEY);
	}

	/**
	 * Show the performance log.
	 */
	public static void show() {
		if (isOutputOK()) {
			StringBuilder sb = new StringBuilder("[Summary]").append(CRLF);
			for (Map.Entry<String, Summary> e : summaryMap.entrySet()) {
				sb.append("\t").append(e.getValue()).append(CRLF);
			}
			println(sb);
		}
	}

	private static StopWatch registAndGet(String key) {
		return isInvalid() ? NULL_WATCH : intervalMapLocal.get().get(key);
	}

	private static boolean isInvalid() {
		return !isOutputOK();
	}

	private static boolean isOutputOK() {
		return out != null;
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

	/**
	 * Stop Watch Object.
	 */
	public static class StopWatch {
		long start_time = Long.MIN_VALUE;
		long start_memory = Long.MIN_VALUE;
		final String key;

		private StopWatch(String key) {
			this.key = key;
		}

		/**
		 * Get the key.
		 * @return key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * Start.
		 * @return stop watch object
		 */
		public StopWatch start() {
			return start(false);
		}

		/**
		 * Start without warning.
		 * @return stop watch object
		 */
		public StopWatch startQuiet() {
			return start(true);
		}

		/**
		 * Stop without audit log.
		 */
		public void stop() {
			terminate(false, false);
		}

		/**
		 * Stop without audit log.
		 */
		public void stopQuiet() {
			terminate(true, false);
		}

		/**
		 * Stop.
		 */
		public void end() {
			terminate(false, true);
		}

		/**
		 * Stop without audit log.
		 */
		public void endQuiet() {
			terminate(false, true);
		}

		private StopWatch start(boolean isQuiet) {
			if (this.start_time != Long.MIN_VALUE) {
				if (!isQuiet)
					println("[WARNING] start() is called without calling end(). key = "
							+ this.key);
			}
			this.start_time = System.nanoTime();
			this.start_memory = usedMemory();
			return this;
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
			if (isOutputRequired && isOutputOK()) {
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
		public StopWatch start() {
			return this;
		}

		@Override
		public StopWatch startQuiet() {
			return this;
		}

		@Override
		public void stop() {
		}

		@Override
		public void stopQuiet() {
		}
	};

	/**
	 * Summary Object.
	 */
	public static class Summary {
		final String key;
		long sum_nano_secs = 0;
		int time = 0;

		/**
		 * Constructor
		 * @param key key.
		 */
		public Summary(String key) {
			this.key = key;
		}

		synchronized Summary add(long nano_sec) {
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
			StopWatch sw = super.get(key);
			if (sw == null) {
				String skey = String.valueOf(key);
				put(skey, sw = new StopWatch(skey));
			}
			return sw;
		}
	}

	@SuppressWarnings("serial")
	private static class ConcurrentSummaryMap extends
			ConcurrentHashMap<String, Summary> {

		@Override
		public Summary get(Object key) {
			Summary summary = super.get(key);
			if (summary == null) {
				String skey = String.valueOf(key);
				Summary putted = putIfAbsent(skey, summary = new Summary(skey));
				if (putted != null)
					summary = putted;
			}
			return summary;
		}
	}
}
