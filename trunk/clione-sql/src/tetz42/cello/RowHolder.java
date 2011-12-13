package tetz42.cello;

import java.util.ArrayList;
import java.util.List;

import tetz42.cello.contents.Row;

public class RowHolder<T> {

	private final Class<T> clazz;
	private final Context context;

	private final List<Row> rowList = new ArrayList<Row>();

	public RowHolder(Class<T> clazz, Context context) {
		this.clazz = clazz;
		this.context = context;
	}


}
