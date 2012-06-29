package tetz42.clione.util;

import static tetz42.util.ReflectionUtil.*;
import static tetz42.util.Util.*;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tetz42.clione.exception.UnsupportedTypeException;
import tetz42.clione.util.converter.ArrayConv;
import tetz42.clione.util.converter.BigDecimaiConv;
import tetz42.clione.util.converter.BigIntegerConv;
import tetz42.clione.util.converter.BlobConv;
import tetz42.clione.util.converter.BooleanConv;
import tetz42.clione.util.converter.BooleanPrimitiveConv;
import tetz42.clione.util.converter.ByteArrayConv;
import tetz42.clione.util.converter.ByteConv;
import tetz42.clione.util.converter.BytePrimitiveConv;
import tetz42.clione.util.converter.ClobConv;
import tetz42.clione.util.converter.DateConv;
import tetz42.clione.util.converter.DefaultConv;
import tetz42.clione.util.converter.DoubleConv;
import tetz42.clione.util.converter.DoublePrimitiveConv;
import tetz42.clione.util.converter.FloatConv;
import tetz42.clione.util.converter.FloatPrimitiveConv;
import tetz42.clione.util.converter.IConv;
import tetz42.clione.util.converter.InputStreamConv;
import tetz42.clione.util.converter.IntConv;
import tetz42.clione.util.converter.IntegerConv;
import tetz42.clione.util.converter.LongConv;
import tetz42.clione.util.converter.LongPrimitiveConv;
import tetz42.clione.util.converter.NClobConv;
import tetz42.clione.util.converter.ReaderConv;
import tetz42.clione.util.converter.RefConv;
import tetz42.clione.util.converter.SQLXMLConv;
import tetz42.clione.util.converter.ShortConv;
import tetz42.clione.util.converter.ShortPrimitiveConv;
import tetz42.clione.util.converter.SqlDateConv;
import tetz42.clione.util.converter.StringConv;
import tetz42.clione.util.converter.TimeConv;
import tetz42.clione.util.converter.TimestampConv;
import tetz42.clione.util.converter.URLConv;
import tetz42.util.Const;

public class ClioneUtil {

	public static final String CRLF = Const.CRLF;

	private static final Map<Class<?>, IConv> convMap4FinalClass;
	private static final Map<Class<?>, IConv> convMap4NormalClass;

	private static final IConv byteArrayConv = new ByteArrayConv();
	private static final IConv defaultConv = new DefaultConv();
	static {
		Map<Class<?>, IConv> map = new IdentityHashMap<Class<?>, IConv>();
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
		map.put(URL.class, new URLConv());
		convMap4FinalClass = Collections.unmodifiableMap(map);

		map = new LinkedHashMap<Class<?>, IConv>();
		map.put(Timestamp.class, new TimestampConv());
		map.put(java.sql.Date.class, new SqlDateConv());
		map.put(Time.class, new TimeConv());
		map.put(Date.class, new DateConv());
		map.put(BigDecimal.class, new BigDecimaiConv());
		map.put(BigInteger.class, new BigIntegerConv());
		map.put(InputStream.class, new InputStreamConv());
		map.put(Reader.class, new ReaderConv());
		map.put(Blob.class, new BlobConv());
		map.put(NClob.class, new NClobConv());
		map.put(Clob.class, new ClobConv());
		map.put(Array.class, new ArrayConv());
		map.put(Ref.class, new RefConv());
		map.put(SQLXML.class, new SQLXMLConv());
		convMap4NormalClass = Collections.unmodifiableMap(map);
	}

	public static boolean isAllSpace(String s) {
		for (byte b : s.getBytes()) {
			if (b != ' ' && b != '\t')
				return false;
		}
		return true;
	}

	public static String nextStr(String src, int pos, int length) {
		if (src.length() < pos + length)
			return null;
		return src.substring(pos, pos + length);
	}

	public static String nextChar(String src, int pos) {
		return nextStr(src, pos, 1);
	}

	public static String genSQLInfo(String sql, List<Object> params,
			String resourceInfo) {
		return mkStringByCRLF("--- sql ---", sql, "--- params ---", params,
				"--- resource ---", resourceInfo);
	}

	public static boolean isSQLType(Class<?> clazz) {
		return convMap4FinalClass.get(clazz) != null ? true
				: convMap4NormalClass.get(clazz) != null ? true : (clazz
						.isArray() && clazz.getComponentType() == Byte.TYPE);
	}

	public static Object getSQLData(Field f, ResultSet rs, int columnIndex)
			throws SQLException {
		try {
			return getSQLData(f.getType(), rs, columnIndex);
		} catch (UnsupportedTypeException e) {
			throw new UnsupportedTypeException(e.getMessage() + " Field name:"
					+ f.toGenericString() + ", Class name:"
					+ f.getDeclaringClass().getName());
		}
	}

	public static Object getSQLData(Class<?> clazz, ResultSet rs,
			int columnIndex) throws SQLException {
		return conv4Get(clazz).get(rs, columnIndex);
	}

	public static <T> void setSQLData(PreparedStatement stmt, Object param,
			int columnIndex) throws SQLException {
		if (param == null)
			defaultConv.set(stmt, param, columnIndex);
		else
			conv4Set(param.getClass()).set(stmt, param, columnIndex);
	}

	private static IConv conv4Get(Class<?> clazz) {
		IConv conv = convMap4FinalClass.get(clazz);
		if (conv != null)
			return conv;
		conv = convMap4NormalClass.get(clazz);
		if (conv != null)
			return conv;
		if (clazz.isArray() && clazz.getComponentType() == Byte.TYPE)
			return byteArrayConv;
		throw new UnsupportedTypeException("The type(" + clazz.getName()
				+ ") is not supported.");
	}

	private static IConv conv4Set(Class<?> clazz) {
		IConv conv = convMap4FinalClass.get(clazz);
		if (conv != null)
			return conv;
		conv = convMap4NormalClass.get(clazz);
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
