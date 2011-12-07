package tetz42.util.tableobject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TOUtil {
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

}
