package tetz42.util.tablequery;

import java.util.LinkedHashMap;

import tetz42.util.exception.WrapException;

public class CellUnitMap<T> {

	private final Class<T> clazz;
	private final LinkedHashMap<String, T> valueMap = new LinkedHashMap<String, T>();

	public CellUnitMap(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T get(String key) {
		if (valueMap.containsKey(key)) {
			try {
				valueMap.put(key, clazz.newInstance());
			} catch (Exception e) {
				throw new WrapException("The class, " + clazz.getName()
						+ ", must have public default constructor.", e);
			}
		}
		return valueMap.get(key);
	}

	public int size() {
		return valueMap.size();
	}
}
