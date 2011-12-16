package tetz42.cello;

public interface ICell {

	String HEADER_STYLE = "HEADER";
	String CELL_STYLE = "CELL";

	String getValue();

	String getStyle();

	int getWidth();

	int getX();

	int getY();

	boolean isSkipped();
}
