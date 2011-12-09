package tetz42.util.tablequery;

import java.util.LinkedHashMap;

public class RecursiveMap<T> extends LinkedHashMap<String, RecursiveMap<T>> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private T value;

	private int depth;

	public int getDepth() {
		return depth;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue(String... keys) {
		return get(keys).value;
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
			put(key, map = new RecursiveMap<T>());
		return map;
	}

	@Override
	public RecursiveMap<T> get(Object key) {
		return get(String.valueOf(key));
	}
}