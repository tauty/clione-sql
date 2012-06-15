package tetz42.clione.conversion;

import static tetz42.util.ReflectionUtil.*;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import tetz42.clione.exception.UnsupportedTypeException;

public class ConvUtil {

	private static final Map<Class<?>, IConv> convMap4FinalClass;
	private static final Map<Class<?>, IConv> convMap4NormalClass;

	private static final IConv byteArrayConv = new ByteArrayConv();
	private static final IConv defaultConv = new DefaultConv();
	static {
		Map<Class<?>, IConv> map = new HashMap<Class<?>, IConv>();
		map.put(String.class, new StringConv());
		map.put(Boolean.class, new BooleanConv());
		map.put(Boolean.TYPE, new BooleanPrimitiveConv());
		map.put(Byte.class, new ByteConv());
		map.put(Byte.TYPE, new BytePrimitiveConv());
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
		convMap4FinalClass = Collections.unmodifiableMap(map);

		map = new LinkedHashMap<Class<?>, IConv>();
		map.put(Array.class, new ArrayConv());
		map.put(BigDecimal.class, new BigDecimaiConv());
		map.put(BigInteger.class, new BigIntegerConv());
		map.put(Blob.class, new BlobConv());
		map.put(NClob.class, new NClobConv());
		map.put(Clob.class, new ClobConv());
		map.put(Date.class, new DateConv());
		map.put(java.sql.Date.class, new SqlDateConv());
		map.put(InputStream.class, new InputStreamConv());
		map.put(Reader.class, new ReaderConv());
		map.put(Timestamp.class, new TimestampConv());
		convMap4NormalClass = Collections.unmodifiableMap(map);
	}

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

	public static IConv conv4Get(Class<?> clazz) {
		IConv conv = convMap4FinalClass.get(clazz);
		if (conv != null)
			return conv;
		if (clazz.isArray() && clazz.getComponentType() == Byte.TYPE)
			return byteArrayConv;
		conv = convMap4NormalClass.get(clazz);
		if (conv != null)
			return conv;
		throw new UnsupportedTypeException("The type(" + clazz.getName()
				+ ") is not supported.");
	}

	public static IConv conv4Set(Class<?> clazz) {
		IConv conv = convMap4FinalClass.get(clazz);
		if (conv != null)
			return conv;
		if (clazz.isArray() && clazz.getComponentType() == Byte.TYPE)
			return byteArrayConv;
		for (Entry<Class<?>, IConv> e : convMap4NormalClass.entrySet()) {
			if (classOf(clazz, e.getKey()))
				return e.getValue();
		}
		return defaultConv;
	}
}
