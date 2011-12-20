package tetz42.cellom;

public interface ICell {

	int UNDEFINED = -1;

	String HEADER_STYLE = "HEADER";
	String BODY_STYLE = "BODY";

	String getValue();

	String getStyle();

	int getWidth();

	int getX();

	int getY();

	boolean isSkipped();
}
