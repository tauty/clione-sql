package tetz42.clione;

import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.util.ReflectionUtil.*;
import static tetz42.util.Util.*;

import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tetz42.clione.util.ResultMap;
import tetz42.util.Function;
import tetz42.util.exception.SQLRuntimeException;

public class SQLIterator<T> implements Iterable<T> {

	static <T> SQLIterator<T> genIterator(SQLExecutor executor, Class<T> clazz,
			Map<String, Object> paramMap) throws SQLException {
		return new SQLIterator<T>(executor, clazz, paramMap);
	}

	private final Class<T> clazz;
	private final SQLExecutor executor;
	private final ResultSetMetaData md;
	private final ConcurrentHashMap<Class<?>, FieldMapContainer> fieldContainerCache = newConcurrentMap();

	public SQLIterator(SQLExecutor executor, final Class<T> clazz,
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
	}

	@Override
	public Iterator<T> iterator() {
		if (clazz == ResultMap.class) {
			return new RsIterator() {
				@SuppressWarnings("unchecked")
				@Override
				public T nextTask() throws SQLException {
					ResultMap map = new ResultMap();
					for (int i = 1; i <= md.getColumnCount(); i++) {
						map.put(md.getColumnLabel(i), executor.rs.getObject(i));
					}
					return (T) map;
				}
			};
		} else if (isSQLType(clazz)) {
			return new RsIterator() {
				@SuppressWarnings("unchecked")
				@Override
				public T nextTask() throws SQLException {
					return (T) getSQLData(clazz, executor.rs, 1);
				}
			};
		}
		return new RsIterator() {
			@Override
			public T nextTask() throws SQLException {
				FieldMapContainer con = getFieldContainer(clazz);
				T instance = newInstance(clazz);
				for (int i = 1; i <= md.getColumnCount(); i++) {
					String label = md.getColumnLabel(i).toLowerCase();
					Field field = con.snakeMap.get(label);
					if (field == null)
						field = con.camelMap.get(camelize(label));
					if (field == null)
						continue;
					setValue(instance, field, getSQLData(field, executor.rs, i));
				}
				return instance;
			}
		};
	}

	class Tako {
		final Map<String, Object> cache = newMap();
		final FieldMapContainer con;

		Tako(Object obj) {
			con = getFieldContainer(clazz);
			cache.put("", newInstance(clazz));
		}

		void set(String label, Object obj) {
			Field f = con.snakeMap.get(label);
			if (f == null)
				f = con.camelMap.get(camelize(label));
			if (f == null)
				return;

		}

		String toBase(Field f, String snake) {
			String base = snake.substring(0, snake.length()
					- f.getName().length());
			if (base.endsWith("_"))
				base = base.substring(0, base.length() - 1);
			return base;
		}
	}

	private Object camelize(String columnLabel) {
		String[] strings = columnLabel.split("_");
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

	private FieldMapContainer getFieldContainer(final Class<?> clazz) {
		return getOrNew(fieldContainerCache, clazz,
				new Function<FieldMapContainer>() {

					@Override
					public FieldMapContainer apply() {
						FieldMapContainer con = new FieldMapContainer();
						map("", "", clazz, con);
						return con.toUnmodifiable();
					}

					private void map(String snakeBaseName,
							String camelBaseName, Class<?> type,
							FieldMapContainer con) {
						for (Field f : getFields(type)) {
							String snakeName = con.putSnake(snakeBaseName, f);
							String camelName = con.putCamel(camelBaseName, f);
							if (!isSQLType(f.getType()))
								map(snakeName, camelName, f.getType(), con);
						}
					}
				});
	}

	private abstract class RsIterator implements Iterator<T> {

		@Override
		public boolean hasNext() {
			try {
				return executor.rs.next();
			} catch (SQLException e) {
				throw new SQLRuntimeException(mkStringByCRLF(e.getMessage(),
						executor.getSQLInfo()), e);
			}
		}

		@Override
		public T next() {
			try {
				return nextTask();
			} catch (SQLException e) {
				throw new SQLRuntimeException(mkStringByCRLF(e.getMessage(),
						executor.getSQLInfo()), e);
			}
		}

		abstract T nextTask() throws SQLException;

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Iterator#remove is not supported.");
		}
	}

	private static class FieldMapContainer {
		final Map<String, Field> snakeMap;
		final Map<String, Field> camelMap;

		FieldMapContainer() {
			snakeMap = newMap();
			camelMap = newMap();
		}

		FieldMapContainer(Map<String, Field> snakeMap,
				Map<String, Field> camelMap) {
			this.snakeMap = snakeMap;
			this.camelMap = camelMap;
		}

		String putSnake(String snakeBaseName, Field f) {
			String name;
			if (isEmpty(snakeBaseName))
				name = f.getName().toLowerCase();
			else
				name = snakeBaseName + "_" + f.getName().toLowerCase();
			snakeMap.put(name, f);
			return name;
		}

		String putCamel(String camelBaseName, Field f) {
			String name;
			if (isEmpty(camelBaseName))
				name = f.getName();
			else
				name = camelBaseName
						+ f.getName().substring(0, 1).toUpperCase()
						+ f.getName().substring(1);
			camelMap.put(name, f);
			return name;
		}

		FieldMapContainer toUnmodifiable() {
			return new FieldMapContainer(Collections.unmodifiableMap(snakeMap),
					Collections.unmodifiableMap(camelMap));
		}
	}

}
