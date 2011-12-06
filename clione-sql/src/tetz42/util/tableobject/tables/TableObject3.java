package tetz42.util.tableobject.tables;

import tetz42.util.tableobject.Column;

public class TableObject3<T1, T2, T3> extends TableObject2<T1, T2> {

	private final Class<T3> cls3;

	public TableObject3(Class<T1> cls1, Class<T2> cls2, Class<T3> cls3) {
		super(cls1, cls2);
		this.cls3 = cls3;
	}

	public void setHeaderAs3(String... keys) {
		for (String key : keys)
			headerClsMap.put(key, cls3);
	}

	public Column<T3> getAs3(String key) {
		return currentRow.get(cls3, key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject3<T1, T2, T3> row(int index) {
		return (TableObject3<T1, T2, T3>) super.row(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject3<T1, T2, T3> row(String rowKey) {
		return (TableObject3<T1, T2, T3>) super.row(rowKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject3<T1, T2, T3> tail() {
		return (TableObject3<T1, T2, T3>) super.tail();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject3<T1, T2, T3> tail(int index) {
		return (TableObject3<T1, T2, T3>) super.tail(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject3<T1, T2, T3> tail(String rowKey) {
		return (TableObject3<T1, T2, T3>) super.tail(rowKey);
	}
}
