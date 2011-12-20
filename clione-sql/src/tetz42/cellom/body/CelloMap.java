package tetz42.cellom.body;

import static tetz42.cellom.CelloUtil.*;

import java.util.LinkedHashMap;
import java.util.List;

import tetz42.cellom.annotation.EachBody;
import tetz42.cellom.annotation.EachHeader;
import tetz42.cellom.header.HeaderCell;
import tetz42.cellom.Context;
import tetz42.cellom.RecursiveMap;
import tetz42.util.exception.InvalidParameterException;

public class CelloMap<V> {

	public static final <V> CelloMap<V> create(Class<V> clazz) {
		return new CelloMap<V>(clazz);
	}

	private final Class<V> clazz;
	private final LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();

	private Context<?> context;
	private EachHeader headerDef;
	private EachBody cellDef;
	private String[] keys;
	private Row<?> row;

	public CelloMap(Class<V> clazz) {
		this.clazz = clazz;
	}

	void init(Context<?> context, String[] keys, Row<?> row,
			EachHeader hdef, EachBody cellDef) {
		this.context = context;
		this.keys = keys;
		this.row = row;
		this.headerDef = hdef;
		this.cellDef = cellDef;
	}

	public String[] getKeys() {
		return this.keys;
	}

	public V get(String key) {
		if (key == null)
			throw new InvalidParameterException(this.getClass().getSimpleName()
					+ "#get does not support null key.");

		// definition check
		RecursiveMap<List<HeaderCell>> hcellMap = context.getHeader()
				.getHeaderCellMap(keys);
		if (!hcellMap.containsKey(key)) {
			context.getHeader().defineHeader(this, key, hcellMap);
		}

		// value check
		if (!valueMap.containsKey(key)) {
			set(key, newInstance(clazz));
		}

		// return the value
		return valueMap.get(key);
	}

	public void set(String key, V value) {
		boolean containsKey = valueMap.containsKey(key);
		valueMap.put(key, value);

		// value check
		if (!containsKey) {
			row.genCell(this, key, keys);
		}
	}

	public void setAllDefinedKeys() {
		for (String cuKey : this.context.getHeader().getCuMapKeys(this)) {
			if (cuKey != null)
				get(cuKey);
		}
	}

	public int size() {
		return valueMap.size();
	}

	public Class<V> getTemplate() {
		return this.clazz;
	}

	public EachHeader getHeaderDef() {
		return headerDef;
	}

	public EachBody getCellDef() {
		return cellDef;
	}

}
