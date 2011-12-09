package tetz42.util.tablequery;

import java.util.ArrayList;
import java.util.List;

import tetz42.util.tablequery.tables.TableQuery;
import tetz42.util.tablequery.tables.TableQuery.ContextValues;

public class RowHolder<T> {

	private final Class<T> clazz;
	private final ContextValues context;

	private final List<Row> rowList = new ArrayList<Row>();

	public RowHolder(Class<T> clazz, ContextValues context) {
		this.clazz = clazz;
		this.context = context;
	}


}
