package tetz42.cellom.header;

import static tetz42.cellom.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import tetz42.cellom.Context;
import tetz42.cellom.ICell;
import tetz42.cellom.IHeader;
import tetz42.cellom.Query;
import tetz42.cellom.RecursiveMap;
import tetz42.cellom.annotation.EachHeader;
import tetz42.cellom.body.CelloMap;
import tetz42.util.exception.InvalidParameterException;

public class HeaderManager<T> implements IHeader {

	private final RecursiveMap<List<HeaderCell>> headerCellMap;
	private final Context<T> context;

	private int depth;

	public RecursiveMap<List<HeaderCell>> getHeaderCellMap(String... keys) {
		return headerCellMap.get(keys);
	}

	public boolean containsHeaderCellMap(String... keys) {
		return headerCellMap.containsKey(keys);
	}

	public int getDepth() {
		return depth;
	}

	public HeaderManager(Class<T> clazz) {
		headerCellMap = new RecursiveMap<List<HeaderCell>>();
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

	public void defineHeader(CelloMap<?> cumap, String key,
			RecursiveMap<List<HeaderCell>> hcellMap) {

		HeaderCell template = getFromList(hcellMap.getValue(ROOT));
		hcellMap = hcellMap.get(key);

		// generate HCell
		int depth = template.getRealDepth();
		if (template.getDepth() == UNDEFINED)
			depth++; // same condition when the template was generated
		HeaderCell cell = new HeaderCell(context, cumap.getHeaderDef(), key,
				depth);
		depth = setCell(cell, hcellMap, depth);
		Object value = newInstance(cumap.getTemplate());

		// generate field cell
		for (Field f : cumap.getTemplate().getDeclaredFields()) {
			if (context.isValid(f) && !context.isHidden(f)) {
				Object fieldValue = getOrNewValue(value, f);
				genHCellRecursively(fieldValue, f, hcellMap.get(f.getName()),
						depth + 1);
			}
		}
	}

	public Iterable<String> getCuMapKeys(CelloMap<?> cumap) {
		RecursiveMap<List<HeaderCell>> map = headerCellMap.get(cumap.getKeys());
		return map.keySet();
	}

	private void genHCellRecursively(Object value, Field field,
			RecursiveMap<List<HeaderCell>> hcellMap, int depth) {

		// generate HCell
		HeaderCell cell = new HeaderCell(context, field, depth);
		depth = setCell(cell, hcellMap, depth);

		if (value instanceof CelloMap<?>) {
			depth++;
			HeaderCell subCell = new HeaderCell(context, field
					.getAnnotation(EachHeader.class), ROOT, depth);
			hcellMap = hcellMap.get(ROOT);
			depth = setCell(subCell, hcellMap, depth);

			CelloMap<?> cuMap = (CelloMap<?>) value;
			value = newInstance(cuMap.getTemplate());
		}

		if (isPrimitive(value))
			return;

		// generate field cell
		for (Field f : value.getClass().getDeclaredFields()) {
			if (context.isValid(f) && !context.isHidden(f)) {
				Object fieldValue = getOrNewValue(value, f);
				genHCellRecursively(fieldValue, f, hcellMap.get(f.getName()),
						depth + 1);
			}
		}
	}

	private int setCell(HeaderCell cell,
			RecursiveMap<List<HeaderCell>> hcellMap, int depth) {
		if (cell.getDepth() == UNDEFINED)
			depth--;

		// add cell to hcellMap
		List<HeaderCell> list = getListOnMap(hcellMap);
		list.add(cell);
		this.depth = max(this.depth, depth);
		return depth;
	}

	public List<HeaderCell> getList(String... keys) {
		return headerCellMap.getValue(keys);
	}

	public HeaderCell get(String... keys) {
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
		each(depth, this.headerCellMap, list);
		return list;
	}

	private void each(int depth, RecursiveMap<List<HeaderCell>> hcellMap,
			ArrayList<ICell> list) {
		HeaderCell hCell = getFromList(hcellMap.getValue());
		if (hCell.isRemoved())
			return;

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

		for (Entry<String, RecursiveMap<List<HeaderCell>>> e : hcellMap
				.entrySet()) {
			if (e.getKey() == null
					|| getFromList(e.getValue().getValue()).isRemoved())
				continue;
			each(depth, e.getValue(), list);
		}
	}

	public void calcCellSize() {
		calcCellSize(this.headerCellMap);
	}

	private int calcCellSize(RecursiveMap<List<HeaderCell>> hcellMap) {
		int size;
		HeaderCell hCell = getFromList(hcellMap.getValue());
		if (hcellMap.size() == 0) {
			size = 1;
		} else {
			size = 0;
			for (Entry<String, RecursiveMap<List<HeaderCell>>> e : hcellMap
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

	public void calcCellWidth() {
		// TODO implementation
	}

	public <E> List<HeaderCell> getByQuery(
			@SuppressWarnings("unused") Class<E> clazz, String query) {
		return getByQuery(query);
	}

	public <E> List<HeaderCell> getByQuery(String query) {
		return getByQuery(Query.parse(query));
	}

	public List<HeaderCell> getByQuery(Query query) {
		return getByQuery(query, 0, this.headerCellMap,
				new ArrayList<HeaderCell>());
	}

	private List<HeaderCell> getByQuery(Query query, int index,
			RecursiveMap<List<HeaderCell>> map, List<HeaderCell> list) {
		if (query.get(index) == null) {
			list.add(getFromList(map.getValue()));
		} else {
			for (String fieldName : query.get(index)) {
				if (fieldName.equals(Query.ANY)) {
					for (Entry<String, RecursiveMap<List<HeaderCell>>> e : map
							.entrySet()) {
						if (e.getKey() != null) {
							getByQuery(query, index + 1, e.getValue(), list);
						}
					}
				} else if (fieldName.startsWith(Query.TERMINATE)) {
					fieldName = fieldName.substring(1);
					if (map.containsKey(fieldName)) {
						RecursiveMap<List<HeaderCell>> subMap = map
								.get(fieldName);
						list.add(getFromList(subMap.getValue()));
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
