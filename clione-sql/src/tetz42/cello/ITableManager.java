package tetz42.cello;

public interface ITableManager {

	IHeader header();

	Iterable<IRow> eachRow();
}