package tetz42.util.tableobject.tables;

import java.util.List;

import tetz42.util.tableobject.Column;

public class TableObject2<T1, T2> extends TableObject1<T1> {
	private final Class<T2> cls2;

	public TableObject2(Class<T1> cls1, Class<T2> cls2) {
		super(cls1);
		this.cls2 = cls2;
		setHeaderDepth(this.cls2);
	}

	public void setHeaderAs2(String... keys) {
		for (String key : keys)
			context.headerClsMap.put(key, new HeaderInfo(cls2));
	}

	public void setHeaderAs2(String key, int width) {
		context.headerClsMap.put(key, new HeaderInfo(cls2, width));
	}

	public Column<T2> getAs2(String key) {
		return currentRow.get(cls2, key);
	}

	public List<Column<T2>> columns2() {
		return currentRow.columnList(cls2);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject2<T1, T2> row(int index) {
		return (TableObject2<T1, T2>) super.row(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject2<T1, T2> row(String rowKey) {
		return (TableObject2<T1, T2>) super.row(rowKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject2<T1, T2> tail() {
		return (TableObject2<T1, T2>) super.tail();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject2<T1, T2> tail(int index) {
		return (TableObject2<T1, T2>) super.tail(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableObject2<T1, T2> tail(String rowKey) {
		return (TableObject2<T1, T2>) super.tail(rowKey);
	}

}
