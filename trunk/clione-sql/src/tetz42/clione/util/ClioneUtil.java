package tetz42.clione.util;

import static tetz42.util.Util.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import tetz42.clione.exception.UnsupportedTypeException;
import tetz42.clione.io.IOUtil;
import tetz42.util.Const;

public class ClioneUtil {

	public static final String CRLF = Const.CRLF;

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

	// TODO better solution.
	public static boolean isSQLType(Class<?> clazz) {
		return clazz == String.class || clazz == Boolean.class
				|| clazz == Boolean.TYPE || clazz == Short.class
				|| clazz == Short.TYPE || clazz == Integer.class
				|| clazz == Integer.TYPE || clazz == Long.class
				|| clazz == Long.TYPE || clazz == Float.class
				|| clazz == Float.TYPE || clazz == Double.class
				|| clazz == Double.TYPE || clazz == BigDecimal.class
				|| clazz == Date.class || clazz == java.sql.Date.class
				|| (clazz.isArray() && clazz.getComponentType() == Byte.TYPE);
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

		// String
		if (clazz == String.class) {
			return rs.getString(columnIndex);
		}

		// boolean
		if (clazz == Boolean.class) {
			if (rs.getObject(columnIndex) == null)
				return null;
			return rs.getBoolean(columnIndex);
		} else if (clazz == Boolean.TYPE) {
			return rs.getBoolean(columnIndex);
		}

		// short
		if (clazz == Short.class) {
			if (rs.getObject(columnIndex) == null)
				return null;
			return rs.getShort(columnIndex);
		} else if (clazz == Short.TYPE) {
			return rs.getShort(columnIndex);
		}

		// integer
		if (clazz == Integer.class) {
			if (rs.getObject(columnIndex) == null)
				return null;
			return rs.getInt(columnIndex);
		} else if (clazz == Integer.TYPE) {
			return rs.getInt(columnIndex);
		}

		// long
		if (clazz == Long.class) {
			if (rs.getObject(columnIndex) == null)
				return null;
			return rs.getLong(columnIndex);
		} else if (clazz == Long.TYPE) {
			return rs.getLong(columnIndex);
		}

		// float
		if (clazz == Float.class) {
			if (rs.getObject(columnIndex) == null)
				return null;
			return rs.getFloat(columnIndex);
		} else if (clazz == Float.TYPE) {
			return rs.getFloat(columnIndex);
		}

		// double
		if (clazz == Double.class) {
			if (rs.getObject(columnIndex) == null)
				return null;
			return rs.getDouble(columnIndex);
		} else if (clazz == Double.TYPE) {
			return rs.getDouble(columnIndex);
		}

		// BigDecimal
		if (clazz == BigDecimal.class) {
			return rs.getBigDecimal(columnIndex);
		}

		// Date
		if (clazz == Date.class) {
			if (rs.getObject(columnIndex) == null)
				return null;
			return new Date(rs.getTimestamp(columnIndex).getTime());
		} else if (clazz == java.sql.Date.class) {
			return rs.getDate(columnIndex);
		}

		// Timestamp
		if (clazz == Timestamp.class) {
			return rs.getTimestamp(columnIndex);
		}

		// byte[]
		if (clazz.isArray() && clazz.getComponentType() == Byte.TYPE) {
			if (rs.getObject(columnIndex) == null)
				return null;
			return IOUtil.loadFromStream(rs.getBinaryStream(columnIndex));
		}

		throw new UnsupportedTypeException("The type(" + clazz.getName()
				+ ") is not supported.");
	}

	private static final Pattern ptn = Pattern.compile("([%_#\\[％＿])");

	public static String escapeBySharp(String src) {
		return src == null ? null : ptn.matcher(src).replaceAll("#$1");
	}

	public static String escapeBySharp(Object obj) {
		return obj == null ? null : escapeBySharp(String.valueOf(obj));
	}
}
