package tetz42.cello.header;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import tetz42.cello.Context;
import tetz42.cello.ICell;
import tetz42.cello.IHeader;
import tetz42.cello.RecursiveMap;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.contents.CellUnitMap;

public class Header<T> implements IHeader {

	private final RecursiveMap<List<HCell>> headerCellMap;
	private final Context<T> context;

	private int depth;

	public RecursiveMap<List<HCell>> getHeaderCellMap(String... keys) {
		return headerCellMap.get(keys);
	}

	public int getDepth() {
		return depth;
	}

	public Header(Class<T> clazz) {
		headerCellMap = new RecursiveMap<List<HCell>>();
		this.context = new Context<T>(clazz, this);
		cellSetting(clazz);
		this.context.init();
	}

	public Context<T> getContext() {
		return this.context;
	}

	private void cellSetting(Class<T> clazz) {
		genHCellRecursively(newInstance(clazz), null, headerCellMap, 0);
	}

	public void defineHeader(CellUnitMap<?> cumap, String key,
			RecursiveMap<List<HCell>> hcellMap) {

		HCell template = getFromList(hcellMap.getValue(ROOT));
		hcellMap = hcellMap.get(key);

		// generate HCell
		int depth = template.getRealDepth();
		if (template.getDepth() == UNDEFINED)
			depth++; // same condition when the template was generated
		HCell cell = new HCell(context, cumap.getHeaderDef(), key, depth);
		depth = setCell(cell, hcellMap, depth);
		Object value = newInstance(cumap.getTemplate());

		// generate field cell
		for (Field f : cumap.getTemplate().getDeclaredFields()) {
			if (context.isValid(f)) {
				Object fieldValue = getOrNewValue(value, f);
				genHCellRecursively(fieldValue, f, hcellMap.get(f.getName()),
						depth + 1);
			}
		}
	}

	public Iterable<String> getCuMapKeys(CellUnitMap<?> cumap) {
		RecursiveMap<List<HCell>> map = headerCellMap.get(cumap.getKeys());
		return map.keySet();
	}

	private void genHCellRecursively(Object value, Field field,
			RecursiveMap<List<HCell>> hcellMap, int depth) {

		// generate HCell
		HCell cell = new HCell(context, field, depth);
		depth = setCell(cell, hcellMap, depth);

		if (value instanceof CellUnitMap<?>) {
			depth++;
			HCell subCell = new HCell(context, field
					.getAnnotation(EachHeaderDef.class), ROOT, depth);
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

	@Override
	public Iterable<Iterable<ICell>> each() {
		List<Iterable<ICell>> result = new ArrayList<Iterable<ICell>>();
		for (int depth : context.displayHeaders) {
			result.add(each(depth));
		}
		return result;
	}

	public List<ICell> each(int depth) {
		calcCellSize();

		ArrayList<ICell> list = new ArrayList<ICell>();
		System.out.println("------------- start:" + depth + "---------------");
		each(depth, this.headerCellMap, list);
		System.out.println("--------------  end  ----------------");
		System.out.println();

		return list;
	}

	private void each(int depth, RecursiveMap<List<HCell>> hcellMap,
			ArrayList<ICell> list) {
		HCell hCell = getFromList(hcellMap.getValue());
		if(hCell.isRemoved())
			return;

		System.out.println("hCell#name = " + hCell.getName() + ", depth = "
				+ hCell.getDepth());
		if (hCell.getDepth() == depth) {
			list.add(hCell);
			if (hcellMap.size() == 0) {
				hCell.setY(context.depth - depth + 1);
			} else {
				for (int i = 1; i < hCell.getSize(); i++)
					list.add(context.getEmptyCell());
			}
			return;
		}

		if (hCell.getDepth() > 0 && hcellMap.size() == 0) {
			if (context.isTop(depth)) {
				hCell.setY(context.depth - depth + 1);
				list.add(hCell);
			} else {
				list.add(context.getEmptyCell());
			}
			return;
		}

		for (Entry<String, RecursiveMap<List<HCell>>> e : hcellMap.entrySet()) {
			if (e.getKey() == null
					|| getFromList(e.getValue().getValue()).isRemoved())
				continue;
			each(depth, e.getValue(), list);
		}
	}

	public void calcCellSize() {
		calcCellSize(this.headerCellMap);
	}

	private int calcCellSize(RecursiveMap<List<HCell>> hcellMap) {
		int size;
		HCell hCell = getFromList(hcellMap.getValue());
		if (hcellMap.size() == 0) {
			size = 1;
		} else {
			size = 0;
			for (Entry<String, RecursiveMap<List<HCell>>> e : hcellMap
					.entrySet()) {
				if (e.getKey() == null
						|| getFromList(e.getValue().getValue()).isRemoved())
					continue;
				size += calcCellSize(e.getValue());
			}
		}
		hCell.setSize(size);
		hCell.setX(size);
		return size;
	}

}
