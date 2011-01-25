/*
 * Copyright 2011 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tetz42.clione;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.clione.module.LineTree;
import tetz42.clione.module.ParamMap;
import tetz42.clione.module.SQLGenerator;
import tetz42.clione.module.SQLParser;
import tetz42.clione.module.WrapException;

public class SQLManager {

	private static ThreadLocal<Connection> tcon = new ThreadLocal<Connection>();

	public static SQLManager sqlManager(Class<?> clazz, String sqlFile) {
		// TODO add instance management logic
		return new SQLManager(clazz, sqlFile);
	}

	public static SQLManager sqlManager(Class<?> clazz, String sqlFile,
			Connection con) {
		// TODO add instance management logic
		return new SQLManager(clazz, sqlFile, con);
	}

	public static SQLManager sqlManager(String sqlPath) {
		// TODO add instance management logic
		return new SQLManager(sqlPath);
	}

	public static SQLManager sqlManager(String sqlPath, Connection con) {
		// TODO add instance management logic
		return new SQLManager(sqlPath, con);
	}

	public static SQLManager sqlManager(InputStream in) {
		// TODO add instance management logic
		return new SQLManager(in);
	}

	public static SQLManager sqlManager(InputStream in, Connection con) {
		// TODO add instance management logic
		return new SQLManager(in, con);
	}

	public static void setThreadConnection(Connection con) {
		tcon.set(con);
	}

	public static Connection getThreadConnection() {
		return tcon.get();
	}

	public static ParamMap params() {
		return new ParamMap();
	}

	public static ParamMap params(String key, Object value) {
		return params().$(key, value);
	}

	final Connection con;
	final List<LineTree> lineTreeList;
	final SQLGenerator sqlGenerator;
	private PreparedStatement stmt;
	private ResultSet rs;

	public SQLManager(Class<?> clazz, String sqlFile) {
		this(clazz, sqlFile, null);
	}

	public SQLManager(Class<?> clazz, String sqlFile, Connection con) {
		InputStream in = clazz.getResourceAsStream(sqlFile);
		if (in == null)
			throw new NullPointerException(
					"SQL File might not be found. file name:" + sqlFile
							+ ", class:" + clazz.getName());
		this.lineTreeList = new SQLParser().parse(in);
		this.con = con;
		this.sqlGenerator = new SQLGenerator();
	}

	public SQLManager(String sqlPath) {
		this(sqlPath, null);
	}

	public SQLManager(String sqlPath, Connection con) {
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				sqlPath);
		if (in == null)
			throw new NullPointerException("SQL File might not be found. path:"
					+ sqlPath);
		this.lineTreeList = new SQLParser().parse(in);
		this.con = con;
		this.sqlGenerator = new SQLGenerator();
	}

	public SQLManager(InputStream in) {
		this(in, null);
	}

	public SQLManager(InputStream in, Connection con) {
		this.lineTreeList = new SQLParser().parse(in);
		this.con = con;
		this.sqlGenerator = new SQLGenerator();
	}

	public Connection con() {
		Connection con = this.con != null ? this.con : tcon.get();
		if (con == null)
			throw new NullPointerException("No connection is available!");
		return con;
	}

	public Map<String, Object> find() throws SQLException {
		return this.find((Map<String, Object>) null);
	}

	public Map<String, Object> find(Map<String, Object> paramMap)
			throws SQLException {
		// TODO 後でIterator対応したら、そちらにロジックを変更(Fetch 1 で呼び出す)
		List<Map<String, Object>> list = this.findAll(paramMap);
		return list.size() != 0 ? list.get(0) : null;
	}

	public List<Map<String, Object>> findAll() throws SQLException {
		return this.findAll((Map<String, Object>) null);
	}

	public List<Map<String, Object>> findAll(Map<String, Object> paramMap)
			throws SQLException {

		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ResultSet rs = null;
		try {
			stmt = this.genStmt(paramMap);
			rs = stmt.executeQuery();
			ResultSetMetaData md = rs.getMetaData();

			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= md.getColumnCount(); i++) {
					map.put(md.getColumnLabel(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			throw new SQLException(
					e.getMessage() + "\n\tsql:" + this.sqlGenerator.sql
							+ "\n\t" + this.sqlGenerator.params,
					e.getSQLState(), e.getErrorCode(), e);
		} finally {
			this.closeStatement();
		}
		return list;
	}

	public <T> T find(Class<T> entityClass) throws SQLException {
		return this.find(entityClass, null);
	}

	public <T> T find(Class<T> entityClass, Map<String, Object> paramMap)
			throws SQLException {
		// TODO 後でIterator対応したら、そちらにロジックを変更(Fetch 1 で呼び出す)
		List<T> list = this.findAll(entityClass, paramMap);
		return list.size() != 0 ? list.get(0) : null;
	}

	public <T> List<T> findAll(Class<T> entityClass) throws SQLException {
		return this.findAll(entityClass, null);
	}

	public <T> List<T> findAll(Class<T> entityClass,
			Map<String, Object> paramMap) throws SQLException {

		// TODO 後でIterator対応したら、そちらにロジックを変更
		ArrayList<T> list = new ArrayList<T>();
		try {
			stmt = this.genStmt(paramMap);
			rs = stmt.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			Field[] fields = entityClass.getDeclaredFields();
			HashMap<String, Field> fieldMap = new HashMap<String, Field>();
			for (Field field : fields)
				fieldMap.put(field.getName(), field);

			while (rs.next()) {
				T instance = entityClass.newInstance();
				for (int i = 1; i <= md.getColumnCount(); i++) {
					Field f = fieldMap.get(md.getColumnLabel(i));
					if (f == null)
						f = fieldMap.get(conv(md.getColumnLabel(i)));
					if (f == null)
						continue;
					f.set(instance, rs.getObject(i));
				}
				list.add(instance);
			}
		} catch (SQLException e) {
			throw new SQLException(
					e.getMessage() + "\n\tsql:" + this.sqlGenerator.sql
							+ "\n\t" + this.sqlGenerator.params,
					e.getSQLState(), e.getErrorCode(), e);
		} catch (InstantiationException e) {
			throw new WrapException(entityClass.getSimpleName()
					+ " must have default constructor.", e);
		} catch (IllegalAccessException e) {
			throw new WrapException(entityClass.getSimpleName()
					+ " have security problem.", e);
		} finally {
			this.closeStatement();
		}
		return list;
	}

	public int update() throws SQLException {
		return this.update(null);
	}

	public int update(Map<String, Object> paramMap) throws SQLException {
		try {
			stmt = this.genStmt(paramMap);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(
					e.getMessage() + "\n\tsql:" + this.sqlGenerator.sql
							+ "\n\t" + this.sqlGenerator.params, e);
		} finally {
			this.closeStatement();
		}
	}

	public void closeStatement() throws SQLException {
		if (stmt != null) {
			stmt.close();
			stmt = null;
		}
		if (rs != null) {
			rs.close();
			rs = null;
		}
	}

	public void closeConnection() throws SQLException {
		closeStatement();
		Connection con = con();
		if (con != null)
			con.close();
	}

	public String getExecutedSql() {
		return this.sqlGenerator.sql;
	}

	public List<Object> getExecutedParams() {
		return this.sqlGenerator.params;
	}

	public String genSql() {
		return sqlGenerator.genSql((Map<String, Object>) null, lineTreeList);
	}

	public String genSql(Map<String, Object> paramMap) {
		return sqlGenerator.genSql(paramMap, lineTreeList);
	}

	private PreparedStatement genStmt(Map<String, Object> paramMap)
			throws SQLException {
		stmt = con().prepareStatement(genSql(paramMap));
		int i = 1;
		for (Object param : this.sqlGenerator.params) {
			stmt.setObject(i++, param);
		}
		return stmt;
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
