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
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import tetz42.clione.util.ParamMap;

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

	// Utility methods are follows:
	
	public static ParamMap convRequest(HttpServletRequest req) {
		ParamMap map = new ParamMap();
		map.putAll(convReqParams(req));
		map.putAll(convSessions(req));
		map.putAll(convReqAttrs(req));
		return map;
	}

	public static ParamMap convReqParams(ServletRequest req) {
		ParamMap map = new ParamMap();

		@SuppressWarnings("rawtypes")
		Enumeration names = req.getParameterNames();

		while (names.hasMoreElements()) {
			String name = String.valueOf(names.nextElement());
			String[] values = req.getParameterValues(name);
			if (values.length == 1)
				map.put(name, values[0]);
			else
				map.put(name, values);
		}
		return map;
	}

	public static ParamMap convReqAttrs(ServletRequest req) {
		ParamMap map = new ParamMap();

		@SuppressWarnings("rawtypes")
		Enumeration names = req.getAttributeNames();

		while (names.hasMoreElements()) {
			String name = String.valueOf(names.nextElement());
			map.put(name, req.getAttribute(name));
		}
		return map;
	}

	public static ParamMap convSessions(HttpServletRequest req) {
		ParamMap map = null;
		HttpSession session = req.getSession(false);
		if (session != null)
			map = convSessions(session);
		return map != null ? map : new ParamMap();
	}

	public static ParamMap convSessions(HttpSession session) {
		ParamMap map = new ParamMap();
		
		@SuppressWarnings("rawtypes")
		Enumeration names = session.getAttributeNames();
		
		while (names.hasMoreElements()) {
			String name = String.valueOf(names.nextElement());
			map.put(name, session.getAttribute(name));
		}
		return map;
	}
}
