package tetz42.cello.contents;

import static tetz42.cello.CelloUtil.*;

import java.util.LinkedHashMap;

import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.util.exception.InvalidParameterException;

public class CellUnitMap<T> {

	public static final <T> CellUnitMap<T> create(Class<T> clazz) {
		return new CellUnitMap<T>(clazz);
	}

	private final Class<T> clazz;
	private final LinkedHashMap<String, T> valueMap = new LinkedHashMap<String, T>();
	@SuppressWarnings("unused")
	private final T template;

	private EachHeaderDef headerDef;
	private EachCellDef cellDef;

	public CellUnitMap(Class<T> clazz) {
		this.clazz = clazz;
		this.template = newInstance(clazz);
	}

	public T get(String key) {
		if (key == null)
			throw new InvalidParameterException(this.getClass().getSimpleName()
					+ "#get does not support null key.");
		if (valueMap.containsKey(key)) {
			valueMap.put(key, newInstance(clazz));
		}
		return valueMap.get(key);
	}

	public int size() {
		return valueMap.size();
	}

	public Class<T> getTemplate() {
		return this.clazz;
	}

	public void setHeaderDef(EachHeaderDef hdef) {
		this.headerDef = hdef;
	}

	public EachHeaderDef getHeaderDef() {
		return headerDef;
	}

	public void setCellDef(EachCellDef cellDef) {
		this.cellDef = cellDef;
	}

	public EachCellDef getCellDef() {
		return cellDef;
	}
}
