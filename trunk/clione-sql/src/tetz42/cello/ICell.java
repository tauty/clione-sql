package tetz42.cello;

public interface ICell {

	int UNDEFINED = -1;

	String HEADER_STYLE = "HEADER";
	String CELL_STYLE = "CELL";

	String getValue();

	String getStyle();

	int getWidth();

	int getX();

	int getY();

	boolean isSkipped();
}
