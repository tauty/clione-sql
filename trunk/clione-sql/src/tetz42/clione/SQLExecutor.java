/*
 * Copyright 2011 - 2012 tetsuo.ohta[at]gmail.com
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

import static tetz42.clione.SQLManager.*;
import static tetz42.clione.common.Util.*;
import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tetz42.clione.SQLManager.SQLSet;
import tetz42.clione.common.Using;
import tetz42.clione.common.exception.SQLRuntimeException;
import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.node.SQLNode;
import tetz42.clione.util.ParamMap;
import tetz42.clione.util.ResultMap;

/**
 *
 * @author tetz
 */
public class SQLExecutor implements Closeable {

	private final SQLManager manager;
	private final int hashValue;
	final SQLGenerator sqlGenerator;
	String resourceInfo = null;
	private final String productName;
	private Object[] negativeValues;

	final SQLNode sqlNode;

	PreparedStatement stmt;
	ResultSet rs;

	SQLExecutor(SQLManager manager, SQLNode sqlNode) {
		this.manager = manager;
		this.sqlNode = sqlNode;
		this.resourceInfo = sqlNode.resourceInfo;
		this.sqlGenerator = new SQLGenerator();
		this.hashValue = (int) (Math.random() * Integer.MAX_VALUE);
		this.productName = manager.getProductName();
		this.negativeValues = manager.getNegativeValues();
	}

	/**
	 * Executes the given SQL select statement and returns the result as a
	 * {@link ResultMap} instance. If no record is selected, it returns null. If
	 * multiple records are selected, it returns the 1st record only.
	 *
	 * @return ResultMap instance
	 * @throws SQLRuntimeException
	 * @see {@link ResultMap}
	 * @see SQLIterator#iterator()
	 */
	public ResultMap find() {
		return this.find((Map<String, Object>) null);
	}

	/**
	 * Executes the given SQL select statement and returns the result as a
	 * {@link ResultMap} instance. If no record is selected, it returns null. If
	 * multiple records are selected, it returns the 1st record only.
	 *
	 * @param object
	 *            the object to be inspected and mapped to SQL parameters
	 * @return ResultMap instance
	 * @throws SQLRuntimeException
	 * @see {@link ResultMap}
	 * @see {@link ParamMap#object(Object)}
	 * @see SQLIterator#iterator()
	 */
	public ResultMap find(Object object) {
		return this.find(params(object));
	}

	/**
	 * Executes the given SQL select statement and returns the result as a
	 * {@link ResultMap} instance. If no record is selected, it returns null. If
	 * multiple records are selected, it returns the 1st record only.
	 *
	 * @param paramMap
	 *            the map instance to be mapped to SQL parameters
	 * @return ResultMap instance
	 * @throws SQLRuntimeException
	 * @see {@link ResultMap}
	 * @see SQLIterator#iterator()
	 */
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

	/**
	 * Executes the given SQL select statement and returns the result as a
	 * instance of the class specified. If no record is selected, it returns
	 * null. If multiple records are selected, it returns the 1st record only.
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @return the result instance
	 * @throws SQLRuntimeException
	 * @see SQLIterator#iterator()
	 */
	public <T> T find(Class<T> entityClass) {
		return this.find(entityClass, (Map<String, Object>) null);
	}

	/**
	 * Executes the given SQL select statement and returns the result as a
	 * instance of the class specified. If no record is selected, it returns
	 * null. If multiple records are selected, it returns the 1st record only.
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @param object
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the result instance
	 * @throws SQLRuntimeException
	 * @see {@link ParamMap#object(Object)}
	 * @see SQLIterator#iterator()
	 */
	public <T> T find(Class<T> entityClass, Object object) {
		return this.find(entityClass, params(object));
	}

	/**
	 * Executes the given SQL select statement and returns the result as a
	 * instance of the class specified. If no record is selected, it returns
	 * null. If multiple records are selected, it returns the 1st record only.
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @param paramMap
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the result instance
	 * @throws SQLRuntimeException
	 * @see {@link ParamMap#object(Object)}
	 * @see SQLIterator#iterator()
	 */
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

	/**
	 * Executes the given SQL select statement and returns the result as a list
	 * of {@link ResultMap} instance. If no record is selected, it returns empty
	 * list.
	 *
	 * @return the list of ResultMap instance
	 * @throws SQLRuntimeException
	 * @see {@link ResultMap}
	 * @see SQLIterator#iterator()
	 */
	public List<ResultMap> findAll() {
		return this.findAll((Map<String, Object>) null);
	}

	/**
	 * Executes the given SQL select statement and returns the result as a list
	 * of {@link ResultMap} instance. If no record is selected, it returns empty
	 * list.
	 *
	 * @param object
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the list of ResultMap instance
	 * @throws SQLRuntimeException
	 * @see {@link ResultMap}
	 * @see {@link ParamMap#object(Object)}
	 * @see SQLIterator#iterator()
	 */
	public List<ResultMap> findAll(Object object) {
		return this.findAll(params(object));
	}

	/**
	 * Executes the given SQL select statement and returns the result as a list
	 * of {@link ResultMap} instance. If no record is selected, it returns empty
	 * list.
	 *
	 * @param paramMap
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the list of ResultMap instance
	 * @throws SQLRuntimeException
	 * @see {@link ResultMap}
	 * @see SQLIterator#iterator()
	 */
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

	/**
	 * Executes the given SQL select statement and returns the result as a list
	 * of instance of the class specified. If no record is selected, it returns
	 * empty list.
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @param paramMap
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the list of result instance
	 * @throws SQLRuntimeException
	 * @see SQLIterator#iterator()
	 */
	public <T> List<T> findAll(Class<T> entityClass) {
		return this.findAll(entityClass, null);
	}

	/**
	 * Executes the given SQL select statement and returns the result as a list
	 * of instance of the class specified. If no record is selected, it returns
	 * empty list.
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @param paramObj
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the list of result instance
	 * @throws SQLRuntimeException
	 * @see {@link ParamMap#object(Object)}
	 * @see SQLIterator#iterator()
	 */
	public <T> List<T> findAll(Class<T> entityClass, Object paramObj) {
		return this.findAll(entityClass, params(paramObj));
	}

	/**
	 * Executes the given SQL select statement and returns the result as a list
	 * of instance of the class specified. If no record is selected, it returns
	 * empty list.
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @param paramMap
	 *            the Map instance mapped to SQL parameters
	 * @return the list of result instance
	 * @throws SQLRuntimeException
	 * @see SQLIterator#iterator()
	 */
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

	/**
	 * Executes the given SQL select statement and returns the iterator wrapper
	 * of result set object. The {@link Iterator#next()} returns the instance of
	 * the class specified.<br>
	 * Note: The result set and statement object bound to this SQLExecutor
	 * instance is NOT close automatically. So you should call
	 * {@link SQLManager#close()} or {@link SQLExecutor#close()} finally like
	 * below:<br>
	 *
	 * <pre>
	 * SQLManager sqlManager = sqlManager();
	 * try {
	 * 	for (Entity e : sqlManager.useFile(&quot;sql/Select.sql&quot;).each(Entity.class,
	 * 			params(&quot;type&quot;, &quot;A&quot;))) {
	 * 		System.out.println(e.toString());
	 * 	}
	 * } finally {
	 * 	sqlManager.close();
	 * }
	 * </pre>
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @return the iterator wrapper of result set
	 * @throws SQLRuntimeException
	 * @see SQLIterator#iterator()
	 */
	public <T> SQLIterator<T> each(Class<T> entityClass) {
		return each(entityClass, null);
	}

	/**
	 * Executes the given SQL select statement and returns the iterator wrapper
	 * of result set object. The {@link Iterator#next()} returns the instance of
	 * the class specified.<br>
	 * Note: The result set and statement object bound to this SQLExecutor
	 * instance is NOT close automatically. So you should call
	 * {@link SQLManager#close()} or {@link SQLExecutor#close()} finally like
	 * below:<br>
	 *
	 * <pre>
	 * SQLManager sqlManager = sqlManager();
	 * try {
	 * 	Condition cond = new Condition();
	 * 	cond.setType(&quot;A&quot;);
	 * 	for (Entity e : sqlManager.useFile(&quot;sql/Select.sql&quot;).each(Entity.class,
	 * 			cond)) {
	 * 		System.out.println(e.toString());
	 * 	}
	 * } finally {
	 * 	sqlManager.close();
	 * }
	 * </pre>
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @param paramObj
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the iterator wrapper of result set
	 * @throws SQLRuntimeException
	 * @see {@link ParamMap#object(Object)}
	 * @see SQLIterator#iterator()
	 */
	public <T> SQLIterator<T> each(Class<T> entityClass, Object paramObj) {
		return each(entityClass, params(paramObj));
	}

	/**
	 * Executes the given SQL select statement and returns the iterator wrapper
	 * of result set object. The {@link Iterator#next()} returns the instance of
	 * {@link ResultMap}.<br>
	 * Note: The result set and statement object bound to this SQLExecutor
	 * instance is NOT close automatically. So you should call
	 * {@link SQLManager#close()} or {@link SQLExecutor#close()} finally like
	 * below:<br>
	 *
	 * <pre>
	 * SQLManager sqlManager = sqlManager();
	 * try {
	 * 	for (ResultMap map : sqlManager.useFile(&quot;sql/Select.sql&quot;).each(
	 * 			Entity.class, params(&quot;type&quot;, &quot;A&quot;))) {
	 * 		System.out.println(map.toString());
	 * 	}
	 * } finally {
	 * 	sqlManager.close();
	 * }
	 * </pre>
	 *
	 * @param <T>
	 * @param paramMap
	 *            the Map instance mapped to SQL parameters
	 * @return the iterator wrapper of result set
	 * @throws SQLRuntimeException
	 * @see SQLIterator#iterator()
	 */
	public SQLIterator<ResultMap> each(Map<String, Object> paramMap) {
		return each(ResultMap.class, paramMap);
	}

	/**
	 * Executes the given SQL select statement and returns the iterator wrapper
	 * of result set object. The {@link Iterator#next()} returns the instance of
	 * {@link ResultMap}.<br>
	 * Note: The result set and statement object bound to this SQLExecutor
	 * instance is NOT close automatically. So you should call
	 * {@link SQLManager#close()} or {@link SQLExecutor#close()} finally like
	 * below:<br>
	 *
	 * <pre>
	 * SQLManager sqlManager = sqlManager();
	 * try {
	 * 	for (ResultMap map : sqlManager.useFile(&quot;sql/Select.sql&quot;).each()) {
	 * 		System.out.println(map.toString());
	 * 	}
	 * } finally {
	 * 	sqlManager.close();
	 * }
	 * </pre>
	 *
	 * @param <T>
	 * @return the iterator wrapper of result set
	 * @throws SQLRuntimeException
	 * @see SQLIterator#iterator()
	 */
	public SQLIterator<ResultMap> each() {
		return each(ResultMap.class, null);
	}

	/**
	 * Executes the given SQL select statement and returns the iterator wrapper
	 * of result set object. The {@link Iterator#next()} returns the instance of
	 * {@link ResultMap}.<br>
	 * Note: The result set and statement object bound to this SQLExecutor
	 * instance is NOT close automatically. So you should call
	 * {@link SQLManager#close()} or {@link SQLExecutor#close()} finally like
	 * below:<br>
	 *
	 * <pre>
	 * SQLManager sqlManager = sqlManager();
	 * try {
	 * 	Condition cond = new Condition();
	 * 	cond.setType(&quot;A&quot;);
	 * 	for (ResultMap map : sqlManager.useFile(&quot;sql/Select.sql&quot;).each(cond)) {
	 * 		System.out.println(map.toString());
	 * 	}
	 * } finally {
	 * 	sqlManager.close();
	 * }
	 * </pre>
	 *
	 * @param <T>
	 * @param paramObj
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the iterator wrapper of result set
	 * @throws SQLRuntimeException
	 * @see {@link ParamMap#object(Object)}
	 * @see SQLIterator#iterator()
	 */
	public SQLIterator<ResultMap> each(Object paramObj) {
		return each(ResultMap.class, params(paramObj));
	}

	/**
	 * Executes the given SQL select statement and returns the iterator wrapper
	 * of result set object. The {@link Iterator#next()} returns the instance of
	 * the class specified.<br>
	 * Note: The result set and statement object bound to this SQLExecutor
	 * instance is NOT close automatically. So you should call
	 * {@link SQLManager#close()} or {@link SQLExecutor#close()} finally like
	 * below:<br>
	 *
	 * <pre>
	 * SQLManager sqlManager = sqlManager();
	 * try {
	 * 	for (Entity e : sqlManager.useFile(&quot;sql/Select.sql&quot;).each(Entity.class,
	 * 			params(&quot;type&quot;, &quot;A&quot;))) {
	 * 		System.out.println(e.toString());
	 * 	}
	 * } finally {
	 * 	sqlManager.close();
	 * }
	 * </pre>
	 *
	 * @param <T>
	 * @param entityClass
	 *            the class of result instance
	 * @param paramMap
	 *            the Map instance mapped to SQL parameters
	 * @return the iterator wrapper of result set
	 * @throws SQLRuntimeException
	 * @see SQLIterator#iterator()
	 */
	public <T> SQLIterator<T> each(Class<T> entityClass,
			Map<String, Object> paramMap) {
		try {
			return SQLIterator.genIterator(this, entityClass, paramMap);
		} catch (SQLException e) {
			throw new SQLRuntimeException(getSQLInfo(), e);
		}
	}

	/**
	 * Executes the given SQL insert/update/delete statement.
	 *
	 * @return the count of updated records
	 * @throws SQLRuntimeException
	 */
	public int update() {
		return this.update((Map<String, Object>) null);
	}

	/**
	 * Executes the given SQL insert/update/delete statement.
	 *
	 * @param paramObj
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the count of updated records
	 * @throws SQLRuntimeException
	 * @see {@link ParamMap#object(Object)}
	 */
	public int update(Object paramObj) {
		return this.update(params(paramObj));
	}

	/**
	 * Executes the given SQL insert/update/delete statement.
	 *
	 * @param paramMap
	 *            the Map instance mapped to SQL parameters
	 * @return the count of updated records
	 * @throws SQLRuntimeException
	 */
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

	/**
	 * Considers empty string as negative.<br>
	 *
	 * @return this
	 * @see SQLExecutor#asNegative(Object...)
	 */
	public SQLExecutor emptyAsNegative() {
		return asNegative("");
	}

	/**
	 * Considers given parameters as negative.<br>
	 * By default, negative values are below:<br>
	 * - null<br>
	 * - Boolean.FALSE<br>
	 * - empty list<br>
	 * - empty array<br>
	 * - a list contains negative value only<br>
	 * - a array contains negative value only<br>
	 *
	 * @param negativeValues
	 *            values to be considered as negative
	 * @return this
	 */
	public SQLExecutor asNegative(Object... negativeValues) {
		this.negativeValues = combine(this.negativeValues, negativeValues);
		return this;
	}

	/**
	 * Close the statement and the result set bound to this SQLExecuter
	 * instance.
	 */
	public void closeStatement() {
		new Using<Object>(rs, stmt) {
			@Override
			protected Object execute() throws Exception {
				return null; // do nothing.
			}

			@Override
			protected void finallyCallback() {
				manager.removeExecutor(SQLExecutor.this);
			}
		}.invoke();
	}

	/**
	 * Close the statement and the result set bound to this SQLExecuter
	 * instance.
	 *
	 * @see SQLExecutor#closeStatement()
	 */
	@Override
	public void close() {
		closeStatement();
	}

	/**
	 * Generates SQL.
	 *
	 * @return the SQL generated
	 */
	public String generateSql() {
		return generateSql(null);
	}

	/**
	 * Generates the SQL.
	 *
	 * @param paramObj
	 *            the object to be inspected and mapped to SQL parameters
	 * @return the SQL generated
	 * @see {@link ParamMap#object(Object)}
	 */
	public String generateSql(Object paramObj) {
		return generateSql(params(paramObj));
	}

	/**
	 * Generates the SQL.
	 *
	 * @param paramMap
	 *            the Map instance mapped to SQL parameters
	 * @return the SQL generated
	 */
	public String generateSql(Map<String, Object> paramMap) {
		setProductName(this.productName);
		addNegative(negativeValues);
		try {
			String sql = sqlGenerator.execute(paramMap, sqlNode);
			manager.setInfo(resourceInfo, sql, sqlGenerator.params);
			return sql;
		} finally {
			clear();
		}
	}

	/**
	 * Generates the {@link SQLSet}.
	 *
	 * @return {@link SQLSet} instance.(contains the SQL generated and its
	 *         parameters)
	 */
	public SQLSet generateSQLSet() {
		return generateSQLSet(null);
	}

	/**
	 * Generates the {@link SQLSet}.
	 *
	 * @param paramObj
	 *            the object to be inspected and mapped to SQL parameters
	 * @return {@link SQLSet} instance.(contains the SQL generated and its
	 *         parameters)
	 * @see {@link ParamMap#object(Object)}
	 */
	public SQLSet generateSQLSet(Object paramObj) {
		return generateSQLSet(params(paramObj));
	}

	/**
	 * Generates the {@link SQLSet}.
	 *
	 * @param paramMap
	 *            the Map instance mapped to SQL parameters
	 * @return {@link SQLSet} instance.(contains the SQL generated and its
	 *         parameters)
	 */
	public SQLSet generateSQLSet(Map<String, Object> paramMap) {
		String sql = generateSql(paramMap);
		return new SQLSet(sql, getParams());
	}

	/**
	 * Generates the {@link PreparedStatement} instance from the SQL bound to
	 * this.
	 *
	 * @return {@link PreparedStatement} instance
	 */
	public PreparedStatement generateStatment() {
		return genStmt(null);
	}

	/**
	 * Generates the {@link PreparedStatement} instance from the SQL bound to
	 * this and the parameters.
	 *
	 * @param paramObj
	 *            the object to be inspected and mapped to SQL parameters
	 * @return {@link PreparedStatement} instance
	 * @see {@link ParamMap#object(Object)}
	 */
	public PreparedStatement generateStatment(Object paramObj) {
		return genStmt(params(paramObj));
	}

	/**
	 * Generates the {@link PreparedStatement} instance from the SQL bound to
	 * this and the parameters.
	 *
	 * @param paramMap
	 *            the Map instance mapped to SQL parameters
	 * @return {@link PreparedStatement} instance
	 */
	public PreparedStatement generateStatment(Map<String, Object> paramMap) {
		try {
			stmt = manager.con().prepareStatement(generateSql(paramMap));
			int i = 1;
			for (Object param : this.sqlGenerator.params) {
				setJDBCData(stmt, param, i++);
			}
			return stmt;
		} catch (SQLException e) {
			throw new SQLRuntimeException(getSQLInfo(), e);
		}
	}

	@Override
	public int hashCode() {
		return hashValue;
	}

	String getSql() {
		return this.sqlGenerator.sql;
	}

	List<Object> getParams() {
		return this.sqlGenerator.params;
	}

	String getSQLInfo() {
		return genSQLInfo(getSql(), getParams(), getResourceInfo());
	}

	String getResourceInfo() {
		return this.resourceInfo;
	}

	PreparedStatement genStmt(Map<String, Object> paramMap) {
		manager.putExecutor(this);
		return generateStatment(paramMap);
	}
}
