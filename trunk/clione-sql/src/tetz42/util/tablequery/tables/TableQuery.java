package tetz42.util.tablequery.tables;

import tetz42.util.tablequery.Context;
import tetz42.util.tablequery.RowHolder;

public class TableQuery<T> {

	private final Class<T> clazz;
	private final Context context;
	private final RowHolder<T> rowHolder;

	public TableQuery(Class<T> clazz) {
		this.clazz = clazz;
		this.context = new Context(clazz);
		this.rowHolder = new RowHolder<T>(clazz, context);
	}

}
