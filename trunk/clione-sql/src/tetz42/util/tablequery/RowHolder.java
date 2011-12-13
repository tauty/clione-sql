package tetz42.util.tablequery;

import java.util.ArrayList;
import java.util.List;

public class RowHolder<T> {

	private final Class<T> clazz;
	private final Context<T> context;

	private final List<Row> rowList = new ArrayList<Row>();

	public RowHolder(Class<T> clazz, Context<T> context) {
		this.clazz = clazz;
		this.context = context;
	}


}
