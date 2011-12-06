package tetz42.util.tableobject.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tetz42.util.exception.InvalidParameterException;
import tetz42.util.exception.WrapException;
import tetz42.util.tableobject.Column;
import tetz42.util.tableobject.Row;

public class TableObject1<T1> implements Cloneable, ITableObject {
	private final Class<T1> cls1;
	protected final LinkedHashMap<String, Class<?>> headerClsMap;
	private final List<Row> rowList;
	private final Map<String, Integer> indexMap;
	private final List<Row> tailRowList;
	private final Map<String, Integer> tailIndexMap;
	private final Map<String, String> aliasMap;

	protected Row currentRow;
	private int headerLevel;

	public TableObject1(Class<T1> cls1) {
		this.cls1 = cls1;
		this.headerLevel = 1;
		this.headerClsMap = new LinkedHashMap<String, Class<?>>();
		this.aliasMap = new HashMap<String, String>();
		this.rowList = new ArrayList<Row>();
		this.indexMap = new HashMap<String, Integer>();
		this.tailRowList = new ArrayList<Row>();
		this.tailIndexMap = new HashMap<String, Integer>();
	}

	public void setHeaderLevel(int level) {
		this.headerLevel = level;
	}

	public void setHeader(String... keys) {
		setHeaderAs1(keys);
	}

	public void setHeaderAs1(String... keys) {
		for (String key : keys)
			headerClsMap.put(key, cls1);
	}

	/* (non-Javadoc)
	 * @see tetz42.util.tableobject.tables.ITableObject#setAlias(java.lang.String, java.lang.String)
	 */
	public void setAlias(String name, String alias) {
		aliasMap.put(name, alias);
	}

	/* (non-Javadoc)
	 * @see tetz42.util.tableobject.tables.ITableObject#getAlias(java.lang.String)
	 */
	public String getAlias(String name) {
		if (aliasMap.containsKey(name))
			return aliasMap.get(name);
		return name;
	}

	public Column<T1> get(String key) {
		return getAs1(key);
	}

	public Column<T1> getAs1(String key) {
		return currentRow.get(cls1, key);
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

	public TableObject1<T1> row(int index) {
		TableObject1<T1> clone = this.clone();
		clone.currentRow = fillRow(rowList, index);
		return clone;
	}

	public TableObject1<T1> row(String rowKey) {
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

	public TableObject1<T1> tail() {
		return tail(0);
	}

	public TableObject1<T1> tail(int index) {
		TableObject1<T1> clone = this.clone();
		clone.currentRow = fillRow(tailRowList, index);
		return clone;
	}

	public TableObject1<T1> tail(String rowKey) {
		if (!tailIndexMap.containsKey(rowKey)) {
			tailIndexMap.put(rowKey, tailRowList.size());
		}
		return tail(tailIndexMap.get(rowKey));
	}

	/* (non-Javadoc)
	 * @see tetz42.util.tableobject.tables.ITableObject#headers(int)
	 */
	public Iterable<Column<String>> headers(int level) {
		return headers(level, false);
	}

	/* (non-Javadoc)
	 * @see tetz42.util.tableobject.tables.ITableObject#headersAll(int)
	 */
	public Iterable<Column<String>> headersAll(int level) {
		return headers(level, true);
	}

	private Iterable<Column<String>> headers(int level, boolean isAll) {
		if (headerLevel < level)
			throw new InvalidParameterException(
					"Parameter, 'level', should be smaller than 'headerLevel'.");
		return genRow().each(this.aliasMap, headerLevel, level, isAll);
	}

	/* (non-Javadoc)
	 * @see tetz42.util.tableobject.tables.ITableObject#headers()
	 */
	public Iterable<Column<String>> headers() {
		return genRow().each();
	}

	/* (non-Javadoc)
	 * @see tetz42.util.tableobject.tables.ITableObject#rows()
	 */
	public List<Row> rows() {
		ArrayList<Row> list = new ArrayList<Row>();
		list.addAll(rowList);
		list.addAll(tailRowList);
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected TableObject1<T1> clone() {
		try {
			return (TableObject1<T1>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new WrapException(e);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (headerLevel == 1) {
			appendHeaders(sb, this.headers());
		} else {
			appendHeaders(sb, this.headers(1));
			appendHeaders(sb, this.headers(2));
		}
		for (Row row : this.rows()) {
			for (Column<String> col : row.each()) {
				sb.append(col.get()).append("\t");
			}
			sb.deleteCharAt(sb.length() - 1).append("\n");
		}
		return sb.toString();
	}

	private void appendHeaders(StringBuilder sb, Iterable<Column<String>> ite) {
		for (Column<String> col : ite) {
			String key = col.isSkip() ? "" : col.getKey();
			sb.append(key).append("\t");
		}
		sb.deleteCharAt(sb.length() - 1).append("\n");
	}

}
