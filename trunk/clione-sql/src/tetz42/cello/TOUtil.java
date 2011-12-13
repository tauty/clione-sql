package tetz42.cello;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import tetz42.util.exception.InvalidParameterException;
import tetz42.util.exception.WrapException;

public class TOUtil {

	public static final int UNDEFINED = -1;
	public static final String ROOT = null;

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

	public static boolean isPrimitive(Class<?> clazz) {
		return primitiveSet.contains(clazz.getName());
	}

	public static boolean isPrimitive(Object obj) {
		if (obj == null)
			return true;
		return isPrimitive(obj.getClass());
	}

	public static int max(int x, int y) {
		return x > y ? x : y;
	}

	public static boolean isEmpty(Object o) {
		if (o == null)
			return true;
		if (o instanceof CharSequence) {
			CharSequence s = (CharSequence) o;
			return s.length() == 0;
		} else if (o.getClass().isArray()) {
			return Array.getLength(o) == 0;
		} else if (o instanceof Collection<?>) {
			Collection<?> c = (Collection<?>) o;
			return c.size() == 0;
		} else if (o instanceof Map<?, ?>) {
			Map<?, ?> m = (Map<?, ?>) o;
			return m.size() == 0;
		}
		return false;
	}

	public static boolean isStatic(Field f) {
		return Modifier.isStatic(f.getModifiers());
	}

	public static <T> void setValue(Object receiver, Field field, T value) {
		try {
			field.setAccessible(true);
			field.set(receiver, value);
		} catch (IllegalArgumentException e) {
			throw new InvalidParameterException(e);
		} catch (IllegalAccessException e) {
			throw new WrapException(e);
		}
	}

	public static Field getField(Object receiver, String fieldName) {
		try {
			return receiver.getClass().getDeclaredField(fieldName);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	public static <T> T getValue(Object receiver, String fieldName) {
		return getValue(receiver, getField(receiver, fieldName));
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object receiver, Field field) {
		try {
			field.setAccessible(true);
			return (T) field.get(receiver);
		} catch (IllegalArgumentException e) {
			throw new InvalidParameterException(e);
		} catch (IllegalAccessException e) {
			throw new WrapException(e);
		}
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			// return clazz.newInstance();
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (NoSuchMethodException e) {
			throw new InvalidParameterException("The class, " + clazz.getName()
					+ ", must have default constructor.", e);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getOrNewValue(Object receiver, Class<?> fieldClass,
			Field field) {
		T value = getValue(receiver, field);
		if (value == null) {
			value = (T) newInstance(fieldClass);
			setValue(receiver, field, value);
		}
		return value;
	}

	public static <T> T getOrNewValue(Object receiver, Field field) {
		return getOrNewValue(receiver, field.getType(), field);
	}

	public static <T> List<T> getListOnMap(RecursiveMap<List<T>> hcellMap) {
		if (hcellMap.getValue() == null) {
			hcellMap.setValue(new ArrayList<T>());
		}
		return hcellMap.getValue();
	}

	public static <T> T getFromList(List<T> list) {
		if (list.size() == 0)
			return null;
		return list.get(0);
	}
}
