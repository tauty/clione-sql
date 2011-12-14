package tetz42.cello.contents;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import tetz42.cello.Context;
import tetz42.cello.RecursiveMap;
import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.util.exception.WrapException;

public class Row<T> {

	private final RecursiveMap<List<Cell<Object>>> cellMap;
	private final Context context;
	private final T value;

	public Row(Class<T> clazz, Context context) {
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
		Cell<Object> cell = new CellForMap<Object>((CellUnitMap<Object>) cumap, key);
		list.add(cell);

		Object value = cell.get();

		// TODO too difficult. fix someday.
//		if (value instanceof CellUnitMap<?>) {
//			CellUnitMap<?> cumap = (CellUnitMap<?>) value;
//			cumap.init(context, cellMap.keys(), this, field
//					.getAnnotation(EachHeaderDef.class), field
//					.getAnnotation(EachCellDef.class));
//			return;
//		}

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
}
