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
import java.util.HashSet;
import java.util.List;

import tetz42.clione.exception.ConnectionNotFoundException;
import tetz42.clione.util.Config;
import tetz42.clione.util.ParamMap;
import tetz42.util.Using;

public class SQLManager implements Closeable {

	/**
	 * Enumeration of RDBMS Product.
	 *
	 * @author tetz
	 */
	public static enum Product {
		ORACLE, SQLSERVER, DB2, MYSQL, FIREBIRD, POSTGRES, SQLITE
	}

	private static ThreadLocal<Connection> tcon = new ThreadLocal<Connection>();

	/**
	 * Generates SQLManager instance.<br>
	 * The instance generated would use the connection passed through
	 * SQLManager.setThreadConnection(Connection) if it is available.<br>
	 * It will be determined by Config#DBMS_PRODUCT_NAME or the value obtained
	 * by DatabaseMetaData#getDatabaseProductName() what RDBMS products are.
	 *
	 * @return SQLManager instance
	 * @see SQLManager#setThreadConnection(Connection)
	 * @see Config#DBMS_PRODUCT_NAME
	 */
	public static SQLManager sqlManager() {
		return new SQLManager(null, (String) null);
	}

	/**
	 * Generates SQLManager instance.<br>
	 * It will be determined by Config#DBMS_PRODUCT_NAME or the value obtained
	 * by DatabaseMetaData#getDatabaseProductName() what RDBMS products are.
	 *
	 * @param con
	 *            connection
	 * @return SQLManager instance
	 * @see Config#DBMS_PRODUCT_NAME
	 */
	public static SQLManager sqlManager(Connection con) {
		return new SQLManager(con, (String) null);
	}

	/**
	 * Generates SQLManager instance.<br>
	 * The instance generated would use the connection passed through
	 * SQLManager.setThreadConnection(Connection) if it is available.<br>
	 *
	 * @param product
	 *            RDBMS product
	 * @return SQLManager instance
	 * @see SQLManager#setThreadConnection(Connection)
	 */
	public static SQLManager sqlManager(Product product) {
		return new SQLManager(null, product);
	}

	/**
	 * Generates SQLManager instance.<br>
	 * The instance generated would use the connection passed through
	 * SQLManager.setThreadConnection(Connection) if it is available.<br>
	 *
	 * @param productName
	 *            RDBMS product name. ex) oracle, mysql, db2, and so on.
	 * @return SQLManager instance
	 * @see SQLManager#setThreadConnection(Connection)
	 */
	public static SQLManager sqlManager(String productName) {
		return new SQLManager(null, productName);
	}

	/**
	 * Generates SQLManager instance.<br>
	 *
	 * @param con
	 *            connection
	 * @param product
	 *            RDBMS product
	 * @return SQLManager instance
	 */
	public static SQLManager sqlManager(Connection con, Product product) {
		return new SQLManager(con, product);
	}

	/**
	 * Generates SQLManager instance.<br>
	 *
	 * @param con
	 *            connection
	 * @param productName
	 *            RDBMS product name. ex) oracle, mysql, db2, and so on.
	 * @return SQLManager instance
	 */
	public static SQLManager sqlManager(Connection con, String productName) {
		return new SQLManager(con, productName);
	}

	/**
	 * Registers the connection with thread local variable.<br>
	 * Note: If you register a connection using this method,
	 * SQLManager.setThreadConnection(null) should be called before the thread
	 * ends.
	 *
	 * @param con
	 *            connection
	 */
	public static void setThreadConnection(Connection con) {
		tcon.set(con);
	}

	/**
	 * Obtains the connection registered by
	 * SQLManager#setThreadConnection(Connection).
	 *
	 * @return connection
	 * @see SQLManager#setThreadConnection(Connection)
	 */
	public static Connection getThreadConnection() {
		return tcon.get();
	}

	/**
	 * Generates ParamMap instance.
	 *
	 * @return ParamMap instance
	 */
	public static ParamMap params() {
		return new ParamMap();
	}

	/**
	 * Generates ParamMap instance and registers the key and value.
	 *
	 * @return ParamMap instance
	 * @see ParamMap#$(String, Object)
	 */
	public static ParamMap params(String key, Object value) {
		return params().$(key, value);
	}

	/**
	 * Generates ParamMap instance, inspects the parameter object, and registers
	 * its result.
	 *
	 * @param obj
	 *            object
	 * @return ParamMap instance
	 * @see ParamMap#object(Object)
	 */
	public static ParamMap params(Object obj) {
		return params().object(obj);
	}

	public static ParamMap paramsOn(String... keys) {
		return params().$on(keys);
	}

	private final Connection con;
	private final String productName;
	private final HashSet<SQLExecutor> processingExecutorSet = new HashSet<SQLExecutor>();
	private String resourceInfo;
	private String executedSql;
	private List<Object> executedParams;
	private Object[] negativeValues = null;

	private SQLManager(Connection con, Product product) {
		this.con = getCon(con);
		this.productName = product.name().toLowerCase();
	}

	private SQLManager(Connection con, String productName) {
		this.con = getCon(con);

		if (productName == null)
			productName = Config.get().DBMS_PRODUCT_NAME;

		if (this.con != null && productName == null) {
			try {
				productName = toProduct(this.con.getMetaData()
						.getDatabaseProductName());
			} catch (Exception ignore) {
			}
		}
		this.productName = productName;
	}

	private static String toProduct(String productName) {
		if (productName == null)
			return null;
		for (Product product : Product.values()) {
			String token = product == Product.SQLSERVER ? "sql server"
					: product.name().toLowerCase();
			if (productName.toLowerCase().contains(token))
				return product.name();
		}
		return null;
	}

	private Connection getCon(Connection con) {
		return con != null ? con : getThreadConnection();
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
		if (this.con == null)
			throw new ConnectionNotFoundException("No connection!");
		return this.con;
	}

	public void closeStatement() {
		new Using<Object>(processingExecutorSet) {
			@Override
			protected Object execute() throws Exception {
				return null; // do nothing.
			}
		}.invoke();
	}

	public void closeConnection() {
		new Using<Object>(processingExecutorSet, con()) {
			@Override
			protected Object execute() throws Exception {
				return null; // do nothing.
			}
		}.invoke();
	}

	@Override
	public void close() {
		closeStatement();
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

	public static class SqlAndParam {
		public final String sql;
		public final List<Object> params;

		public SqlAndParam(String sql, List<Object> params) {
			this.sql = sql;
			this.params = params;
		}
	}
}
