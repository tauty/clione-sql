package tetz42.cello.contents;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import tetz42.cello.Context;
import tetz42.cello.ICell;
import tetz42.cello.IRow;
import tetz42.cello.Query;
import tetz42.cello.RecursiveMap;
import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.header.HeaderCell;
import tetz42.util.exception.InvalidParameterException;
import tetz42.util.exception.WrapException;

public class Row<T> implements IRow {

	private final RecursiveMap<List<Cell<Object>>> cellMap;
	private final Context<T> context;
	private final T value;

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
		if (value instanceof CellUnitMap<?>) {
			CellUnitMap<?> cumap = (CellUnitMap<?>) value;
			cumap.init(context, cellMap.keys(), this, field
					.getAnnotation(EachHeaderDef.class), field
					.getAnnotation(EachCellDef.class));
			return;
		}

		if (isPrimitive(value))
			return;

		// generate field cell
		for (Field f : value.getClass().getDeclaredFields()) {
			if (context.isValid(f)) {
				genCellRecursively(value, f, cellMap.get(f.getName()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	void genCell(CellUnitMap<?> cumap, String key, String... ownKeys) {
		RecursiveMap<List<Cell<Object>>> cellMap = this.cellMap.get(ownKeys);
		cellMap = cellMap.get(key);

		// generate cell
		List<Cell<Object>> list = getListOnMap(cellMap);
		Cell<Object> cell = new CellForMap<Object>((CellUnitMap<Object>) cumap,
				key);
		list.add(cell);

		Object value = cell.get();

		// TODO too difficult. fix someday.
		// if (value instanceof CellUnitMap<?>) {
		// CellUnitMap<?> cumap = (CellUnitMap<?>) value;
		// cumap.init(context, cellMap.keys(), this, field
		// .getAnnotation(EachHeaderDef.class), field
		// .getAnnotation(EachCellDef.class));
		// return;
		// }

		if (isPrimitive(value))
			return;

		// generate field cell
		for (Field f : value.getClass().getDeclaredFields()) {
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
		if (isRemoved(cellMap))
			return;

		Cell<Object> cell = getFromList(cellMap.getValue());
		Object value = cell.get();
		if (isPrimitive(value)) {
			list.add(cell);
		} else {
			if (value instanceof CellUnitMap<?>)
				((CellUnitMap<?>) cell.get()).setAllDefinedKeys();
			for (Entry<String, RecursiveMap<List<Cell<Object>>>> e : cellMap
					.entrySet()) {
				each(e.getValue(), list);
			}
		}
	}

	private boolean isRemoved(RecursiveMap<List<Cell<Object>>> cellMap) {
		if (!this.context.getHeader().containsHeaderCellMap(cellMap.keys()))
			return true;
		RecursiveMap<List<HeaderCell>> hCellMap = this.context.getHeader()
				.getHeaderCellMap(cellMap.keys());
		HeaderCell hCell = getFromList(hCellMap.getValue());
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

	@SuppressWarnings("unchecked")
	private <E> List<Cell<E>> getByQuery(Query query, int index,
			RecursiveMap<List<Cell<Object>>> map, List<Cell<E>> list) {
		Cell<Object> cell = getFromList(map.getValue());
		if (query.get(index) == null) {
			list.add((Cell<E>) cell);
		} else {
			if (cell.get() instanceof CellUnitMap<?>)
				((CellUnitMap<?>) cell.get()).setAllDefinedKeys();
			String[] fieldNames = query.get(index);
			for (String fieldName : fieldNames) {
				if (fieldName.equals(Query.ANY)) {
					for (Entry<String, RecursiveMap<List<Cell<Object>>>> e : map
							.entrySet()) {
						if (e.getKey() != null) {
							getByQuery(query, index + 1, e.getValue(), list);
						}
					}
				} else if (fieldName.startsWith(Query.TERMINATE)) {
					fieldName = fieldName.substring(1);
					if (map.containsKey(fieldName)) {
						RecursiveMap<List<Cell<Object>>> subMap = map
								.get(fieldName);
						Cell<Object> subCell = getFromList(subMap.getValue());
						list.add((Cell<E>) subCell);
					} else {
						throw new InvalidParameterException(
								"Unknown field/key name has specified. name="
										+ join(map.keys(), "|") + "|"
										+ fieldName);
					}
				} else if (map.containsKey(fieldName)) {
					getByQuery(query, index + 1, map.get(fieldName), list);
				} else {
					throw new InvalidParameterException(
							"Unknown field/key name has specified. name="
									+ join(map.keys(), "|") + "|" + fieldName);
				}
			}
		}
		return list;
	}

}
