package tetz42.clione.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import tetz42.clione.exception.WrapException;

public class IOUtil {

	public static byte[] loadFromStream(InputStream in) {
		try {
			try {
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
				byte[] b_all = new byte[SIZE * (list.size() - 1)
						+ list.get(list.size() - 1).length];
				for (int i = 0; i < list.size(); i++) {
					System.arraycopy(list.get(i), 0, b_all, i * SIZE,
							list.get(i).length);
				}
				return b_all;
			} finally {
				if (in != null)
					in.close();
			}
		} catch (IOException e) {
			throw new WrapException(e);
		}
	}

}
