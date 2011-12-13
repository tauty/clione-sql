package tetz42.util.tablequery;

import static tetz42.util.tablequery.TOUtil.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import tetz42.util.exception.WrapException;

public class QRow<T> {

	private final RecursiveMap<List<Cell<Object>>> cellMap;
	private final Context<T> context;
	private final T value;

	public QRow(Class<T> clazz, Context<T> context) {
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
			genCellRecursively(this, clazz, f, cellMap);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	private void genCellRecursively(Object receiver, Class<?> clazz,
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
			genCellRecursively(value, f.getType(), f, cellMap.get(f.getName()));
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
