package tetz42.clione;

import static tetz42.clione.SQLManager.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.node.SQLNode;
import tetz42.clione.util.ResultMap;

public class SQLExecutor {

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
		this.sqlGenerator = new SQLGenerator(manager.getNullValues());
		this.hashValue = (int) (Math.random() * Integer.MAX_VALUE);
	}

	public Map<String, Object> find() throws SQLException {
		return this.find((Map<String, Object>) null);
	}

	@Override
	public int hashCode() {
		return hashValue;
	}

	public ResultMap find(Object paramObj) throws SQLException {
		return this.find(params(paramObj));
	}

	public ResultMap find(Map<String, Object> paramMap) throws SQLException {
		try {
			for (ResultMap map : each(ResultMap.class, paramMap)) {
				return map;
			}
			return null;
		} catch (SQLException e) {
			throw new SQLException(joinByCrlf(e.getMessage(), getSQLInfo()), e);
		} finally {
			closeStatement();
		}
	}

	public List<ResultMap> findAll() throws SQLException {
		return this.findAll((Map<String, Object>) null);
	}

	public List<ResultMap> findAll(Object paramObj) throws SQLException {
		return this.findAll(params(paramObj));
	}

	public List<ResultMap> findAll(Map<String, Object> paramMap)
			throws SQLException {

		try {
			ArrayList<ResultMap> list = new ArrayList<ResultMap>();
			for (ResultMap map : each(ResultMap.class, paramMap)) {
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			throw new SQLException(joinByCrlf(e.getMessage(), getSQLInfo()), e);
		} finally {
			this.closeStatement();
		}
	}

	public <T> T find(Class<T> entityClass) throws SQLException {
		return this.find(entityClass, (Map<String, Object>) null);
	}

	public <T> T find(Class<T> entityClass, Object paramObj)
			throws SQLException {
		return this.find(entityClass, params(paramObj));
	}

	public <T> T find(Class<T> entityClass, Map<String, Object> paramMap)
			throws SQLException {
		try {
			for (T entity : each(entityClass, paramMap)) {
				return entity;
			}
			return null;
		} catch (SQLException e) {
			throw new SQLException(joinByCrlf(e.getMessage(), getSQLInfo()), e);
		} finally {
			this.closeStatement();
		}
	}

	public <T> List<T> findAll(Class<T> entityClass) throws SQLException {
		return this.findAll(entityClass, null);
	}

	public <T> List<T> findAll(Class<T> entityClass, Object paramObj)
			throws SQLException {
		return this.findAll(entityClass, params(paramObj));
	}

	public <T> List<T> findAll(Class<T> entityClass,
			Map<String, Object> paramMap) throws SQLException {

		try {
			ArrayList<T> list = new ArrayList<T>();
			for (T entity : each(entityClass, paramMap)) {
				list.add(entity);
			}
			return list;
		} catch (SQLException e) {
			throw new SQLException(joinByCrlf(e.getMessage(), getSQLInfo()), e);
		} finally {
			this.closeStatement();
		}
	}

	public <T> SQLIterator<T> each(Class<T> entityClass) throws SQLException {
		return SQLIterator.genIterator(this, entityClass, null);
	}

	public SQLIterator<ResultMap> each() throws SQLException {
		return SQLIterator.genIterator(this, ResultMap.class, null);
	}

	public <T> SQLIterator<T> each(Class<T> entityClass,
			Map<String, Object> paramMap) throws SQLException {
		return SQLIterator.genIterator(this, entityClass, paramMap);
	}

	public int update() throws SQLException {
		return this.update((Map<String, Object>) null);
	}

	public int update(Object paramObj) throws SQLException {
		return this.update(params(paramObj));
	}

	public int update(Map<String, Object> paramMap) throws SQLException {
		try {
			stmt = this.genStmt(paramMap);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(joinByCrlf(e.getMessage(), getSQLInfo()), e);
		} finally {
			this.closeStatement();
		}
	}

	public void closeStatement() throws SQLException {
		if (rs != null) {
			rs.close();
			rs = null;
		}
		if (stmt != null) {
			stmt.close();
			stmt = null;
		}
		manager.removeExecutor(this);
	}

	public String getSQLInfo() {
		return genSQLInfo(getSql(), getParams(), getResourceInfo());
	}

	public String getSql() {
		return this.sqlGenerator.sql;
	}

	public List<Object> getParams() {
		return this.sqlGenerator.params;
	}

	public String getResourceInfo() {
		return this.resourceInfo;
	}

	public String genSql() {
		return genSql(null);
	}

	public String genSql(Map<String, Object> paramMap) {
		String sql = sqlGenerator.genSql(paramMap, sqlNode);
		manager.setInfo(resourceInfo, sql, sqlGenerator.params);
		return sql;
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
