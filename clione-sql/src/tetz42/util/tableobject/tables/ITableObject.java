package tetz42.util.tableobject.tables;

import java.util.List;

import tetz42.util.tableobject.Column;
import tetz42.util.tableobject.Row;

public interface ITableObject {

	public abstract void setAlias(String name, String alias);

	public abstract String getAlias(String name);

	public abstract Iterable<Column<String>> headers(int level);

	public abstract Iterable<Column<String>> headersAll(int level);

	public abstract Iterable<Column<String>> headers();

	public abstract List<Row> rows();

}