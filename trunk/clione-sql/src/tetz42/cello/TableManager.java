package tetz42.cello;

import tetz42.cello.header.Header;


public class TableManager<T> {

	public static<T> TableManager<T> create(Class<T> clazz){
		return new TableManager<T>(clazz);
	}

	private final Class<T> clazz;
	private final RowHolder rowHolder;
	private final Header<T> header;

	public TableManager(Class<T> clazz) {
		this.clazz = clazz;
		this.header = new Header<T>(clazz);
		this.rowHolder = new RowHolder<T>(clazz, this.header.getContext());
	}

}
