package tetz42.clione.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class IOUtil {

	public static byte[] loadFromStream(final InputStream in) {

		return new IOWrapper<byte[]>(in) {

			@Override
			protected byte[] execute() throws IOException {
				if (in == null)
					return null;
				final int SIZE = 100;
				ArrayList<byte[]> list = new ArrayList<byte[]>();
				int result = 0;
				while (true) {
					byte[] b = new byte[SIZE];
					result = in.read(b);
					if (result == -1)
						break;
					if (result < SIZE) {
						byte[] rest = new byte[result];
						System.arraycopy(b, 0, rest, 0, result);
						list.add(rest);
						break;
					}
					list.add(b);
				}
				if(list.size() == 0)
					return new byte[0];
				byte[] b_all = new byte[SIZE * (list.size() - 1)
						+ list.get(list.size() - 1).length];
				for (int i = 0; i < list.size(); i++) {
					System.arraycopy(list.get(i), 0, b_all, i * SIZE, list
							.get(i).length);
				}
				return b_all;
			}

		}.invoke();
	}

	public static Properties getProperties(String path, ClassLoader loader) {
		final InputStream in = loader.getResourceAsStream(path);
		if (in == null)
			return null;

		return new IOWrapper<Properties>(in) {

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
