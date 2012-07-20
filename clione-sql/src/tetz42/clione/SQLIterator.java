package tetz42.clione;

import static tetz42.clione.common.ReflectionUtil.*;
import static tetz42.clione.common.Util.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tetz42.clione.common.Function;
import tetz42.clione.common.exception.SQLRuntimeException;
import tetz42.clione.exception.DuplicateKeyException;
import tetz42.clione.util.ClioneUtil;
import tetz42.clione.util.Config;
import tetz42.clione.util.ResultMap;

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

	/**
	 * Generates Iterator instance to iterate the result set and convert the
	 * result into a instance of the specified Class.<br>
	 * The conversion rules are below:<br>
	 *
	 * <pre>
	 * [In case of the Class is not specified or is assignable from ResultMap]
	 * 	Converts the result into a instance of ResultMap.
	 * 	For example, in case of the result set keys and values are below:
	 *
	 * 		"key1": 100
	 * 		"key2": "value2"
	 *
	 * 	it is converted to:
	 *
	 * 		ResultMap {
	 * 			"key1": 100
	 * 			"key2": "value2"
	 * 		}
	 *
	 * [In case of the result of {@link ClioneUtil#isJDBCGetterType(Class)} is true]
	 * 	Converts the first value of ResultSet into a instance of the specified Class.
	 * 	For example, in case of the result set indexes and values are below:
	 *
	 * 		1: "foo"
	 * 		2: 100
	 * 		3: "bar"
	 *
	 * 	and the specified Class is String.class, the result is converted to "foo".
	 *
	 * [Any other case]
	 * 	Converts the result into a instance of the specified Class.
	 * 	For example, in case of the result set keys and values are below:
	 *
	 * 		"name": "John"
	 * 		"title": "Chief Architect"
	 * 		"employed_from": "1998-04-01"
	 * 		"employed_to": "2012-08-31"
	 *
	 * 	and the specified Class have the matched fields, the result would be mapped to like below:
	 *
	 * 		Employee {
	 * 			name = "John"
	 * 			title = "Chief Architect"
	 * 			employed = Period {
	 * 				from = "1998-04-01"
	 * 				to = "2012-08-31"
	 * 			}
	 * 		}
	 * </pre>
	 *
	 * @see ClioneUtil#isJDBCGetterType(Class)
	 */
	@Override
	public Iterator<T> iterator() {
		if (clazz == null || classOf(ResultMap.class, clazz)) {
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
		} else if (isJDBCGetterType(clazz)) {
			return new RsIterator() {
				@SuppressWarnings("unchecked")
				@Override
				public T nextTask() throws SQLException {
					return (T) getJDBCData(clazz, executor.rs, 1);
				}
			};
		} else {
			return new RsIterator() {
				@Override
				public T nextTask() throws SQLException {
					ObjBuilder builder = new ObjBuilder();
					for (int i = 1; i <= md.getColumnCount(); i++) {
						String label = md.getColumnLabel(i).toLowerCase();
						FN fn = builder.con.getField(label);
						if (fn.f == null)
							continue;
						builder.set(fn.name, fn.f, getJDBCData(fn.f,
								executor.rs, i));
					}
					return builder.getInstance();
				}
			};
		}
	}

	class ObjBuilder {
		final Map<String, Object> cache = newMap();
		final FieldMapContainer con = getFieldContainer(clazz);
		final T instance = newInstance(clazz);

		{
			cache.put("", instance);
		}

		T getInstance() {
			return instance;
		}

		void set(String label, Field f, Object obj) {
			Object receiver = getObj(toBase(f.getName(), label));
			if (receiver == null)
				return;

			setValue(receiver, f, obj);
		}

		Object getObj(String name) {

			Object obj = cache.get(name);
			if (obj != null)
				return obj;

			FN fn = con.getField(name);
			if (fn.f == null)
				return null;

			Object receiver = getObj(toBase(fn.f.getName(), fn.name));
			if (receiver == null)
				return null;

			obj = getValue(receiver, fn.f);
			if (obj == null) {
				obj = newInstance(fn.f.getType());
				setValue(receiver, fn.f, obj);
			}
			cache.put(name, obj);
			return obj;
		}
	}

	private static String camelize(String columnLabel) {
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

	private static String toBase(String name, String fullName) {
		String base = fullName.substring(0, fullName.length() - name.length());
		if (base.endsWith("_"))
			base = base.substring(0, base.length() - 1);
		return base;
	}

	private FieldMapContainer getFieldContainer(final Class<?> clazz) {
		return getOrNew(fieldContainerCache, clazz,
				new Function<FieldMapContainer>() {

					@Override
					public FieldMapContainer apply() {
						FieldMapContainer con = new FieldMapContainer();
						map("", "", clazz, con, 0);
						return con.toUnmodifiable();
					}

					private void map(String snakeBaseName,
							String camelBaseName, Class<?> type,
							FieldMapContainer con, int depth) {
						for (Field f : getFields(type)) {
							String snakeName = con.putSnake(snakeBaseName, f);
							String camelName = con.putCamel(camelBaseName, f);
							if (!isJDBCGetterType(f.getType())
									&& depth < Config.get().ENTITY_DEPTH_LIMIT)
								map(snakeName, camelName, f.getType(), con,
										depth + 1);
						}
					}
				});
	}

	private static enum RsStatus {
		UNKOWN, NEXT_OK, ENDED
	}

	private abstract class RsIterator implements Iterator<T> {

		RsStatus status = RsStatus.UNKOWN;

		@Override
		public boolean hasNext() {
			try {
				switch (status) {
				case NEXT_OK:
					return true;
				case ENDED:
					return false;
				default:
					boolean ret = executor.rs.next();
					status = ret ? RsStatus.NEXT_OK : RsStatus.ENDED;
					return ret;
				}
			} catch (SQLException e) {
				throw new SQLRuntimeException(mkStringByCRLF(e.getMessage(),
						executor.getSQLInfo()), e);
			}
		}

		@Override
		public T next() {
			try {
				switch (status) {
				case NEXT_OK:
					status = RsStatus.UNKOWN;
					return nextTask();
				case ENDED:
					throw new UnsupportedOperationException(
							"Iterator has already ended.");
				default:
					if (executor.rs.next()) {
						status = RsStatus.UNKOWN;
						return nextTask();
					} else {
						status = RsStatus.ENDED;
						throw new UnsupportedOperationException(
								"Iterator has already ended.");
					}
				}
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

	private class FieldMapContainer {
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

		FN getField(String name) {
			Field f = snakeMap.get(name);
			if (f == null) {
				name = camelize(name);
				f = camelMap.get(name);
			}
			return new FN(f, name);
		}

		String putSnake(String snakeBaseName, Field f) {
			String name;
			if (isEmpty(snakeBaseName))
				name = f.getName().toLowerCase();
			else
				name = snakeBaseName + "_" + f.getName().toLowerCase();
			putMap(snakeMap, name, f);
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
			putMap(camelMap, name, f);
			return name;
		}

		FieldMapContainer toUnmodifiable() {
			return new FieldMapContainer(Collections.unmodifiableMap(snakeMap),
					Collections.unmodifiableMap(camelMap));
		}

		private void putMap(Map<String, Field> map, String name, Field f) {
			if (map.containsKey(name))
				throw new DuplicateKeyException("The key, '" + name
						+ "' is duplicate. class:" + clazz.getName());
			map.put(name, f);
		}
	}

	static class FN {
		final Field f;
		final String name;

		FN(Field f, String name) {
			this.f = f;
			this.name = name;
		}
	}

}
