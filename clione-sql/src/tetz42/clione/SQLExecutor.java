package tetz42.clione;

import static tetz42.clione.SQLManager.*;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.node.LineNode;
import tetz42.clione.parsar.SQLParser;

public class SQLExecutor {

	private final SQLManager manager;
	private final int hashValue;
	final SQLGenerator sqlGenerator;
	private String resourceInfo = null;

	final List<LineNode> lineTreeList;

	PreparedStatement stmt;
	ResultSet rs;

	SQLExecutor(SQLManager manager, InputStream in) {
		this.manager = manager;
		this.lineTreeList = new SQLParser(resourceInfo).parse(in);
		this.sqlGenerator = new SQLGenerator(manager.getNullValues());
		this.hashValue = (int) (Math.random() * Integer.MAX_VALUE);
	}

	void setResourceInfo(String resourceInfo) {
		this.resourceInfo = resourceInfo;
		this.sqlGenerator.setResourceInfo(resourceInfo);
	}

	public Map<String, Object> find() throws SQLException {
		return this.find((Map<String, Object>) null);
	}

	@Override
	public int hashCode() {
		return hashValue;
	}

	public Map<String, Object> find(Object paramObj) throws SQLException {
		return this.find(params(paramObj));
	}

	public Map<String, Object> find(Map<String, Object> paramMap)
			throws SQLException {
		try {
			for (Map<String, Object> map : SQLIterator.genIterator(this,
					Map.class, paramMap)) {
				return map;
			}
			return null;
		} catch (SQLException e) {
			throw new SQLException(e.getMessage() + "\nsql -> "
					+ this.sqlGenerator.sql + "\nparams -> "
					+ this.sqlGenerator.params + "\nresource -> "
					+ resourceInfo, e);
		} finally {
			closeStatement();
		}
	}

	public List<Map<String, Object>> findAll() throws SQLException {
		return this.findAll((Map<String, Object>) null);
	}

	public List<Map<String, Object>> findAll(Object paramObj)
			throws SQLException {
		return this.findAll(params(paramObj));
	}

	public List<Map<String, Object>> findAll(Map<String, Object> paramMap)
			throws SQLException {

		try {
			ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map : SQLIterator.genIterator(this,
					Map.class, paramMap)) {
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			throw new SQLException(e.getMessage() + "\nsql -> "
					+ this.sqlGenerator.sql + "\nparams -> "
					+ this.sqlGenerator.params + "\nresource -> "
					+ resourceInfo, e);
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
			throw new SQLException(e.getMessage() + "\nsql -> "
					+ this.sqlGenerator.sql + "\nparams -> "
					+ this.sqlGenerator.params + "\nresource -> "
					+ resourceInfo, e);
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
			throw new SQLException(e.getMessage() + "\nsql -> "
					+ this.sqlGenerator.sql + "\nparams -> "
					+ this.sqlGenerator.params + "\nresource -> "
					+ resourceInfo, e);
		} finally {
			this.closeStatement();
		}
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
			throw new SQLException(e.getMessage() + "\nsql -> "
					+ this.sqlGenerator.sql + "\nparams -> "
					+ this.sqlGenerator.params + "\nresource -> "
					+ resourceInfo, e);
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

	public String getExecutedSql() {
		return this.sqlGenerator.sql;
	}

	public List<Object> getExecutedParams() {
		return this.sqlGenerator.params;
	}

	public String genSql() {
		return genSql(null);
	}

	public String genSql(Map<String, Object> paramMap) {
		String sql = sqlGenerator.genSql(paramMap, lineTreeList);
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
