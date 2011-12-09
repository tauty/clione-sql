package tetz42.util.tablequery.tables;

import java.util.List;

import tetz42.util.tablequery.Column;

public class TableObject4<T1, T2, T3, T4> extends TableObject3<T1, T2, T3> {

	private final Class<T4> cls4;

	public TableObject4(Class<T1> cls1, Class<T2> cls2, Class<T3> cls3,
			Class<T4> cls4) {
		super(cls1, cls2, cls3);
		this.cls4 = cls4;
		setHeaderDepth(this.cls4);
	}

	public void setHeaderAs4(String... keys) {
		for (String key : keys)
			context.headerClsMap.put(key, new HeaderInfo(cls4));
	}

	public void setHeaderAs4(String key, int width) {
		context.headerClsMap.put(key, new HeaderInfo(cls4, width));
	}

	public Column<T4> getAs4(String key) {
		return currentRow.get(cls4, key);
	}

	public List<Column<T4>> columns4() {
		return currentRow.columnList(cls4);
	}

	public List<Column<T4>> columns4(String... keys) {
		return columns(cls4, keys);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject4<T1, T2, T3, T4> row(int index) {
		return (TableObject4<T1, T2, T3, T4>) super.row(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject4<T1, T2, T3, T4> row(String rowKey) {
		return (TableObject4<T1, T2, T3, T4>) super.row(rowKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject4<T1, T2, T3, T4> tail() {
		return (TableObject4<T1, T2, T3, T4>) super.tail();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject4<T1, T2, T3, T4> tail(int index) {
		return (TableObject4<T1, T2, T3, T4>) super.tail(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject4<T1, T2, T3, T4> tail(String rowKey) {
		return (TableObject4<T1, T2, T3, T4>) super.tail(rowKey);
	}
}
