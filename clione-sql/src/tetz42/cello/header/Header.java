package tetz42.cello.header;

import static tetz42.cello.TOUtil.*;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import tetz42.cello.Context;
import tetz42.cello.RecursiveMap;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.contents.Cell;
import tetz42.cello.contents.CellUnitMap;

public class Header<T> {

	private final RecursiveMap<List<HCell>> headerCellMap;
	private final Context context;

	private int depth;

	public int getDepth() {
		return depth;
	}

	public Header(Class<T> clazz) {
		headerCellMap = new RecursiveMap<List<HCell>>();
		this.context = new Context(this);
		cellSetting(clazz);
	}

	public Context getContext() {
		return this.context;
	}

	private void cellSetting(Class<T> clazz) {
		genHCellRecursively(newInstance(clazz), null, headerCellMap, 0);
	}

	private void genHCellRecursively(Object value, Field field,
			RecursiveMap<List<HCell>> hcellMap, int depth) {

		// generate HCell
		HCell cell = new HCell(field, depth);
		depth = setCell(cell, hcellMap, depth);

		if (value instanceof CellUnitMap<?>) {
			HCell subCell = new HCell(field.getAnnotation(EachHeaderDef.class),
					ROOT, ++depth);
			hcellMap = hcellMap.get(ROOT);
			depth = setCell(subCell, hcellMap, depth);

			CellUnitMap<?> cuMap = (CellUnitMap<?>) value;
			value = newInstance(cuMap.getTemplate());
		}

		if (isPrimitive(value))
			return;

		// generate field cell
		for (Field f : value.getClass().getDeclaredFields()) {
			if (context.isValid(f)) {
				Object fieldValue = getOrNewValue(value, f);
				genHCellRecursively(fieldValue, f, hcellMap.get(f.getName()),
						depth + 1);
			}
		}
	}

	// TODO refactoring
	public void genHeaderCell(CellUnitMap<?> value, String key,
			String... ownKeys) {

		RecursiveMap<List<HCell>> hcellMap = this.headerCellMap.get(ownKeys);
		HCell template = getFromList(hcellMap.getValue(ROOT));
		hcellMap = hcellMap.get(key);

		// generate HCell
		int depth = template.getDepth();
		HCell cell = new HCell(value.getHeaderDef(), key, depth);
		depth = setCell(cell, hcellMap, depth);

		if (isPrimitive(value))
			return;

		// generate field cell
		for (Field f : value.getClass().getDeclaredFields()) {
			if (context.isValid(f)) {
				Object fieldValue = getOrNewValue(value, f);
				genHCellRecursively(fieldValue, f, hcellMap.get(f.getName()),
						depth + 1);
			}
		}
	}

	private int setCell(HCell cell, RecursiveMap<List<HCell>> hcellMap,
			int depth) {
		if (cell.getDepth() == UNDEFINED)
			depth--;

		// add cell to hcellMap
		List<HCell> list = getListOnMap(hcellMap);
		list.add(cell);
		this.depth = max(this.depth, depth);
		return depth;
	}

	public List<HCell> getList(String... keys) {
		return headerCellMap.getValue(keys);
	}

	public HCell get(String... keys) {
		return getFromList(getList(keys));
	}

//	public Iterable<Cell<String>> each(final int level, final boolean isAll) {
//		return new Iterable<Cell<String>>() {
//
//			@Override
//			public Iterator<Cell<String>> iterator() {
//				return new Iterator<Cell<String>>() {
//
//					private final Iterator<Entry<Object, Cell<Object>>> iterator = map
//							.entrySet().iterator();
//					private Iterator<Cell<String>> colIte;
//
//					@Override
//					public boolean hasNext() {
//						boolean hasColNext = colIte == null ? false : colIte
//								.hasNext();
//						return iterator.hasNext() || hasColNext;
//					}
//
//					@Override
//					public Cell<String> next() {
//						if (colIte != null && colIte.hasNext())
//							return colIte.next();
//						Entry<Object, Cell<Object>> e = iterator.next();
//						// TODO temporary implementation. fix below.
//						colIte = e.getValue().each(level + 1, isAll).iterator();
//						return colIte.next();
//					}
//
//					@Override
//					public void remove() {
//						throw new UnsupportedOperationException(
//								"'remove' is not supported.");
//					}
//				};
//			}
//		};
//	}
}
