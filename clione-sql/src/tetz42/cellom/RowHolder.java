package tetz42.cellom;

import static tetz42.util.Pair.*;
import static tetz42.util.Util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tetz42.cellom.body.Cell;
import tetz42.cellom.body.Row;
import tetz42.util.Pair;
import tetz42.util.exception.InvalidParameterException;

public class RowHolder<T> {

	private final Class<T> clazz;
	private final Context<T> context;

	private final List<Row<T>> rowList = newArrayList();
	private final HashMap<String, Pair<Row<T>, Integer>> rowMap = newHashMap();

	private int index = 0;
	private boolean isBody;

	RowHolder(Class<T> clazz, Context<T> context) {
		this(clazz, context, false);
	}

	RowHolder(Class<T> clazz, Context<T> context, boolean isBody) {
		this.clazz = clazz;
		this.context = context;
		this.isBody = isBody;
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

	public Row<T> getRow(int i) {
		if (i >= rowList.size())
			return null;
		return rowList.get(i);
	}

	public Row<T> getRow() {
		return getRow(index);
	}

	public Row<T> getRow(String key) {
		Pair<Row<T>, Integer> pair = rowMap.get(key);
		return pair == null ? null : pair.getFirst();
	}

	public Row<T> row() {
		Row<T> row = getRow();
		if (row == null)
			row = newRow();
		return row;
	}

	public Row<T> row(String key) {
		Pair<Row<T>, Integer> pair = rowMap.get(key);
		if (pair == null)
			return newRow(key);

		// set current row as the key indicated
		if(getRow(pair.getSecond()) == pair.getFirst())
			return pair.getFirst();
		this.index = rowList.indexOf(pair.getFirst());
		return pair.getFirst();
	}

	public void setCurrentRowAs(String key) {
		rowMap.put(key, pair(getRow(), index));
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

	public List<Row<T>> getRowList() {
		return this.rowList;
	}

	public <E> List<Cell<E>> getByQuery(
			@SuppressWarnings("unused") Class<E> clazz, String query) {
		return getByQuery(query);
	}

	public <E> List<Cell<E>> getByQuery(String query) {
		return getByQuery(Query.parse(query));
	}

	public <E> List<Cell<E>> getByQuery(
			@SuppressWarnings("unused") Class<E> clazz, Query query) {
		return getByQuery(query);
	}

	public <E> List<Cell<E>> getByQuery(Query query) {
		if (query.get(0) == null)
			throw new InvalidParameterException(
					"Query must have 1 more elements");
		List<Cell<E>> list = new ArrayList<Cell<E>>();
		for (String rowName : query.get(0)) {
			if (rowName.equals(Query.CURRENT_ROW) && isBody) {
				addMatchedCells(row(), query, list);
			} else if (rowName.equals(Query.ANY)) {
				for (Row<T> row : rowList)
					addMatchedCells(row, query, list);
			} else if (rowMap.containsKey(rowName)) {
				addMatchedCells(getRow(rowName), query, list);
			} else if (Query.numPtn.matcher(rowName).matches()) {
				int i = Integer.parseInt(rowName);
				if (i < rowList.size())
					addMatchedCells(rowList.get(i), query, list);
			} else {
				// TODO consider about this case.
				// throw new InvalidParameterException(
				// "Unknown row name has specified. name=" + rowName);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private <E> void addMatchedCells(Row<T> row, Query query, List<Cell<E>> list) {
		for (Cell<Object> cell : row.getByQuery(query)) {
			list.add((Cell<E>) cell);
		}
	}

	public void clear() {
		rowMap.clear();
		rowList.clear();
	}

	Row<T> remove(String key) {
		Pair<Row<T>, Integer> pair = rowMap.remove(key);
		if (pair == null)
			return null;
		if (getRow(pair.getSecond()) == pair.getFirst())
			rowList.remove(pair.getSecond());
		else
			rowList.remove(pair.getFirst());
		return pair.getFirst();
	}

	public void addRow(Row<T> row) {
		rowList.add(row);
		index = rowList.size() - 1;
	}

	public void addRows(List<Row<T>> rows) {
		for (Row<T> row : rows) {
			rowList.add(row);
		}
		index = rowList.size() - 1;
	}
}
