package tetz42.util.tablequery;

import java.lang.reflect.Field;

import tetz42.util.exception.InvalidParameterException;
import tetz42.util.exception.WrapException;

public class Cell<T> {

	private final Object receiver;
	private final Field field;

	public Cell(Object receiver, Field field) {
		this.receiver = receiver;
		this.field = field;
	}

	public void set(T value) {
		try {
			this.field.set(receiver, value);
		} catch (IllegalArgumentException e) {
			throw new InvalidParameterException(e);
		} catch (IllegalAccessException e) {
			throw new WrapException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public T get() {
		try {
			T value = (T) this.field.get(receiver);
			if (value == null) {
				value = (T) this.field.getType().newInstance();
				set(value);
			}
			return value;
		} catch (IllegalArgumentException e) {
			throw new InvalidParameterException(e);
		} catch (IllegalAccessException e) {
			throw new WrapException(e);
		} catch (InstantiationException e) {
			throw new WrapException("The class, "
					+ this.field.getType().getName()
					+ ", must have public default constructor.", e);
		}
	}
}
