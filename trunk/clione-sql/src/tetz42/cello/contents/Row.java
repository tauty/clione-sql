package tetz42.cello.contents;

import static tetz42.cello.TOUtil.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import tetz42.cello.Context;
import tetz42.cello.RecursiveMap;
import tetz42.util.exception.WrapException;

public class Row<T> {

	private final RecursiveMap<List<Cell<Object>>> cellMap;
	private final Context context;
	private final T value;

	public Row(Class<T> clazz, Context context) {
		cellMap = new RecursiveMap<List<Cell<Object>>>();
		this.context = context;
		try {
			this.value = clazz.newInstance();
		} catch (Exception e) {
			throw new WrapException(e);
		}
		cellSetting(clazz);
	}

	public T get() {
		return value;
	}

	private void cellSetting(Class<T> clazz) {
		try {
			Field f = this.getClass().getDeclaredField("value");
			f.setAccessible(true);

			// --------initial setting--------
			setValue(this, f, clazz.newInstance());
			// ----------------


			genCellRecursively(clazz, this, f, cellMap);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	private void genCellRecursively(Class<?> clazz,Object receiver,
			Field field, RecursiveMap<List<Cell<Object>>> cellMap) {

		// generate cell
		List<Cell<Object>> list = getListOnMap(cellMap);
		Cell<Object> cell = new Cell<Object>(receiver, field, context);
		list.add(cell);

		if (isPrimitive(clazz))
			return;

		// generate field cell
		Object value = cell.get();
		for (Field f : value.getClass().getDeclaredFields()) {
			genCellRecursively(f.getType(), value, f, cellMap.get(f.getName()));
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
