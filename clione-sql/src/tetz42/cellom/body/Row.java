package tetz42.cellom.body;

import static tetz42.cellom.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import tetz42.cellom.Context;
import tetz42.cellom.ICell;
import tetz42.cellom.IRow;
import tetz42.cellom.Query;
import tetz42.cellom.RecursiveMap;
import tetz42.cellom.annotation.EachBody;
import tetz42.cellom.annotation.EachHeader;
import tetz42.cellom.header.HeaderCell;
import tetz42.util.exception.InvalidParameterException;
import tetz42.util.exception.WrapException;

public class Row<T> implements IRow {

	private final RecursiveMap<List<Cell<Object>>> cellMap;
	private final Context<T> context;
	private final T value;
	private boolean isRemoved = false;

	public Row(Class<T> clazz, Context<T> context) {
		this.cellMap = new RecursiveMap<List<Cell<Object>>>();
		this.context = context;
		this.value = newInstance(clazz);
		cellSetting();
	}

	public T get() {
		return value;
	}

	private void cellSetting() {
		Field f;
		try {
			f = this.getClass().getDeclaredField("value");
		} catch (Exception e) {
			throw new WrapException(e);
		}
		genCellRecursively(this, f, cellMap);
	}

	private void genCellRecursively(Object receiver, Field field,
			RecursiveMap<List<Cell<Object>>> cellMap) {

		// generate cell
		List<Cell<Object>> list = getListOnMap(cellMap);
		Cell<Object> cell = new Cell<Object>(receiver, field);
		list.add(cell);

		Object value = cell.get();
		if (value instanceof CelloMap<?>) {
			CelloMap<?> cumap = (CelloMap<?>) value;
			cumap.init(context, cellMap.keys(), this, field
					.getAnnotation(EachHeader.class), field
					.getAnnotation(EachBody.class));
			return;
		}

		if (isPrimitive(value))
			return;

		// generate field cell
		for (Field f : getFields(value.getClass())) {
			if (context.isValid(f)) {
				genCellRecursively(value, f, cellMap.get(f.getName()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	void genCell(CelloMap<?> cumap, String key, String... ownKeys) {
		RecursiveMap<List<Cell<Object>>> cellMap = this.cellMap.get(ownKeys);
		cellMap = cellMap.get(key);

		// generate cell
		List<Cell<Object>> list = getListOnMap(cellMap);
		Cell<Object> cell = new CellForMap<Object>((CelloMap<Object>) cumap,
				key);
		list.add(cell);

		Object value = cell.get();

		if (isPrimitive(value))
			return;

		// generate field cell
		for (Field f : getFields(value.getClass())) {
			if (context.isValid(f)) {
				genCellRecursively(value, f, cellMap.get(f.getName()));
			}
		}
	}

	private List<Cell<Object>> getListOnMap(
			RecursiveMap<List<Cell<Object>>> cellMap) {
		if (cellMap.getValue() == null) {
			cellMap.setValue(new ArrayList<Cell<Object>>());
		}
		return cellMap.getValue();
	}

	@Override
	public List<ICell> each() {
		List<ICell> list = new ArrayList<ICell>();
		each(this.cellMap, list);
		return list;
	}

	private void each(RecursiveMap<List<Cell<Object>>> cellMap, List<ICell> list) {
		if (isRemovedCell(cellMap))
			return;

		Cell<Object> cell = head(cellMap.getValue());
		Object value = cell.get();
		if (isPrimitive(value)) {
			list.add(cell);
		} else {
			Iterable<String> keys;
			if (value instanceof CelloMap<?>) {
				CelloMap<?> cmap = ((CelloMap<?>) cell.get());
				cmap.setAllDefinedKeys();
				keys = cmap.getFaithKeys();
			} else {
				keys = cellMap.keySet();
			}
			for (String key : keys) {
				each(cellMap.get(key), list);
			}
			// if (value instanceof CelloMap<?>)
			// ((CelloMap<?>) cell.get()).setAllDefinedKeys();
			// for (Entry<String, RecursiveMap<List<Cell<Object>>>> e : cellMap
			// .entrySet()) {
			// each(e.getValue(), list);
			// }
		}
	}

	private boolean isRemovedCell(RecursiveMap<List<Cell<Object>>> cellMap) {
		if (!this.context.getHeader().containsHeaderCellMap(cellMap.keys()))
			return true;
		RecursiveMap<List<HeaderCell>> hCellMap = this.context.getHeader()
				.getHeaderCellMap(cellMap.keys());
		HeaderCell hCell = head(hCellMap.getValue());
		return hCell.isRemoved();
	}

	public <E> List<Cell<E>> getByQuery(
			@SuppressWarnings("unused") Class<E> clazz, String query) {
		return getByQuery(query);
	}

	public <E> List<Cell<E>> getByQuery(String query) {
		return getByQuery(Query.parse(query));
	}

	public <E> List<Cell<E>> getByQuery(Query query) {
		return getByQuery(query, 1, this.cellMap, new ArrayList<Cell<E>>());
	}

	public <E> List<Cell<E>> getByQuery(
			@SuppressWarnings("unused") Class<E> clazz, Query query) {
		return getByQuery(query);
	}

	private <E> List<Cell<E>> getByQuery(Query query, int index,
			RecursiveMap<List<Cell<Object>>> map, List<Cell<E>> list) {
		Cell<Object> cell = head(map.getValue());
		if (query.get(index) == null) {
			addToList(list, cell);
		} else {
			String[] fieldNames = query.get(index);
			for (String fieldName : fieldNames) {
				doField(query, index, map, list, fieldName, cell);
			}
		}
		return list;
	}

	private <E> void doField(Query query, int index,
			RecursiveMap<List<Cell<Object>>> map, List<Cell<E>> list,
			String fieldName, Cell<Object> cell) {
		if (fieldName.equals(Query.ANY)) {
			if (cell.get() instanceof CelloMap<?>)
				((CelloMap<?>) cell.get()).setAllDefinedKeys();
			for (Entry<String, RecursiveMap<List<Cell<Object>>>> e : map
					.entrySet()) {
				if (e.getKey() != null) {
					getByQuery(query, index + 1, e.getValue(), list);
				}
			}
		} else if (fieldName.startsWith(Query.TERMINATE)) {
			fieldName = fieldName.substring(1);
			if (cell.get() instanceof CelloMap<?>)
				((CelloMap<?>) cell.get()).get(fieldName);
			if (map.containsKey(fieldName)) {
				Cell<Object> subCell = head(map.get(fieldName).getValue());
				addToList(list, subCell);
			} else {
				throw genE(map.keys(), fieldName);
			}
		} else {
			Matcher m = Query.numPtn.matcher(fieldName);
			if (m.matches()) {
				if (cell.get() instanceof CelloMap<?>)
					((CelloMap<?>) cell.get()).setAllDefinedKeys();
				getByQuery(query, index + 1, map.get(indexToKey(context
						.getHeader().getHeaderCellMap(map.keys()), Integer
						.parseInt(m.group(1)))), list);
			} else if (map.containsKey(fieldName)
					|| cell.get() instanceof CelloMap<?>) {
				if (cell.get() instanceof CelloMap<?>)
					((CelloMap<?>) cell.get()).get(fieldName);
				getByQuery(query, index + 1, map.get(fieldName), list);
			} else {
				throw genE(map.keys(), fieldName);
			}
		}
	}

	private String indexToKey(RecursiveMap<?> map, int position) {
		String key = getKeyByPosition(map, position);
		if (key == null)
			throw genE(map.keys(), "[" + position + "]");
		return key;
	}

	private InvalidParameterException genE(String[] keys, String fieldName) {
		return new InvalidParameterException(
				"Unknown field name, key name or index has specified. name="
						+ join(keys, "|") + "|" + fieldName);
	}

	@SuppressWarnings("unchecked")
	private <E> void addToList(List<Cell<E>> list, Cell<Object> cell) {
		list.add((Cell<E>) cell);
	}

	public void remove() {
		this.isRemoved = true;
	}

	public boolean isRemoved() {
		return isRemoved;
	}

}
