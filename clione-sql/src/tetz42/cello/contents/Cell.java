package tetz42.cello.contents;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;

import tetz42.cello.ICell;
import tetz42.cello.annotation.CellDef;
import tetz42.cello.annotation.EachCellDef;

public class Cell<T> implements ICell {

	private final Object receiver;
	private final Field field;
	private String style;

	Cell(Object receiver, Field field) {
		this.receiver = receiver;
		this.field = field;
		CellDef cellDef = field.getAnnotation(CellDef.class);
		if (cellDef != null)
			this.style = cellDef.style();
	}

	public Cell(CellUnitMap<T> cumap, EachCellDef cellDef) {
		this.receiver = cumap;
		this.field = null;
		if (cellDef != null)
			this.style = cellDef.style();
	}

	public void set(T value) {
		setValue(this.receiver, this.field, value);
	}

	public T get() {
		return getOrNewValue(this.receiver, this.field);
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String getStyle() {
		return this.style;
	}

	@Override
	public String getValue() {
		return String.valueOf(get());
	}

	@Override
	public int getX() {
		return 1;
	}

	@Override
	public int getY() {
		return 1;
	}

	@Override
	public boolean isSkipped() {
		return false;
	}

	@Override
	public int getWidth() {
		return UNDEFINED;
	}
}
