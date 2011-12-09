package tetz42.util.tablequery;

import java.util.HashMap;

import tetz42.util.exception.InvalidParameterException;

public class RemovedMap extends HashMap<String, RemovedMap> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final RemovedMap REMOVED_MARK = new RemovedMap();

	public boolean isRemoved(String... keys) {
		RemovedMap map = this;
		for (String key : keys) {
			if (map == REMOVED_MARK) {
				return true;
			}
			if (!map.containsKey(key)) {
				return false;
			}
			map = map.get(key);
		}
		return map == REMOVED_MARK;
	}

	public void mark(String... keys) {
		if (keys.length == 0)
			throw new InvalidParameterException(
					"No parameter detected. Must be passed 1 more parameters.");
		RemovedMap map = this;
		for (int i = 0; i < keys.length - 1; i++) {
			String key = keys[i];
			if (map.containsKey(key)) {
				map = map.get(key);
			} else {
				map.put(key, new RemovedMap());
				map = map.get(key);
			}
		}
		String lastKey = keys[keys.length - 1];
		map.put(lastKey, REMOVED_MARK);
	}

}
