package tetz42.util.tableobject.tables;

import java.util.List;

import tetz42.util.tableobject.Column;

public class TableObject3<T1, T2, T3> extends TableObject2<T1, T2> {

	private final Class<T3> cls3;

	public TableObject3(Class<T1> cls1, Class<T2> cls2, Class<T3> cls3) {
		super(cls1, cls2);
		this.cls3 = cls3;
		setHeaderDepth(this.cls3);
	}

	public void setHeaderAs3(String... keys) {
		for (String key : keys)
			context.headerClsMap.put(key, new HeaderInfo(cls3));
	}

	public void setHeaderAs3(String key, int width) {
		context.headerClsMap.put(key, new HeaderInfo(cls3, width));
	}

	public Column<T3> getAs3(String key) {
		return currentRow.get(cls3, key);
	}

	public List<Column<T3>> columns3() {
		return currentRow.columnList(cls3);
	}

	public List<Column<T3>> columns3(String... keys) {
		return columns(cls3, keys);
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
