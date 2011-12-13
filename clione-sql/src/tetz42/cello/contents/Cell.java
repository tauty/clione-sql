package tetz42.cello.contents;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;

import tetz42.cello.Context;

public class Cell<T> {

	private final Object receiver;
	private final Field field;
	private final Context context;

	public Cell(Object receiver, Field field, Context context) {
		this.receiver = receiver;
		this.field = field;
		this.context = context;
	}

	public void set(T value) {
		setValue(this.receiver, this.field, value);
	}

	public T get() {
		return getOrNewValue(this.receiver, this.field);
	}
}
