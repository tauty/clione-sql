package tetz42.util;

import static tetz42.util.Util.*;

import java.lang.annotation.Annotation;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import tetz42.util.exception.InvalidParameterException;
import tetz42.util.exception.WrapException;

public class ReflectionUtil {

	private static final ConcurrentHashMap<ClazzWrapper, Field[]> fieldCache = newConcurrentMap();
	private static final ConcurrentHashMap<FieldWrapper, ConcurrentHashMap<ClazzWrapper, Annotation>> annotationCache = newConcurrentMap();

	protected static final Set<String> primitiveSet;
	static {
		HashSet<String> set = new HashSet<String>();
		set.add(boolean.class.getName());
		set.add(Boolean.class.getName());
		set.add(byte.class.getName());
		set.add(Byte.class.getName());
		set.add(short.class.getName());
		set.add(Short.class.getName());
		set.add(int.class.getName());
		set.add(Integer.class.getName());
		set.add(long.class.getName());
		set.add(Long.class.getName());
		set.add(float.class.getName());
		set.add(Float.class.getName());
		set.add(double.class.getName());
		set.add(Double.class.getName());
		set.add(BigInteger.class.getName());
		set.add(BigDecimal.class.getName());
		set.add(AtomicInteger.class.getName());
		set.add(AtomicLong.class.getName());
		set.add(Number.class.getName());
		set.add(String.class.getName());
		set.add(Object.class.getName());
		set.add(Class.class.getName());
		primitiveSet = Collections.unmodifiableSet(set);
	}

	public static boolean isPrimitive(Class<?> clazz) {
		return primitiveSet.contains(clazz.getName());
	}

	public static boolean isSingle(Object obj) {
		if (obj == null)
			return true;
		return isPrimitive(obj.getClass());
	}

	public static boolean isEachable(Object obj) {
		if (obj == null)
			return false;
		return isEachable(obj.getClass());
	}

	public static boolean isEachable(Class<?> clazz) {
		if (clazz.isArray())
			return true;
		for (Class<?> c : clazz.getInterfaces()) {
			// TODO consider if it should be 'Iterable' instead.
			if (c == Collection.class)
				return true;
		}
		return false;
	}

	public static boolean classOf(Class<?> child, Class<?> parent) {
		Class<?> clazz = child;
		do {
			if (clazz == parent)
				return true;
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		for (Class<?> c : child.getInterfaces()) {
			if (c == parent)
				return true;
		}
		return false;
	}

	public static boolean isStatic(Field f) {
		return Modifier.isStatic(f.getModifiers());
	}

	public static void setValue(Object receiver, Field field, Object value) {
		try {
			if(!field.isAccessible())
				field.setAccessible(true);
			field.set(receiver, value);
		} catch (IllegalArgumentException e) {
			throw new InvalidParameterException(e);
		} catch (IllegalAccessException e) {
			throw new WrapException(e);
		}
	}

	public static void setStringValue(Object receiver, Field field,
			String sValue) {
		Object value = str2primitive(field.getType(), sValue);
		if (value != null) {
			setValue(receiver, field, value);
		}
	}

	// TODO revise
	public static Object str2primitive(Class<?> clazz, String sValue) {
		if (clazz == String.class) {
			return sValue;
		} else if (clazz == Integer.class || clazz == int.class) {
			if (isEmpty(sValue))
				return clazz.isPrimitive() ? 0 : null;
			return Integer.parseInt(sValue);
		} else if (clazz == Long.class || clazz == long.class) {
			if (isEmpty(sValue))
				return clazz.isPrimitive() ? (long) 0 : null;
			return Long.parseLong(sValue);
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			return Boolean.parseBoolean(sValue);
		} else if (clazz == Byte.class || clazz == byte.class) {
			return Byte.parseByte(sValue);
		} else if (clazz == Short.class || clazz == short.class) {
			return Short.parseShort(sValue);
		} else if (clazz == Float.class || clazz == float.class) {
			return Float.parseFloat(sValue);
		} else if (clazz == Double.class || clazz == double.class) {
			return Double.parseDouble(sValue);
		} else if (clazz == BigInteger.class) {
			// TODO is it OK?
			return BigInteger.valueOf(Long.parseLong(sValue));
		} else if (clazz == BigDecimal.class) {
			// TODO is it OK?
			return BigDecimal.valueOf(Double.parseDouble(sValue));
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object receiver, Field field) {
		try {
			if(!field.isAccessible())
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
			throw new WrapException(clazz.getSimpleName()
					+ " might have security problem.", e);
		}
	}

	public static List<Field> getFields(Class<?> clazz){
		return avoidDuplication(getAllFields(clazz));
	}

	public static List<Field> avoidDuplication(List<Field> fList) {
		ArrayList<Field> resList = new ArrayList<Field>();
		HashSet<String> set = new HashSet<String>();
		for (Field f : fList) {
			if (!set.contains(f.getName())) {
				resList.add(f);
				set.add(f.getName());
			}
		}
		return resList;
	}

	public static List<Field> getAllFields(Class<?> clazz) {
		return getAllFields(clazz, new ArrayList<Field>());
	}

	private static List<Field> getAllFields(Class<?> clazz, List<Field> fList) {
		if (clazz == null || clazz == Object.class)
			return fList;
		for (Field f : getDeclaredFields(clazz)) {
			if (isStatic(f))
				continue;
			fList.add(f);
		}
		return getAllFields(clazz.getSuperclass(), fList);
	}

	public static Field[] getDeclaredFields(final Class<?> clazz) {
		return getOrNew(fieldCache, new ClazzWrapper(clazz),
				new Function<Field[]>() {
					@Override
					public Field[] apply() {
						return clazz.getDeclaredFields();
					}
				});
	}

	public static <A extends Annotation> A getAnnotation(final Field field,
			final Class<A> annoType) {
		ConcurrentHashMap<ClazzWrapper, Annotation> annoMap = getOrNew(
				annotationCache, new FieldWrapper(field),
				new Function<ConcurrentHashMap<ClazzWrapper, Annotation>>() {
					@Override
					public ConcurrentHashMap<ClazzWrapper, Annotation> apply() {
						return newConcurrentMap();
					}
				});
		Annotation annotation = getOrNew(annoMap, new ClazzWrapper(annoType),
				new Function<Annotation>() {
					@Override
					public Annotation apply() {
						return field.getAnnotation(annoType);
					}
				});
		return annoType.cast(annotation);
	}

	public static <K, V> V getOrNew(ConcurrentHashMap<K, V> map, K key,
			Function<V> newInstance) {
		V value = map.get(key);
		if (value == null) {
			value = newInstance.apply();
			if (value != null) {
				V putted = map.putIfAbsent(key, value);
				if (putted != null)
					value = putted;
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	private static <T> T newPrimitive(Class<T> clazz) {
		if (clazz == Boolean.class || clazz == boolean.class)
			return (T) Boolean.FALSE;
		else if (clazz == Byte.class || clazz == byte.class)
			return (T) new Byte((byte) 0);
		else if (clazz == Short.class || clazz == short.class)
			return (T) new Short((short) 0);
		else if (clazz == Integer.class || clazz == int.class)
			return (T) new Integer(0);
		else if (clazz == Long.class || clazz == long.class)
			return (T) new Long(0);
		else if (clazz == Float.class || clazz == float.class)
			return (T) new Float(0);
		else if (clazz == Double.class || clazz == double.class)
			return (T) new Double(0);
		else if (clazz == BigInteger.class)
			return (T) BigInteger.ZERO;
		else if (clazz == BigDecimal.class)
			return (T) BigDecimal.ZERO;
		else
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
