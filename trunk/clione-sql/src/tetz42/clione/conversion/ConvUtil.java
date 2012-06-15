package tetz42.clione.conversion;

import static tetz42.util.ReflectionUtil.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConvUtil {

	private static final Map<Class<?>, IConv> convMap;
	static {
		Map<Class<?>, IConv> map = new HashMap<Class<?>, IConv>();
		map.put(String.class, new StringConv());
		map.put(Boolean.class, new BooleanConv());
		map.put(Boolean.TYPE, new BooleanPrimitiveConv());
		map.put(Short.class, new ShortConv());
		map.put(Short.TYPE, new ShortPrimitiveConv());
		map.put(Integer.class, new IntegerConv());
		map.put(Integer.TYPE, new IntConv());
		map.put(Long.class, new LongConv());
		map.put(Long.TYPE, new LongPrimitiveConv());
		map.put(Float.class, new FloatConv());
		map.put(Float.TYPE, new FloatPrimitiveConv());
		map.put(Double.class, new DoubleConv());
		map.put(Double.TYPE, new DoublePrimitiveConv());
		convMap = Collections.unmodifiableMap(map);
	}
	private static IConv defaultConv = new DefaultConv();
	private static IConv decimalConv = new BigDecimaiConv();
	private static IConv integerConv = new BigIntegerConv();

	public static boolean isSQLType(Class<?> clazz) {
		return clazz == String.class || clazz == Boolean.class
		|| clazz == Boolean.TYPE || clazz == Short.class
		|| clazz == Short.TYPE || clazz == Integer.class
		|| clazz == Integer.TYPE || clazz == Long.class
		|| clazz == Long.TYPE || clazz == Float.class
		|| clazz == Float.TYPE || clazz == Double.class
		|| clazz == Double.TYPE || clazz == BigDecimal.class
		|| clazz == Date.class || clazz == java.sql.Date.class
		|| (clazz.isArray() && clazz.getComponentType() == Byte.TYPE)
		|| clazz == InputStream.class;
	}

	public static IConv getConv(Class<?> clazz) {
		IConv conv = convMap.get(clazz);
		if(conv != null)
			return conv;
		if(classOf(clazz, BigDecimal.class))
			return decimalConv;
		if(classOf(clazz, BigInteger.class))
			return integerConv;
		return defaultConv;
	}

}
