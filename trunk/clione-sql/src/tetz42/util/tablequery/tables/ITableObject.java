package tetz42.util.tablequery.tables;

import java.util.List;

import tetz42.util.tablequery.Column;
import tetz42.util.tablequery.Row;

public interface ITableObject {

	void setAlias(String name, String alias);

	String getAlias(String name);

	Iterable<Column<String>> headers(int level);

	Iterable<Column<String>> headers();

	List<Row> rows();

	int headerDepth();
}