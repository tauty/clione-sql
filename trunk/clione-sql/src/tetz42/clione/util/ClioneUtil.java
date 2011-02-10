package tetz42.clione.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import tetz42.clione.exception.UnsupportedTypeException;
import tetz42.clione.io.IOUtil;

public class ClioneUtil {

	public static Object getSQLData(Field f, ResultSet rs, int columnIndex)
			throws SQLException {

		Class<?> clazz = f.getType();

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
				+ ") is not supported. Field name:" + f.toGenericString()
				+ ", Class name:" + f.getDeclaringClass().getName());
	}
}
