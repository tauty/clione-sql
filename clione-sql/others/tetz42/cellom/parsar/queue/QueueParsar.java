package tetz42.cellom.parsar.queue;

import static tetz42.util.ReflectionUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tetz42.cellom.parsar.queue.annotation.QueueCell;
import tetz42.util.Using;

public class QueueParsar {

	public static <T> List<T> parseAll(Class<T> clazz, InputStream in) {
		return parseAll(clazz, in, "UTF-8");
	}

	public static <T> List<T> parseAll(Class<T> clazz, InputStream in,
			String charasetName) {
		return parseAll(clazz, in, "\r\n", charasetName);
	}

	public static <T> List<T> parseAll(final Class<T> clazz,
			final InputStream in, final String recordDelim,
			final String charasetName) {
		return new Using<List<T>>(in) {
			@Override
			protected List<T> execute() throws Exception {
				ArrayList<T> list = new ArrayList<T>();
				Token token = new IStreamToken(in, charasetName);
				T t;
				while (null != (t = parse(clazz, token))) {
					list.add(t);
					String rd;
					while (null != (rd = token.next(recordDelim.length()))) {
						if (!recordDelim.equals(rd)) {
							token.back();
							break;
						}
					}
				}
				return list;
			}
		}.invoke();
	}

	public static <T> T parse(Class<T> clazz, InputStream in) {
		return parse(clazz, in, "UTF-8");
	}

	public static <T> T parse(final Class<T> clazz, final InputStream in,
			final String charasetName) {
		return new Using<T>(in) {
			@Override
			protected T execute() throws Exception {
				return parse(clazz, new IStreamToken(in, charasetName));
			}
		}.invoke();
	}

	public static <T> T parse(final Class<T> clazz, final String src) {
		return new Using<T>() {
			@Override
			protected T execute() throws Exception {
				return parse(clazz, new StrToken(src));
			}
		}.invoke();
	}

	private static <T> T parse(Class<T> clazz, Token token) throws IOException {
		List<Field> fList = avoidDuplication(getAllFields(clazz));
		Collections.sort(fList, new Comparator<Field>() {
			@Override
			public int compare(Field f1, Field f2) {
				QueueCell q1 = getAnnotation(f1, QueueCell.class);
				QueueCell q2 = getAnnotation(f2, QueueCell.class);
				if (q1 == null && q2 == null)
					return 0;
				else if (q1 == null || q2 == null)
					return q1 == null ? 1 : -1; // null last
				return q1.order() - q2.order();
			}
		});
		T res = newInstance(clazz);
		for (Field f : fList) {
			QueueCell q = getAnnotation(f, QueueCell.class);
			if (q == null)
				break;
			if (isPrimitive(f.getType())) {
				String s = token.next(q.size());
				if (s == null)
					return null;
				setStringValue(res, f, s);
			} else {
				Object child = parse(f.getType(), token);
				if (child == null)
					return null;
				setValue(res, f, child);
			}
		}
		return res;
	}

	private static abstract class Token {
		private String previous = null;
		private boolean backed = false;

		final String next(int size) throws IOException {
			String res;
			if (this.backed && this.previous != null) {
				if (this.previous.length() <= size) {
					this.backed = false;
					String s = this.nextTask(size - this.previous.length());
					res = this.previous = this.previous + s;
				} else {
					res = this.previous.substring(0, size);
					this.previous = this.previous.substring(size);
				}
			} else {
				res = this.previous = this.nextTask(size);
			}
			System.out.println("next#res = " + res);
			return res;
		}

		abstract String nextTask(int size) throws IOException;

		void back() {
			this.backed = true;
		}
	}

	private static class IStreamToken extends Token {
		private InputStream in;
		private String charsetName;
		private byte[] buf = new byte[0xFF];

		IStreamToken(InputStream in, String charasetName) {
			this.in = in;
			this.charsetName = charasetName;
		}

		@Override
		String nextTask(int size) throws IOException {
			if (buf.length < size)
				buf = new byte[size];
			if (in.read(buf, 0, size) != size)
				return null;
			String res = new String(buf, 0, size, charsetName);
			return res;
		}
	}

	private static class StrToken extends Token {
		private byte[] src;
		private int pos;

		StrToken(String src) {
			this.src = src.getBytes();
			this.pos = 0;
		}

		@Override
		String nextTask(int size) {
			if (src.length < pos + size)
				return null;
			String res = new String(src, pos, size);
			pos += size;
			return res;
		}
	}

}
