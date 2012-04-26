package tetz42.clione;

import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.util.ReflectionUtil.*;
import static tetz42.util.Util.*;

import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tetz42.clione.util.ResultMap;
import tetz42.util.exception.SQLRuntimeException;

public class SQLIterator<T> implements Iterable<T> {

	static <T> SQLIterator<T> genIterator(SQLExecutor executor, Class<T> clazz,
			Map<String, Object> paramMap) throws SQLException {
		return new SQLIterator<T>(executor, clazz, paramMap);
	}

	private final Class<T> clazz;
	private final SQLExecutor executor;
	private final ResultSetMetaData md;
	private final HashMap<String, Field> fieldMap;

	public SQLIterator(SQLExecutor executor, Class<T> clazz,
			Map<String, Object> paramMap) {
		this.executor = executor;
		this.clazz = clazz;
		try {
			executor.stmt = executor.genStmt(paramMap);
			executor.rs = executor.stmt.executeQuery();
			this.md = executor.rs.getMetaData();
		} catch (SQLException e) {
			throw new SQLRuntimeException(mkStringByCRLF(e.getMessage(),
					executor.getSQLInfo()), e);
		}

		fieldMap = new HashMap<String, Field>();
		for (Field field : getFields(clazz)) {
			fieldMap.put(field.getName(), field);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				try {
					return executor.rs.next();
				} catch (SQLException e) {
					throw new SQLRuntimeException(mkStringByCRLF(
							e.getMessage(), executor.getSQLInfo()), e);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public T next() {
				if (clazz == ResultMap.class)
					return nextMap();
				try {
					if (isSQLType(clazz))
						return (T) getSQLData(clazz, executor.rs, 1);
					T instance = newInstance(clazz);
					for (int i = 1; i <= md.getColumnCount(); i++) {
						Field field = fieldMap.get(md.getColumnLabel(i));
						if (field == null)
							field = fieldMap.get(conv(md.getColumnLabel(i)));
						if (field == null)
							continue;
						setValue(instance, field, getSQLData(field,
								executor.rs, i));
					}
					return instance;
				} catch (SQLException e) {
					throw new SQLRuntimeException(mkStringByCRLF(
							e.getMessage(), executor.getSQLInfo()), e);
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"Iterator#remove is not supported.");
			}
		};
	}

	@SuppressWarnings("unchecked")
	private T nextMap() {
		ResultMap map = new ResultMap();
		try {
			for (int i = 1; i <= md.getColumnCount(); i++) {
				map.put(md.getColumnLabel(i), executor.rs.getObject(i));
			}
			return (T) map;
		} catch (SQLException e) {
			throw new SQLRuntimeException(mkStringByCRLF(e.getMessage(),
					executor.getSQLInfo()), e);
		}
	}

	private Object conv(String columnLabel) {
		String[] strings = columnLabel.toLowerCase().split("_");
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			if (s.length() == 0)
				continue;
			if (sb.length() == 0)
				sb.append(s);
			else
				sb.append(s.substring(0, 1).toUpperCase() + s.substring(1));
		}
		return sb.toString();
	}
}
