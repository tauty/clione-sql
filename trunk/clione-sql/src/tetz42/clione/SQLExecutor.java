package tetz42.clione;

import static tetz42.clione.SQLManager.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.node.SQLNode;
import tetz42.clione.util.ResultMap;
import tetz42.util.Pair;
import tetz42.util.Using;
import tetz42.util.exception.SQLRuntimeException;

public class SQLExecutor implements Closeable {

	private final SQLManager manager;
	private final int hashValue;
	final SQLGenerator sqlGenerator;
	String resourceInfo = null;

	final SQLNode sqlNode;

	PreparedStatement stmt;
	ResultSet rs;

	SQLExecutor(SQLManager manager, SQLNode sqlNode) {
		this.manager = manager;
		this.sqlNode = sqlNode;
		this.resourceInfo = sqlNode.resourceInfo;
		this.sqlGenerator = new SQLGenerator(manager.getNegativeValues());
		this.hashValue = (int) (Math.random() * Integer.MAX_VALUE);
	}

	public SQLExecutor emptyAsNegative() {
		return asNegative("");
	}

	public SQLExecutor asNegative(Object... negativeValues) {
		this.sqlGenerator.asNegative(negativeValues);
		return this;
	}

	public Map<String, Object> find() {
		return this.find((Map<String, Object>) null);
	}

	@Override
	public int hashCode() {
		return hashValue;
	}

	public ResultMap find(Object obj) {
		return this.find(params(obj));
	}

	public ResultMap find(final Map<String, Object> paramMap) {
		return new Using<ResultMap>(this) {

			@Override
			protected ResultMap execute() {
				for (ResultMap map : each(ResultMap.class, paramMap)) {
					return map;
				}
				return null;
			}
		}.invoke();
	}

	public List<ResultMap> findAll() {
		return this.findAll((Map<String, Object>) null);
	}

	public List<ResultMap> findAll(Object paramObj) {
		return this.findAll(params(paramObj));
	}

	public List<ResultMap> findAll(final Map<String, Object> paramMap) {
		return new Using<List<ResultMap>>(this) {

			@Override
			protected List<ResultMap> execute() {
				ArrayList<ResultMap> list = new ArrayList<ResultMap>();
				for (ResultMap map : each(ResultMap.class, paramMap)) {
					list.add(map);
				}
				return list;
			}
		}.invoke();
	}

	public <T> T find(Class<T> entityClass) {
		return this.find(entityClass, (Map<String, Object>) null);
	}

	public <T> T find(Class<T> entityClass, Object paramObj) {
		return this.find(entityClass, params(paramObj));
	}

	public <T> T find(final Class<T> entityClass,
			final Map<String, Object> paramMap) {
		return new Using<T>(this) {

			@Override
			protected T execute() {
				for (T entity : each(entityClass, paramMap)) {
					return entity;
				}
				return null;
			}
		}.invoke();
	}

	public <T> List<T> findAll(Class<T> entityClass) {
		return this.findAll(entityClass, null);
	}

	public <T> List<T> findAll(Class<T> entityClass, Object paramObj) {
		return this.findAll(entityClass, params(paramObj));
	}

	public <T> List<T> findAll(final Class<T> entityClass,
			final Map<String, Object> paramMap) {
		return new Using<List<T>>(this) {

			@Override
			protected List<T> execute() {
				ArrayList<T> list = new ArrayList<T>();
				for (T entity : each(entityClass, paramMap)) {
					list.add(entity);
				}
				return list;
			}
		}.invoke();
	}

	public <T> SQLIterator<T> each(Class<T> entityClass) {
		return each(entityClass, null);
	}

	public <T> SQLIterator<T> each(Class<T> entityClass, Object obj) {
		return each(entityClass, params(obj));
	}

	public SQLIterator<ResultMap> each(Map<String, Object> paramMap) {
		return each(ResultMap.class, paramMap);
	}

	public SQLIterator<ResultMap> each() {
		return each(ResultMap.class, null);
	}

	public SQLIterator<ResultMap> each(Object obj) {
		return each(ResultMap.class, params(obj));
	}

	public <T> SQLIterator<T> each(Class<T> entityClass,
			Map<String, Object> paramMap) {
		try {
			return SQLIterator.genIterator(this, entityClass, paramMap);
		} catch (SQLException e) {
			throw new SQLRuntimeException(getSQLInfo(), e);
		}
	}

	public int update() {
		return this.update((Map<String, Object>) null);
	}

	public int update(Object paramObj) {
		return this.update(params(paramObj));
	}

	public int update(final Map<String, Object> paramMap) {
		return new Using<Integer>(this) {

			@Override
			protected Integer execute() {
				try {
					stmt = genStmt(paramMap);
					return stmt.executeUpdate();
				} catch (SQLException e) {
					throw new SQLRuntimeException(getSQLInfo(), e);
				}
			}
		}.invoke();
	}

	public void closeStatement() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			manager.removeExecutor(this);
		}
	}

	@Override
	public void close() {
		closeStatement();
	}

	public String getSQLInfo() {
		return genSQLInfo(getSql(), getParams(), getResourceInfo());
	}

	public String getResourceInfo() {
		return this.resourceInfo;
	}

	public String genSql() {
		return genSql(null);
	}

	public Pair<String, List<Object>> genSqlAndParams() {
		return genSqlAndParams(null);
	}

	public Pair<String, List<Object>> genSqlAndParams(Object obj) {
		return genSqlAndParams(params(obj));
	}

	public Pair<String, List<Object>> genSqlAndParams(
			Map<String, Object> paramMap) {
		Pair<String, List<Object>> pair = new Pair<String, List<Object>>();
		pair.setFirst(genSql(paramMap));
		pair.setSecond(getParams());
		return pair;
	}

	public String genSql(Map<String, Object> paramMap) {
		String sql = sqlGenerator.genSql(paramMap, sqlNode);
		manager.setInfo(resourceInfo, sql, sqlGenerator.params);
		return sql;
	}

	public String getSql() {
		return this.sqlGenerator.sql;
	}

	public List<Object> getParams() {
		return this.sqlGenerator.params;
	}

	PreparedStatement genStmt(Map<String, Object> paramMap) throws SQLException {
		stmt = manager.con().prepareStatement(genSql(paramMap));
		manager.putExecutor(this);
		int i = 1;
		for (Object param : this.sqlGenerator.params) {
			stmt.setObject(i++, param);
		}
		return stmt;
	}
}
