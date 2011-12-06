package tetz42.util.tableobject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Row {
	private LinkedHashMap<Object, Column<Object>> map = new LinkedHashMap<Object, Column<Object>>();

	@SuppressWarnings("unchecked")
	public <T> Column<T> get(Class<T> clazz, String key) {
		if (!map.containsKey(key)) {
			map.put(key, (Column<Object>) new Column<T>(clazz, key));
		}
		return (Column<T>) map.get(key);
	}

	public LinkedHashMap<Object, Column<Object>> getMap() {
		return map;
	}

	public Iterable<Column<String>> each() {
		return each(null, 2, 2, true);
	}

	public Iterable<Column<String>> each(final Map<String, String> aliasMap, final int wholeLevel,
			final int curLevel, final boolean isAll) {
		return new Iterable<Column<String>>() {

			@Override
			public Iterator<Column<String>> iterator() {
				return new Iterator<Column<String>>() {

					private final Iterator<Entry<Object, Column<Object>>> iterator = map
							.entrySet().iterator();
					private Iterator<Column<String>> colIte;

					@Override
					public boolean hasNext() {
						boolean hasColNext = colIte == null ? false : colIte
								.hasNext();
						return iterator.hasNext() || hasColNext;
					}

					@Override
					public Column<String> next() {
						if (colIte != null && colIte.hasNext())
							return colIte.next();
						Entry<Object, Column<Object>> e = iterator.next();
						colIte = e.getValue().each(aliasMap, wholeLevel, curLevel, isAll)
								.iterator();
						return colIte.next();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException(
								"'remove' is not supported.");
					}
				};
			}
		};
	}
}
