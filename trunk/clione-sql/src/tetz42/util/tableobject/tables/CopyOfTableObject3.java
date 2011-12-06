package tetz42.util.tableobject.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tetz42.util.tableobject.Column;
import tetz42.util.tableobject.Row;

public class CopyOfTableObject3<T1, T2, T3> implements Cloneable {

	private final Class<T1> cls1;
	private final Class<T2> cls2;
	private final Class<T3> cls3;
	protected final LinkedHashMap<String, Class<?>> headerClsMap;
	private final List<Row> rowList;
	private final Map<String, Integer> indexMap;
	private final List<Row> tailRowList;
	private final Map<String, Integer> tailIndexMap;

	protected Row currentRow;

	public CopyOfTableObject3(Class<T1> cls1, Class<T2> cls2, Class<T3> cls3) {
		this.cls1 = cls1;
		this.cls2 = cls2;
		this.cls3 = cls3;
		this.headerClsMap = new LinkedHashMap<String, Class<?>>();
		this.rowList = new ArrayList<Row>();
		this.indexMap = new HashMap<String, Integer>();
		this.tailRowList = new ArrayList<Row>();
		this.tailIndexMap = new HashMap<String, Integer>();
	}

	public void setHeaderAs1(String... keys) {
		for (String key : keys)
			headerClsMap.put(key, cls1);
	}

	public void setHeaderAs2(String... keys) {
		for (String key : keys)
			headerClsMap.put(key, cls2);
	}

	public void setHeaderAs3(String... keys) {
		for (String key : keys)
			headerClsMap.put(key, cls3);
	}

	public Column<T1> getAs1(String key) {
		return currentRow.get(cls1, key);
	}

	public Column<T2> getAs2(String key) {
		return currentRow.get(cls2, key);
	}

	public Column<T3> getAs3(String key) {
		return currentRow.get(cls3, key);
	}

	public void newRow() {
		setRow(rowList.size());
	}

	public void setRow(int index) {
		currentRow = fillRow(rowList, index);
	}

	public void setRow(String rowKey) {
		if (!indexMap.containsKey(rowKey)) {
			indexMap.put(rowKey, rowList.size());
			fillRow(rowList, rowList.size());
		}
		currentRow = rowList.get(indexMap.get(rowKey));
	}

	public CopyOfTableObject3<T1, T2, T3> row(int index) {
		CopyOfTableObject3<T1, T2, T3> clone = this.clone();
		clone.currentRow = fillRow(rowList, index);
		return clone;
	}

	public CopyOfTableObject3<T1, T2, T3> row(String rowKey) {
		if (!indexMap.containsKey(rowKey)) {
			indexMap.put(rowKey, rowList.size());
		}
		return row(indexMap.get(rowKey));
	}

	public void newTailRow() {
		setTailRow(tailRowList.size());
	}

	public void setTailRow(int index) {
		currentRow = fillRow(tailRowList, index);
	}

	public void setTailRow(String rowKey) {
		if (!tailIndexMap.containsKey(rowKey)) {
			tailIndexMap.put(rowKey, tailRowList.size());
			fillRow(tailRowList, tailRowList.size());
		}
		currentRow = tailRowList.get(tailIndexMap.get(rowKey));
	}

	public CopyOfTableObject3<T1, T2, T3> tail() {
		return tail(0);
	}

	public CopyOfTableObject3<T1, T2, T3> tail(int index) {
		CopyOfTableObject3<T1, T2, T3> clone = this.clone();
		clone.currentRow = fillRow(tailRowList, index);
		return clone;
	}

	public CopyOfTableObject3<T1, T2, T3> tail(String rowKey) {
		if (!tailIndexMap.containsKey(rowKey)) {
			tailIndexMap.put(rowKey, tailRowList.size());
		}
		return tail(tailIndexMap.get(rowKey));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected CopyOfTableObject3<T1, T2, T3> clone() {
		try {
			return (CopyOfTableObject3<T1, T2, T3>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	private Row fillRow(List<Row> rowList, int index) {
		for (int i = rowList.size(); i <= index; i++) {
			rowList.add(genRow());
		}
		return rowList.get(index);
	}

	private Row genRow() {
		Row row = new Row();
		for (Map.Entry<String, Class<?>> e : headerClsMap.entrySet())
			row.get(e.getValue(), e.getKey());
		return row;
	}

}
