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

import static tetz42.clione.loader.LoaderUtil.*;
import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.util.Util.*;

import java.io.Closeable;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tetz42.clione.exception.ConnectionNotFoundException;
import tetz42.clione.loader.LoaderUtil;
import tetz42.clione.util.ParamMap;
import tetz42.util.exception.SQLRuntimeException;

public class SQLManager implements Closeable {

	private static ThreadLocal<Connection> tcon = new ThreadLocal<Connection>();

	public static SQLManager sqlManager() {
		return new SQLManager();
	}

	public static SQLManager sqlManager(Connection con) {
		return new SQLManager(con);
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

	/**
	 *
	 * @param <T>
	 *            note: This is not used, but required for avoiding warning.
	 * @param key
	 * @param values
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "varargs" })
	public static <T> ParamMap params(String key, T... values) {
		return params().$(key, values);
	}

	public static ParamMap params(Object obj) {
		return params().object(obj);
	}

	public static ParamMap paramsOn(String... keys) {
		return params().$on(keys);
	}

	public static String getSQLPath(Class<?> clazz, String sqlFileName) {
		return LoaderUtil.getSQLPath(clazz, sqlFileName);
	}

	private final Connection con;
	private final String productName;
	private HashSet<SQLExecutor> processingExecutorSet = new HashSet<SQLExecutor>();
	private String resourceInfo;
	private String executedSql;
	private List<Object> executedParams;
	private Object[] negativeValues = null;

	public SQLManager() {
		this(null);
	}

	public SQLManager(Connection con) {
		if (con != null)
			this.con = con;
		else
			this.con = getThreadConnection();
		if (this.con != null) {
			try {
				DatabaseMetaData metaData = this.con.getMetaData();
				String name = metaData.getDatabaseProductName();
				productName = name == null ? name : name.toLowerCase();
			} catch (SQLException e) {
				throw new SQLRuntimeException(e);
			}
		} else {
			productName = null;
		}
	}

	public SQLManager emptyAsNegative() {
		return asNegative("");
	}

	public SQLManager asNegative(Object... negativeValues) {
		this.negativeValues = combine(this.negativeValues, negativeValues);
		return this;
	}

	public String getSQLInfo() {
		return genSQLInfo(getSql(), getParams(), getResourceInfo());
	}

	public String getResourceInfo() {
		return this.resourceInfo;
	}

	public String getSql() {
		return this.executedSql;
	}

	public List<Object> getParams() {
		return this.executedParams;
	}

	public void closeStatement() {
		ArrayList<SQLExecutor> list = new ArrayList<SQLExecutor>(
				processingExecutorSet);
		for (SQLExecutor executor : list)
			executor.closeStatement();
	}

	public SQLExecutor useSQL(String sql) {
		SQLExecutor sqlExecutor = new SQLExecutor(this, getNodeBySQL(sql));
		// TODO better solution.
		this.resourceInfo = sqlExecutor.resourceInfo;
		return sqlExecutor;
	}

	public SQLExecutor useFile(Class<?> clazz, String sqlFile) {
		SQLExecutor sqlExecutor = new SQLExecutor(this, getNodeByClass(clazz,
				sqlFile, productName));
		// TODO better solution.
		this.resourceInfo = sqlExecutor.resourceInfo;
		return sqlExecutor;
	}

	public SQLExecutor useFile(String sqlPath) {
		SQLExecutor sqlExecutor = new SQLExecutor(this, getNodeByPath(sqlPath,
				productName));
		// TODO better solution.
		this.resourceInfo = sqlExecutor.resourceInfo;
		return sqlExecutor;
	}

	public SQLExecutor useStream(InputStream in) {
		SQLExecutor sqlExecutor = new SQLExecutor(this, getNodeByStream(in));
		// TODO better solution.
		this.resourceInfo = sqlExecutor.resourceInfo;
		return sqlExecutor;
	}

	public Connection con() {
		Connection con = this.con != null ? this.con : tcon.get();
		if (con == null)
			throw new ConnectionNotFoundException("No connection!");
		return con;
	}

	public void closeConnection() {
		closeStatement();
		Connection con = con();
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				throw new SQLRuntimeException(e);
			}
		}
	}

	@Override
	public void close() {
		closeConnection();
	}

	Object[] getNegativeValues() {
		return negativeValues;
	}

	void putExecutor(SQLExecutor executor) {
		this.processingExecutorSet.add(executor);
	}

	void removeExecutor(SQLExecutor executor) {
		this.processingExecutorSet.remove(executor);
	}

	void setInfo(String resourceInfo, String sql, List<Object> params) {
		this.resourceInfo = resourceInfo;
		this.executedSql = sql;
		this.executedParams = params;
	}

	String getProductName() {
		return productName;
	}
}
