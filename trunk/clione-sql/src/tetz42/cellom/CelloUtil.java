package tetz42.cellom;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

	// TODO change below maps to soft reference CuncurrentHashMap.
	private static final HashMap<ClazzWrapper, Field[]> fieldCache = newMap();
	private static final HashMap<FieldWrapper, Map<ClazzWrapper, Annotation>> annotationCache = newMap();

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

	public static <K, V> HashMap<K, V> newMap() {
		return new HashMap<K, V>();
	}

	// TODO better algorithm with cache.
	public static Field getField(Class<?> clazz, String name) {
		for (Field f : getFields(clazz)) {
			if (f.getName().equals(name))
				return f;
		}
		return null;
	}

	public static Field[] getFields(Class<?> clazz) {
		ClazzWrapper key = new ClazzWrapper(clazz);
		Field[] fields = fieldCache.get(key);
		if (fields == null) {
			synchronized (fieldCache) {
				fields = clazz.getDeclaredFields();
				fieldCache.put(key, fields);
			}
		}
		return fields;
	}

	public static <A extends Annotation> A getAnnotation(Field field,
			Class<A> annoType) {
		FieldWrapper key = new FieldWrapper(field);
		Map<ClazzWrapper, Annotation> annoMap = annotationCache.get(key);
		if (annoMap == null) {
			synchronized (annotationCache) {
				annoMap = newMap();
				annotationCache.put(key, annoMap);
			}
		}
		ClazzWrapper annoKey = new ClazzWrapper(annoType);
		Annotation annotation = annoMap.get(annoKey);
		if (!annoMap.containsKey(annoKey)) {
			synchronized (annoMap) {
				annotation = field.getAnnotation(annoType);
				annoMap.put(annoKey, annotation);
			}
		}
		return annoType.cast(annotation);
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

	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
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
		Field f = getField(receiver.getClass(), fieldName);
		if (f == null)
			// TODO better exception
			throw new RuntimeException("Unknown field specified. Class:"
					+ receiver.getClass().getName() + ", Field:" + fieldName);
		return f;
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
			if (obj != null)
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

	public static <T> T head(List<T> list) {
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

	public static <T> T getKeyByPosition(LinkedHashMap<T, ?> map, int pos) {
		int i = 0;
		for (T key : map.keySet()) {
			if (key != null) {
				if (i == pos) {
					return key;
				} else {
					i++;
				}
			}
		}
		return null;
	}

	private static class ClazzWrapper {
		private final Class<?> clazz;

		ClazzWrapper(Class<?> clazz) {
			this.clazz = clazz;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ClazzWrapper) {
				ClazzWrapper dst = (ClazzWrapper) obj;
				return this.clazz == dst.clazz;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.clazz.getName().hashCode();
		}
	}

	private static class FieldWrapper {
		private final Field field;

		FieldWrapper(Field field) {
			this.field = field;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FieldWrapper) {
				FieldWrapper dst = (FieldWrapper) obj;
				return this.field.equals(dst.field);
			}
			return false;
		}

		@Override
		public int hashCode() {
			String fieldFullName = this.field.getDeclaringClass().getName()
					+ "#" + this.field.getName();
			return fieldFullName.hashCode();
		}
	}
}
