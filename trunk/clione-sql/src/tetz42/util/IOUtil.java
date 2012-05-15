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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import tetz42.util.exception.WrapException;

public class IOUtil {

	public static String toString(final InputStream in, String encoding) {
		try {
			return new String(toByteArray(in), encoding);
		} catch (UnsupportedEncodingException e) {
			throw new WrapException(e);
		}
	}

	public static String toString(final InputStream in) {
		return toString(in, "utf-8");
	}

	public static byte[] toByteArray(final InputStream in) {
		return in2out(in, new ByteArrayOutputStream(0xFFFF)).toByteArray();
	}

	public static <T extends OutputStream> T in2out(final InputStream in,
			final T out) {
		return new Using<T>(in, out) {

			@Override
			protected T execute() throws Exception {
				byte[] buf = new byte[0xFFF];
				int len;
				while (-1 != (len = in.read(buf)))
					out.write(buf, 0, len);
				return out;
			}
		}.invoke();
	}

	public static Properties getProperties(String path, ClassLoader loader) {
		final InputStream in = loader.getResourceAsStream(path);
		if (in == null)
			return null;

		return new Using<Properties>(in) {

			@Override
			protected Properties execute() throws IOException {
				Properties prop = new Properties();
				prop.load(in);
				return prop;
			}
		}.invoke();
	}

	public static Properties getProperties(String path) {
		return getProperties(path, Thread.currentThread()
				.getContextClassLoader());
	}

}
