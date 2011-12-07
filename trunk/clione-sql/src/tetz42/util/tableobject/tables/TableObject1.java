package tetz42.util.tableobject.tables;

import static tetz42.util.tableobject.TOUtil.*;

import java.lang.reflect.Field;
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
	private final List<Row> rowList;
	private final Map<String, Integer> indexMap;
	private final List<Row> tailRowList;
	private final Map<String, Integer> tailIndexMap;
	protected final ContextValues context = new ContextValues();

	protected Row currentRow;

	public static class ContextValues {
		public int headerDepth = 0;
		public final Map<String, String> aliasMap = new HashMap<String, String>();
		public final LinkedHashMap<String, HeaderInfo> headerClsMap = new LinkedHashMap<String, HeaderInfo>();
		public int[] displayHeaders = null;

	}

	public boolean isTopHeader(int level) {
		setDefaultDisplayHeaders();
		return level == context.displayHeaders[0];
	}

	public static class HeaderInfo {
		public static final int UNDEFINED = -1;
		public final Class<?> cls;
		public final int width;

		protected HeaderInfo(Class<?> cls, int width) {
			this.cls = cls;
			this.width = width;
		}

		protected HeaderInfo(Class<?> cls) {
			this(cls, UNDEFINED);
		}
	}

	public TableObject1(Class<T1> cls1) {
		this.cls1 = cls1;
		this.rowList = new ArrayList<Row>();
		this.indexMap = new HashMap<String, Integer>();
		this.tailRowList = new ArrayList<Row>();
		this.tailIndexMap = new HashMap<String, Integer>();
		setHeaderDepth(this.cls1);
	}

	protected final void setHeaderDepth(Class<?> clazz) {
		System.out.println("<%-------------------------");
		context.headerDepth = max(context.headerDepth, countDepth(clazz, 0));
		System.out.println(context.headerDepth);
		System.out.println("-------------------------%>");
	}

	private int countDepth(Class<?> clazz, int paramCount) {
		System.out.println(clazz.getName());
		paramCount++;
		if (isPrimitive(clazz))
			return paramCount;
		int count = paramCount;
		for (Field f : clazz.getDeclaredFields()) {
			count = max(count, countDepth(f.getType(), paramCount));
		}
		return count;
	}

	public void setDisplayHeaders(int... displayHeaders) {
		if (displayHeaders.length == 0)
			throw new InvalidParameterException(
					"No parameter detected. Must be passed 1 more parameters.");
		context.displayHeaders = displayHeaders;
	}

	private void setDefaultDisplayHeaders() {
		if (context.displayHeaders != null)
			return;
		int[] displayHeaders = new int[context.headerDepth];
		for (int i = 0; i < displayHeaders.length; i++)
			displayHeaders[i] = i;
		context.displayHeaders = displayHeaders;
	}

	public void setHeader(String... keys) {
		setHeaderAs1(keys);
	}

	public void setHeader(String key, int width) {
		setHeaderAs1(key, width);
	}

	public void setHeaderAs1(String... keys) {
		for (String key : keys)
			context.headerClsMap.put(key, new HeaderInfo(cls1));
	}

	public void setHeaderAs1(String key, int width) {
		context.headerClsMap.put(key, new HeaderInfo(cls1, width));
	}

	public void setAlias(String name, String alias) {
		context.aliasMap.put(name, alias);
	}

	public String getAlias(String name) {
		if (context.aliasMap.containsKey(name))
			return context.aliasMap.get(name);
		return name;
	}

	public Column<T1> get(String key) {
		return getAs1(key);
	}

	public Column<T1> getAs1(String key) {
		return currentRow.get(cls1, key);
	}

	public List<Column<T1>> columns1() {
		return currentRow.columnList(cls1);
	}

	private int rowIndex = 0;

	public void resetRowIndex() {
		rowIndex = 0;
	}

	public boolean nextRow() {
		if (rowIndex >= rowList.size())
			return false;
		setRow(rowIndex);
		rowIndex++;
		return true;
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

	public Iterable<Column<String>> headers(int level) {
		return headers(level, isTopHeader(level));
	}

	public Iterable<Column<String>> headers(int level, boolean isAll) {
		setDefaultDisplayHeaders();
		return genRow().each(level, isAll);
	}

	public Iterable<Column<String>> headers() {
		int level = context.headerDepth - 1;
		setDisplayHeaders(level);
		return headers(level, true);
	}

	public void removeRow() {
		if (currentRow != null)
			currentRow.remove();
	}

	public List<Row> rows() {
		ArrayList<Row> list = new ArrayList<Row>();
		avoidRemovedRow();
		list.addAll(rowList);
		list.addAll(tailRowList);
		return list;
	}

	private void avoidRemovedRow() {
		avoidRemovedRow(rowList);
		avoidRemovedRow(tailRowList);
	}

	private List<Row> avoidRemovedRow(List<Row> ls) {
		for (int i = ls.size() - 1; i >= 0; i--) {
			if (ls.get(i).isRemoved()) {
				ls.remove(i);
			}
		}
		return ls;
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
		Row row = new Row(context);
		for (Map.Entry<String, HeaderInfo> e : context.headerClsMap.entrySet())
			row.get(e.getValue().cls, e.getKey());
		return row;
	}

	@Override
	public String toString() {
		this.setDefaultDisplayHeaders();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < context.displayHeaders.length; i++)
			appendHeaders(sb, this.headers(context.displayHeaders[i]));
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