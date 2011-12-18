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
	private boolean isConverted;
	private String convertSchema;

	Cell(Object receiver, Field field) {
		this.receiver = receiver;
		this.field = field;
		CellDef cellDef = field.getAnnotation(CellDef.class);
		if (cellDef != null) {
			this.style = cellDef.style();
			this.isConverted = cellDef.convert();
			this.convertSchema = cellDef.convertSchema();
		} else {
			setDefaultValue();
		}
	}

	Cell(CelloMap<T> cumap, EachCellDef cellDef) {
		this.receiver = cumap;
		this.field = null;
		if (cellDef != null) {
			this.style = cellDef.style();
			this.isConverted = cellDef.convert();
			this.convertSchema = cellDef.convertSchema();
		} else {
			setDefaultValue();
		}
	}

	private void setDefaultValue() {
		this.style = ICell.CELL_STYLE;
		this.isConverted = false;
		this.convertSchema = "";
	}

	@SuppressWarnings("unchecked")
	public void add(T augend) {
		if (augend == null)
			return;
		T value = get();
		if (value instanceof Integer) {
			int iValue = (Integer) value;
			int iAugend = (Integer) augend;
			set((T) new Integer(iValue + iAugend));
		} else if (value instanceof String) {
			set((T) ("" + value + augend));
		} else {
			throw new UnsupportedOperationException("Cell<"
					+ value.getClass().getName()
					+ "> type does not support 'add' method.");
		}
	}

	public void set(T value) {
		setValue(this.receiver, this.field, value);
	}

	@SuppressWarnings("unchecked")
	public T get() {
		return (T) getOrNewValue(this.receiver, this.field);
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

	public void convert() {
		this.isConverted = true;
	}

	public boolean isConverted() {
		return isConverted;
	}

	public void setConvertSchema(String convertSchema) {
		this.convertSchema = convertSchema;
	}

	public String getConvertSchema() {
		return convertSchema;
	}
}
