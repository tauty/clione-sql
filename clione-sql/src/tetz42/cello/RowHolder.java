package tetz42.cello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.cello.contents.Row;

public class RowHolder<T> {

	private final Class<T> clazz;
	private final Context<T> context;

	private final List<Row<T>> rowList = new ArrayList<Row<T>>();
	private final Map<String, Row<T>> rowMap = new HashMap<String, Row<T>>();

	private int index = 0;

	RowHolder(Class<T> clazz, Context<T> context) {
		this.clazz = clazz;
		this.context = context;
	}

	public Row<T> newRow() {
		rowList.add(new Row<T>(clazz, context));
		index = rowList.size() - 1;
		return getRow();
	}

	public Row<T> newRow(String key) {
		newRow();
		setCurrentRowAs(key);
		return getRow(key);
	}

	public Row<T> getRow() {
		if (index >= rowList.size())
			return null;
		return rowList.get(index);
	}

	public Row<T> getRow(String key) {
		return rowMap.get(key);
	}

	public Row<T> row(){
		Row<T> row = getRow();
		if(row == null)
			row = newRow();
		return row;
	}

	public Row<T> row(String key){
		Row<T> row = getRow(key);
		if(row == null)
			row = newRow(key);
		return row;
	}

	public void setCurrentRowAs(String key) {
		rowMap.put(key, getRow());
	}

	public int getCurrentIndex() {
		return index;
	}

	public void setCurrentIndex(int index) {
		this.index = index;
	}

	public int getSize() {
		return rowList.size();
	}

	List<Row<T>> getRowList() {
		return this.rowList;
	}
}
