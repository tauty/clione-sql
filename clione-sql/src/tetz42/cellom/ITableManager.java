package tetz42.cellom;

public interface ITableManager {

	IHeader header();

	Iterable<IRow> eachRow();
}