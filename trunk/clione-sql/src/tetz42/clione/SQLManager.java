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
import java.sql.Connection;
import java.sql.SQLException;

import tetz42.clione.module.ParamMap;

public class SQLManager {

	private static ThreadLocal<Connection> tcon = new ThreadLocal<Connection>();

	public static SQLManager sqlManager() {
		// TODO add instance management logic
		return new SQLManager();
	}

	public static SQLManager sqlManager(Connection con) {
		// TODO add instance management logic
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

	public static ParamMap params(Object obj) {
		return params().object(obj);
	}
	
	final Connection con;
	
	public SQLManager() {
		this.con = null;
	}

	public SQLManager(Connection con) {
		this.con = con;
	}

	public SQLExecutor useFile(Class<?> clazz, String sqlFile){
		InputStream in = clazz.getResourceAsStream(sqlFile);
		if (in == null)
			throw new NullPointerException(
					"SQL File might not be found. file name:" + sqlFile
							+ ", class:" + clazz.getName());
		return new SQLExecutor(this.con(), in);
	}
	
	public SQLExecutor useFile(String sqlPath){
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				sqlPath);
		if (in == null)
			throw new NullPointerException("SQL File might not be found. path:"
					+ sqlPath);
		return new SQLExecutor(this.con(), in);
	}
	
	public SQLExecutor useStream(InputStream in){
		if (in == null)
			throw new NullPointerException("The in parameter is null.");
		return new SQLExecutor(this.con(), in);
	}
	
	public Connection con() {
		Connection con = this.con != null ? this.con : tcon.get();
		if (con == null)
			throw new NullPointerException("No connection is available!");
		return con;
	}

	public void closeConnection() throws SQLException {
		Connection con = con();
		if (con != null)
			con.close();
	}
}
