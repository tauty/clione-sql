package tetz42.cello.contents;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import tetz42.cello.Context;
import tetz42.cello.ICell;
import tetz42.cello.IRow;
import tetz42.cello.RecursiveMap;
import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.header.HCell;
import tetz42.util.exception.WrapException;

public class Row<T> implements IRow{

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

//		if (cellMap.size() == 0) {
//		list.add(getFromList(cellMap.getValue()));
		Cell<Object> cell = getFromList(cellMap.getValue());
		if (isPrimitive(cell.get())) {
			list.add(cell);
		} else {
			for (Entry<String, RecursiveMap<List<Cell<Object>>>> e : cellMap
					.entrySet()) {
				each(e.getValue(), list);
			}
		}
	}

	private boolean isRemoved(RecursiveMap<List<Cell<Object>>> cellMap) {
		RecursiveMap<List<HCell>> hCellMap = this.context.getHeader()
				.getHeaderCellMap(cellMap.keys());
		HCell hCell = getFromList(hCellMap.getValue());
		return hCell.isRemoved();
	}

}
