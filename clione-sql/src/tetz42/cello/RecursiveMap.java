package tetz42.cello;

import java.util.LinkedHashMap;
import java.util.List;

public class RecursiveMap<T> extends LinkedHashMap<String, RecursiveMap<T>> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private T value;

	private final String[] keys;

	public RecursiveMap() {
		this.keys = new String[] {};
	}

	private RecursiveMap(String[] paramKeys, String newKey) {
		String[] keys = new String[paramKeys.length + 1];
		System.arraycopy(paramKeys, 0, keys, 0, paramKeys.length);
		setLast(keys, newKey);
		this.keys = keys;
	}

	private static <X> void setLast(X[] xs, X e) {
		xs[lastIndex(xs)] = e;
	}

	private static <X> int lastIndex(X[] xs) {
		return xs.length - 1;
	}

	public String[] keys() {
		return keys;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue(List<String> keys) {
		return get(keys).value;
	}

	public T getValue(String... keys) {
		return get(keys).value;
	}

	public RecursiveMap<T> get(List<String> keys) {
		RecursiveMap<T> map = this;
		for (String key : keys)
			map = map.get(key);
		return map;
	}

	public RecursiveMap<T> get(String... keys) {
		RecursiveMap<T> map = this;
		for (String key : keys)
			map = map.get(key);
		return map;
	}

	public RecursiveMap<T> get(String key) {
		RecursiveMap<T> map = super.get(key);
		if (map == null)
			put(key, map = new RecursiveMap<T>(this.keys, key));
		return map;
	}

	@Override
	public RecursiveMap<T> get(Object key) {
		return get(String.valueOf(key));
	}
}