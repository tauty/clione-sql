package tetz42.util;

import java.util.ArrayList;
import java.util.HashMap;
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
}
