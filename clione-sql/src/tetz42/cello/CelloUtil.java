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

public class CelloUtil {

	public static final int UNDEFINED = ICell.UNDEFINED;
	public static final String ROOT = null;
	public static final String CRLF = "\r\n";

	protected static final Set<String> primitiveSet;
	static {
		HashSet<String> set = new HashSet<String>();
		set.add(Boolean.class.getName());
		set.add(Byte.class.getName());
		set.add(Short.class.getName());
		set.add(Integer.class.getName());
		set.add(Long.class.getName());
		set.add(Float.class.getName());
		set.add(Double.class.getName());
		set.add(BigInteger.class.getName());
		set.add(BigDecimal.class.getName());
		set.add(AtomicInteger.class.getName());
		set.add(AtomicLong.class.getName());
		set.add(Number.class.getName());
		set.add(String.class.getName());
		set.add(Object.class.getName());
		primitiveSet = Collections.unmodifiableSet(set);
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

	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object receiver, String fieldName) {
		return (T) getValue(receiver, getField(receiver, fieldName));
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
			T obj = newPrimitive(clazz);
			if(obj != null)
				return obj;
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
	private static <T> T newPrimitive(Class<T> clazz) {
		if (clazz == Boolean.class)
			return (T) Boolean.FALSE;
		else if (clazz == Byte.class)
			return (T) new Byte((byte) 0);
		else if (clazz == Short.class)
			return (T) new Short((short) 0);
		else if (clazz == Integer.class)
			return (T) new Integer(0);
		else if (clazz == Long.class)
			return (T) new Long(0);
		else if (clazz == Float.class)
			return (T) new Float(0);
		else if (clazz == Double.class)
			return (T) new Double(0);
		else if (clazz == BigInteger.class)
			return (T) BigInteger.ZERO;
		else if (clazz == BigDecimal.class)
			return (T) BigDecimal.ZERO;
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getOrNewValue(Object receiver, Class<?> fieldClass,
			Field field) {
		T value = (T) getValue(receiver, field);
		if (value == null) {
			value = (T) newInstance(fieldClass);
			setValue(receiver, field, value);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getOrNewValue(Object receiver, Field field) {
		return (T) getOrNewValue(receiver, field.getType(), field);
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

	public static String join(String[] args, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			if (sb.length() != 0)
				sb.append(delimiter);
			sb.append(arg);
		}
		return sb.toString();
	}

}
