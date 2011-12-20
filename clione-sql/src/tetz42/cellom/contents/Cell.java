package tetz42.cellom.contents;

import static tetz42.cellom.CelloUtil.*;

import java.lang.reflect.Field;

import tetz42.cellom.ICell;
import tetz42.cellom.annotation.Body;
import tetz42.cellom.annotation.EachBody;

public class Cell<T> implements ICell {

	private final Object receiver;
	private final Field field;
	private String style;
	private boolean isConverted;
	private String convertSchema;

	Cell(Object receiver, Field field) {
		this.receiver = receiver;
		this.field = field;
		Body cellDef = field.getAnnotation(Body.class);
		if (cellDef != null) {
			this.style = cellDef.style();
			this.isConverted = cellDef.convert();
			this.convertSchema = cellDef.convertSchema();
		} else {
			setDefaultValue();
		}
	}

	Cell(CelloMap<T> cumap, EachBody cellDef) {
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
		this.style = ICell.BODY_STYLE;
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
