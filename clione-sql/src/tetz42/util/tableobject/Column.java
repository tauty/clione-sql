package tetz42.util.tableobject;

import static tetz42.util.tableobject.TOUtil.*;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import tetz42.util.exception.WrapException;
import tetz42.util.tableobject.tables.TableObject1.ContextValues;
import tetz42.util.tableobject.tables.TableObject1.HeaderInfo;

public class Column<T> {

	private final Class<T> cls;
	private final ContextValues context;
	private String key;
	private T value;
	private boolean isSkip = false;
	private int x = 0;
	private int y = 0;
	private int width = HeaderInfo.UNDEFINED;

	public Column(Class<T> cls, String key, ContextValues context) {
		this.cls = cls;
		this.key = key;
		this.context = context;
		if (context.headerClsMap != null) {
			HeaderInfo headerInfo = context.headerClsMap.get(key);
			if (headerInfo != null) {
				setWidth(headerInfo.width);
			}
		}
	}

	public void set(T value) {
		this.value = value;
	}

	public T get() {
		if (this.value == null) {
			try {
				this.value = this.cls.newInstance();
			} catch (Exception e) {
				throw new WrapException(e);
			}
		}
		return this.value;
	}

	public String getKey() {
		return key;
	}

	public String getKeyAlias() {
		if (context.aliasMap == null || !context.aliasMap.containsKey(key))
			return key;
		return context.aliasMap.get(key);
	}

	public Column<T> skip() {
		isSkip = true;
		return this;
	}

	public boolean isSkip() {
		return isSkip;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return this.width;
	}

	private void setWidth(int width) {
		if (width == HeaderInfo.UNDEFINED)
			this.width = HeaderInfo.UNDEFINED;
		else
			this.width = width * 256;
	}

	@SuppressWarnings("unchecked")
	public void add(T another) {
		if (another == null) {
			return;
		} else if (value == null) {
			this.value = another;
		} else if (cls == Integer.class) {
			int iValue = (Integer) value;
			int iAnother = (Integer) another;
			value = (T) new Integer(iValue + iAnother);
		} else {
			value = (T) ("" + value + another);
		}
	}

	public Iterable<Column<String>> each(final int level, final boolean isAll) {
		final int levelMargin = context.displayHeaders.length - level;

		return new Iterable<Column<String>>() {

			@Override
			public Iterator<Column<String>> iterator() {
				// if (primitiveSet.contains(cls.getName())) {
				if (isPrimitive(cls)) {
					return new Iterator<Column<String>>() {

						boolean returned = false;

						@Override
						public boolean hasNext() {
							return !returned;
						}

						@Override
						public Column<String> next() {
							Column<String> column = new Column<String>(
									String.class, key, context);
							if (!isAll) {
								column.skip();
							} else {
								if (value == null)
									column.set("");
								else
									column.set("" + value);
								column.y = levelMargin;
							}
							returned = true;
							return column;
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException(
									"'remove' is not supported.");
						}
					};
				} else {

					return new Iterator<Column<String>>() {

						int index = 0;
						List<Field> fields = context.validFields(cls);

						@Override
						public boolean hasNext() {
							return index < fields.size();
						}

						@Override
						public Column<String> next() {
							Column<String> column = new Column<String>(
									String.class, key, context);
							// TODO temporary implementation. fix below.
							if (level == 1) {
								if (index == 0) {
									column.x = fields.size() - 1;
									column.setWidth(context.classWidth(cls));
								} else {
									column.skip();
								}
							} else {
								Field f = fields.get(index);
								column.key = context.fieldTitle(f);
								column.setWidth(context.fieldWidth(f));
								try {
									if (value == null)
										column.set("");
									else
										column.set("" + f.get(value));
								} catch (Exception e) {
									throw new WrapException(e);
								}
							}
							index++;
							return column;
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException(
									"'remove' is not supported.");
						}
					};
				}
			}
		};
	}
}
