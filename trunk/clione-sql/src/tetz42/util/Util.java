package tetz42.util;

import static tetz42.util.Const.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Util {

	public static final <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	public static final <K, V> ConcurrentHashMap<K, V> newConcurrentMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static final <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean isEmpty(Object o) {
		return o == null ? true : isEmpty(String.valueOf(o));
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	public static boolean isAllEmpty(Object... objs) {
		for (Object o : objs) {
			if (isNotEmpty(o))
				return false;
		}
		return true;
	}

	public static boolean containsEmpty(Object... objs) {
		for (Object o : objs) {
			if (isEmpty(o))
				return true;
		}
		return false;
	}

	public static boolean notContainsEmpty(Object... objs) {
		return !containsEmpty(objs);
	}

	public static boolean contains(Object key, Object... values) {
		for (Object value : values) {
			if (key.equals(value))
				return true;
		}
		return false;
	}

	public static <T> T nvl(T... objs) {
		for (T obj : objs) {
			if (obj != null)
				return obj;
		}
		return null;
	}

	public static <T> T evl(T... objs) {
		for (T obj : objs) {
			if (isNotEmpty(obj))
				return obj;
		}
		return null;
	}

	public static String mkString(Object... objs) {
		return mkStringBy("", objs);
	}

	public static String mkStringByComma(Object... objs) {
		return mkStringBy(", ", objs);
	}

	public static String mkStringByCRLF(Object... objs) {
		return mkStringBy(CRLF, objs);
	}

	public static String mkStringBy(String delimiter, Object... objs) {
		if (objs == null || objs.length == 0)
			return "";
		StringBuilder sb = new StringBuilder().append(objs[0]);
		for (int i = 1; i < objs.length; i++)
			sb.append(delimiter).append(objs[i]);
		return sb.toString();
	}

	public static String mkString(List<?> objs) {
		return mkStringBy("", objs);
	}

	public static String mkStringByComma(List<?> objs) {
		return mkStringBy(", ", objs);
	}

	public static String mkStringByCRLF(List<?> objs) {
		return mkStringBy(CRLF, objs);
	}

	public static String mkStringBy(String delimiter, List<?> objs) {
		if (objs == null || objs.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder().append(objs.get(0));
		for (int i = 1; i < objs.size(); i++)
			sb.append(delimiter).append(objs.get(i));
		return sb.toString();
	}
}
