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
package tetz42.clione.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;
import java.util.Properties;

import tetz42.clione.common.exception.WrapException;

/**
 * InputStream and OutputStream Utility class.
 *
 * @author tetsuo.ohta
 */
public class IOUtil {

	/**
	 * Generates string from input stream.
	 *
	 * @param in
	 *            input stream
	 * @param charset
	 *            character set
	 * @return string generated
	 */
	public static String toString(final InputStream in, String charset) {
		try {
			return new String(toByteArray(in), charset);
		} catch (UnsupportedEncodingException e) {
			throw new WrapException(e);
		}
	}

	/**
	 * Generates String from input stream.
	 *
	 * @param in
	 *            - input stream
	 * @return string generated
	 */
	public static String toString(final InputStream in) {
		return toString(in, "utf-8");
	}

	/**
	 * Generates byte array from input stream.
	 *
	 * @param in
	 *            input stream
	 * @return byte array generated
	 */
	public static byte[] toByteArray(final InputStream in) {
		return in2out(in, new ByteArrayOutputStream(0xFFFF)).toByteArray();
	}

	/**
	 * Copy the contents of input stream to output stream.
	 *
	 * @param <T>
	 *            sub type of output stream
	 * @param in
	 *            input stream
	 * @param out
	 *            output stream
	 * @return the output stream passed as a parameter
	 */
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

	/**
	 * Generates Property object.
	 *
	 * @param path
	 *            the path of the property file
	 * @param loader
	 *            class loader
	 * @return Properties object
	 */
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

	/**
	 * Generates Property object. <br>
	 * If the key is not specified, MissingResourceException will be thrown.
	 *
	 * @param path
	 *            the path of the property file
	 * @param loader
	 *            class loader
	 * @return Properties object
	 */
	public static Properties getPropertiesRB(final String path,
			ClassLoader loader) {
		final InputStream in = loader.getResourceAsStream(path);
		if (in == null)
			return null;

		return new Using<Properties>(in) {

			@Override
			protected Properties execute() throws IOException {
				Properties prop = new Properties() {
					/** */
					private static final long serialVersionUID = 1L;

					/**
					 * @param key
					 *            key
					 * @return the value obtained by the key
					 * @throws MissingResourceException
					 *             if the key is not specified.
					 */
					@Override
					public String getProperty(String key) {
						String value = super.getProperty(key);
						if (value == null)
							throw new MissingResourceException(
									"No object for the given key, '" + key
											+ "', can be found in '" + path
											+ "'.", getClass().getName(), key);
						return value;
					}
				};
				prop.load(in);
				return prop;
			}
		}.invoke();
	}

	/**
	 * Generates Property object.
	 *
	 * @param path
	 *            the path of the property file
	 * @return Properties object
	 */
	public static Properties getProperties(String path) {
		return getProperties(path, Thread.currentThread()
				.getContextClassLoader());
	}

	/**
	 * Generates Property object. <br>
	 * If the key is not specified, MissingResourceException will be thrown.
	 *
	 * @param path
	 *            the path of the property file
	 * @return Properties object
	 */
	public static Properties getPropertiesRB(String path) {
		return getPropertiesRB(path, Thread.currentThread()
				.getContextClassLoader());
	}

	/**
	 * Generates Property object.
	 *
	 * @param path
	 *            the path of the property file
	 * @param ext
	 *            the extension folder.
	 * @return Properties object
	 */
	public static Properties getProperties(String path, String ext) {
		Properties prop = IOUtil.getProperties(path);
		Properties extProp = IOUtil.getProperties(ext + "/" + path);
		if (extProp != null) {
			prop.putAll(extProp);
		}
		return prop;
	}

	/**
	 * Generates Property object. <br>
	 * If the key is not specified, MissingResourceException will be thrown.
	 *
	 * @param path
	 *            the path of the property file
	 * @param ext
	 *            the extension folder.
	 * @return Properties object
	 */
	public static Properties getPropertiesRB(String path, String ext) {
		Properties prop = IOUtil.getPropertiesRB(path);
		Properties extProp = IOUtil.getPropertiesRB(ext + "/" + path);
		if (extProp != null) {
			prop.putAll(extProp);
		}
		return prop;
	}

	/**
	 * Load resource from default package.
	 *
	 * @param path
	 *            resource name
	 * @return Input stream
	 */
	public static InputStream loadFromRoot(String path) {
		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(path);
	}
}
