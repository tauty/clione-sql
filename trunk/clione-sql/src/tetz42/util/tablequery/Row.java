package tetz42.util.tablequery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import tetz42.util.tablequery.tables.TableObject1.ContextValues;

public class Row {

	private final ContextValues context;
	private final LinkedHashMap<Object, Column<Object>> map = new LinkedHashMap<Object, Column<Object>>();

	private boolean isRemoved = false;

	public Row(ContextValues context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public <T> Column<T> get(Class<T> clazz, String key) {
		if (!map.containsKey(key)) {
			map.put(key, (Column<Object>) new Column<T>(clazz, key, context));
		}
		return (Column<T>) map.get(key);
	}

	public LinkedHashMap<Object, Column<Object>> getMap() {
		return map;
	}

	@SuppressWarnings("unchecked")
	public <T> List<Column<T>> columnList(Class<T> clazz) {
		ArrayList<Column<T>> list = new ArrayList<Column<T>>();
		for (Column<Object> col : map.values()) {
			if (clazz.isInstance(col.get()))
				list.add((Column<T>) col);
		}
		return list;
	}

	public Iterable<Column<String>> each() {
		return each(1, true);
	}

	public void remove() {
		this.isRemoved = true;
	}

	public boolean isRemoved() {
		return isRemoved;
	}

	public Iterable<Column<String>> each(final int level, final boolean isAll) {
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
						// TODO temporary implementation. fix below.
						colIte = e.getValue().each(level + 1, isAll).iterator();
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
