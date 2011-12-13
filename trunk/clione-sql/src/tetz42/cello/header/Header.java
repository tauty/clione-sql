package tetz42.cello.header;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
		this.context.init();
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
		HCell cell = new HCell(context, field, depth);
		depth = setCell(cell, hcellMap, depth);

		if (value instanceof CellUnitMap<?>) {
			HCell subCell = new HCell(context, field.getAnnotation(EachHeaderDef.class),
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
		HCell cell = new HCell(context, value.getHeaderDef(), key, depth);
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

	public Iterable<Cell<String>> each(final int level) {
		ArrayList<Cell<String>> list = new ArrayList<Cell<String>>();
		
		// TODO
		return list;
	}
}
