package tetz42.util.tableobject;

import static tetz42.util.tableobject.TOUtil.*;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import tetz42.util.exception.WrapException;
import tetz42.util.tableobject.annotation.ColumnDef;
import tetz42.util.tableobject.tables.TableObject1.ContextValues;
import tetz42.util.tableobject.tables.TableObject1.HeaderInfo;

public class Column<T> {

	private final Class<T> cls;
	private final Map<String, String> aliasMap;
	private String key;
	private T value;
	private boolean isSkip = false;
	private int x = 0;
	private int y = 0;
	private int width = HeaderInfo.UNDEFINED;

	public Column(Class<T> cls, String key) {
		this(cls, key, null, null);
	}

	public Column(Class<T> cls, String key,
			LinkedHashMap<String, HeaderInfo> headerClsMap,
			Map<String, String> aliasMap) {
		this.cls = cls;
		this.key = key;
		if (headerClsMap != null) {
			HeaderInfo headerInfo = headerClsMap.get(key);
			if (headerInfo != null) {
				setWidth(headerInfo.width);
			}
		}
		this.aliasMap = aliasMap;
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
		if (aliasMap == null || !aliasMap.containsKey(key))
			return key;
		return aliasMap.get(key);
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

	public Iterable<Column<String>> each(final ContextValues context,
			final int level, final boolean isAll) {
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
									String.class, key, context.headerClsMap,
									aliasMap);
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
						Field[] fields = cls.getDeclaredFields();

						@Override
						public boolean hasNext() {
							return index < fields.length;
						}

						@Override
						public Column<String> next() {
							Column<String> column = new Column<String>(
									String.class, key, context.headerClsMap,
									aliasMap);
							// TODO temporary implementation. fix below.
							if (level == 1) {
								if (index == 0)
									column.x = fields.length - 1;
								else
									column.skip();
							} else {
								String key = fields[index].getName();
								ColumnDef def = fields[index]
										.getAnnotation(ColumnDef.class);
								if (def != null) {
									key = def.title();
									column.setWidth(def.width());
								}
								column.key = key;
								try {
									if (value == null)
										column.set("");
									else
										column.set(""
												+ fields[index].get(value));
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
