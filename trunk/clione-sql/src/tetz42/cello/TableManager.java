package tetz42.cello;

import static tetz42.cello.CelloUtil.*;

import java.util.ArrayList;
import java.util.List;

import tetz42.cello.contents.Row;
import tetz42.cello.header.Header;

public class TableManager<T> implements ITableManager {

	public static <T> TableManager<T> create(Class<T> clazz) {
		return new TableManager<T>(clazz);
	}

	private final Header<T> header;
	private final Context<T> context;
	private final RowHolder<T> rowHolder;
	private final RowHolder<T> tailHolder;

	private TableManager(Class<T> clazz) {
		this.header = new Header<T>(clazz);
		this.context = header.getContext();
		this.rowHolder = new RowHolder<T>(clazz, context);
		this.tailHolder = new RowHolder<T>(clazz, context);
	}

	public RowHolder<T> rows() {
		return this.rowHolder;
	}

	public RowHolder<T> tails() {
		return this.tailHolder;
	}

	public Row<T> newRow() {
		return rows().newRow();
	}

	public Row<T> newRow(String key) {
		return rows().newRow(key);
	}

	public Row<T> getRow() {
		return rows().getRow();
	}

	public Row<T> getRow(String key) {
		return rows().getRow(key);
	}

	public Row<T> row() {
		return rows().row();
	}

	public Row<T> row(String key) {
		return rows().row(key);
	}

	public Row<T> tail() {
		return tails().row();
	}

	public Row<T> tail(String key) {
		return tails().row(key);
	}

	public void setCurrentRowAs(String key) {
		rows().setCurrentRowAs(key);
	}

	public void putConversion(String convertFrom, String convertTo) {
		putConversion("", convertFrom, convertTo);
	}

	public void putConversion(String schema, String convertFrom,
			String convertTo) {
		this.context.putConversion(schema, convertFrom, convertTo);
	}

	public Row<T> def() {
		return this.context.getRowDef();
	}

	@Override
	public IHeader header() {
		return this.header;
	}

	@Override
	public List<IRow> eachRow() {
		List<IRow> rows = new ArrayList<IRow>();
		rows.addAll(rowHolder.getRowList());
		rows.addAll(tailHolder.getRowList());
		return rows;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterable<ICell> it : header().each()) {
			appendCells(sb, it);
		}
		for (IRow row : eachRow()) {
			appendCells(sb, row.each());
		}
		return sb.toString();
	}

	private void appendCells(StringBuilder sb, Iterable<ICell> it) {
		for (ICell cell : it) {
			if (!cell.isSkipped()) {
				sb.append(cell.getValue()).append("(Style:").append(
						cell.getStyle()).append(")");
			}
			sb.append("\t");
		}
		sb.deleteCharAt(sb.length() - 1).append(CRLF);
	}

}
