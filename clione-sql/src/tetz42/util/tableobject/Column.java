package tetz42.util.tableobject;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import tetz42.util.exception.WrapException;
import tetz42.util.tableobject.annotation.Title;

public class Column<T> {

	protected static final Set<String> primitiveSet;
	static {
		HashSet<String> map = new HashSet<String>();
		map.add(Object.class.getName());
		map.add(Class.class.getName());
		map.add(Boolean.class.getName());
		map.add(Character.class.getName());
		map.add(Number.class.getName());
		map.add(Byte.class.getName());
		map.add(Short.class.getName());
		map.add(Integer.class.getName());
		map.add(Long.class.getName());
		map.add(Float.class.getName());
		map.add(Double.class.getName());
		map.add(BigInteger.class.getName());
		map.add(BigDecimal.class.getName());
		map.add(AtomicInteger.class.getName());
		map.add(AtomicLong.class.getName());
		map.add(String.class.getName());
		primitiveSet = Collections.unmodifiableSet(map);
	}

	private final Class<T> cls;
	private final Map<String, String> aliasMap;
	private String key;
	private T value;
	private boolean isSkip = false;
	private int x = 0;
	private int y = 0;

	public Column(Class<T> cls, String key) {
		this(cls, key, null);
	}

	public Column(Class<T> cls, String key, Map<String, String> aliasMap) {
		this.cls = cls;
		this.key = key;
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
		if(aliasMap == null || aliasMap.containsKey(key))
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

	public Iterable<Column<String>> each(final Map<String, String> aliasMap, final int wholeLevel,
			final int curLevel, final boolean isAll) {

		final boolean isPrimitiveOut = isAll || curLevel == 1;
		final int levelMargin = wholeLevel - curLevel;

		return new Iterable<Column<String>>() {

			@Override
			public Iterator<Column<String>> iterator() {
				if (primitiveSet.contains(cls.getName())) {
					return new Iterator<Column<String>>() {

						boolean returned = false;

						@Override
						public boolean hasNext() {
							return !returned;
						}

						@Override
						public Column<String> next() {
							Column<String> column = new Column<String>(
									String.class, key, aliasMap);
							if (!isPrimitiveOut) {
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
									String.class, key, aliasMap);
							if (curLevel == 1) {
								if (index == 0)
									column.x = fields.length - 1;
								else
									column.skip();
							} else {
								String key = fields[index].getName();
								Title def = fields[index]
										.getAnnotation(Title.class);
								if (def != null)
									key = def.value();
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
