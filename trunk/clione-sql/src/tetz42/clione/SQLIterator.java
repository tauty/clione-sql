package tetz42.clione;

import static tetz42.clione.util.ClioneUtil.*;

import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import tetz42.clione.exception.SQLRuntimeException;
import tetz42.clione.exception.WrapException;

public class SQLIterator<T> implements Iterable<T> {

	static <T> SQLIterator<T> genIterator(SQLExecutor executor, Class<T> clazz,
			Map<String, Object> paramMap) throws SQLException {
		return new SQLIterator<T>(executor, clazz, paramMap);
	}

	private final Class<T> clazz;
	private final SQLExecutor executor;
	private final ResultSetMetaData md;
	private final HashMap<String, FSet> fieldMap;

	public SQLIterator(SQLExecutor executor, Class<T> clazz,
			Map<String, Object> paramMap) throws SQLException {
		this.executor = executor;
		this.clazz = clazz;
		executor.stmt = executor.genStmt(paramMap);
		executor.rs = executor.stmt.executeQuery();
		this.md = executor.rs.getMetaData();
		Class<?> entityClass = clazz;
		fieldMap = new HashMap<String, FSet>();
		while (entityClass != null && entityClass != Object.class) {
			Field[] fields = entityClass.getDeclaredFields();
			for (Field field : fields) {
				if (fieldMap.containsKey(field.getName()))
					continue;
				fieldMap.put(field.getName(),
						new FSet(field, field.isAccessible()));
			}
			entityClass = entityClass.getSuperclass();
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
					throw new SQLRuntimeException(e.getMessage() + "\nsql -> "
							+ executor.sqlGenerator.sql + "\nparams -> "
							+ executor.sqlGenerator.params + "\nresource -> "
							+ executor.resourceInfo, e);
				}
			}

			@Override
			public T next() {
				if (clazz == Map.class || clazz == HashMap.class
						|| clazz == LinkedHashMap.class)
					return nextMap();
				try {
					T instance = clazz.newInstance();
					for (int i = 1; i <= md.getColumnCount(); i++) {
						FSet fset = fieldMap.get(md.getColumnLabel(i));
						if (fset == null)
							fset = fieldMap.get(conv(md.getColumnLabel(i)));
						if (fset == null)
							continue;
						fset.f.setAccessible(true);
						fset.f.set(instance, getSQLData(fset.f, executor.rs, i));
						fset.f.setAccessible(fset.b);
					}
					return instance;
				} catch (SQLException e) {
					throw new SQLRuntimeException(e.getMessage() + "\nsql -> "
							+ executor.sqlGenerator.sql + "\nparams -> "
							+ executor.sqlGenerator.params + "\nresource -> "
							+ executor.resourceInfo, e);
				} catch (InstantiationException e) {
					throw new WrapException(clazz.getSimpleName()
							+ " must have default constructor.", e);
				} catch (IllegalAccessException e) {
					throw new WrapException(clazz.getSimpleName()
							+ " have security problem.", e);
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
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			for (int i = 1; i <= md.getColumnCount(); i++) {
				map.put(md.getColumnLabel(i), executor.rs.getObject(i));
			}
			return (T) map;
		} catch (SQLException e) {
			throw new SQLRuntimeException(e.getMessage() + "\nsql -> "
					+ executor.sqlGenerator.sql + "\nparams -> "
					+ executor.sqlGenerator.params + "\nresource -> "
					+ executor.resourceInfo, e);
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

	private static class FSet {
		Field f;
		boolean b;

		private FSet(Field f, boolean b) {
			this.f = f;
			this.b = b;
		}
	}
}
