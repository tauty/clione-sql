package tetz42.clione;

import static tetz42.clione.SQLManager.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import tetz42.clione.SQLManager;
import tetz42.clione.util.ParamMap;

public class DBCopy {

	private static final String CRLF = System.getProperty("line.separator");

	public static void main(String[] args) {
		try {
			new DBCopy().dropTables();
			new DBCopy().copyTables();
			new DBCopy().copyDatas();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void dropTables() throws SQLException {
		SQLManager dstManager = sqlManager(getDstConnection());
		try {
			for (String tableName : dstManager.useSQL("show tables").each(
					String.class)) {
				// create
				StringBuilder dropSQL = new StringBuilder();
				dropSQL.append("DROP TABLE ").append(tableName).append(CRLF);
				System.out.println(dropSQL);
				dstManager.useSQL(dropSQL.toString()).update();
			}
			dstManager.con().commit();
		} finally {
			dstManager.closeStatement();
		}
	}

	@SuppressWarnings("unchecked")
	private void copyTables() throws SQLException {
		SQLManager srcManager = sqlManager(getSrcConnection());
		SQLManager dstManager = sqlManager(getDstConnection());
		try {
			for (String tableName : srcManager.useSQL("show tables").each(
					String.class)) {
				// create
				StringBuilder createSQL = new StringBuilder();
				createSQL.append("CREATE TABLE ").append(tableName).append("(")
						.append(CRLF);
				ArrayList<String> primaries = new ArrayList<String>();
				for (Map map : srcManager.useSQL(
						"show fields from " + tableName).each()) {
					createSQL.append("\t").append(map.get("Field"))
							.append("\t").append(map.get("Type"));
					if (!isEmpty(map.get("Default")))
						createSQL.append("\t").append("DEFAULT ").append(
								map.get("Default"));
					if ("NO".equals(map.get("Null")))
						createSQL.append("\t").append("NOT NULL");
					if ("PRI".equals(map.get("Key")))
						primaries.add(String.valueOf(map.get("Field")));
					if (map.get("Extra") != null
							&& !"".equals(map.get("Extra")))
						createSQL.append("\t").append(map.get("Extra"));
					createSQL.append(",").append(CRLF);
				}
				createSQL.append("\t").append("PRIMARY\tKEY(");
				for (String key : primaries) {
					createSQL.append(key).append(",");
				}
				createSQL.deleteCharAt(createSQL.length() - 1).append(")")
						.append(CRLF).append(")");
				System.out.println(createSQL);
				dstManager.useSQL(createSQL.toString()).update();
			}
			dstManager.con().commit();
		} finally {
			srcManager.closeStatement();
			dstManager.closeStatement();
		}
	}

	@SuppressWarnings("unchecked")
	private void copyDatas() throws SQLException {
		SQLManager srcManager = sqlManager(getSrcConnection());
		SQLManager dstManager = sqlManager(getDstConnection());
		try {
			for (String tableName : srcManager.useSQL("show tables").each(
					String.class)) {
				System.out.println("[[[" + tableName + "]]]");
				// create
				for (Map map : srcManager.useSQL("select * from " + tableName)
						.each()) {
					StringBuilder insertSQL = new StringBuilder();
					StringBuilder valuesSQL = new StringBuilder();
					ParamMap paramMap = new ParamMap();
					insertSQL.append("INSERT INTO ").append(tableName).append(
							"(").append(CRLF);
					for (Object o : map.entrySet()) {
						Map.Entry<String, Object> e = (Map.Entry<String, Object>) o;
						insertSQL.append("\t").append(e.getKey()).append(",")
								.append(CRLF);
						valuesSQL.append("\t").append("/* ").append(e.getKey())
								.append(" */,").append(CRLF);
						paramMap.$(e.getKey(), e.getValue());
					}
					valuesSQL.deleteCharAt(valuesSQL.length() - 3);
					insertSQL.deleteCharAt(insertSQL.length() - 3).append(
							") VALUES( ").append(CRLF).append(valuesSQL)
							.append(")");
					System.out.println(insertSQL);
					dstManager.useSQL(insertSQL.toString()).update(paramMap);
				}
			}
			dstManager.con().commit();
		} finally {
			srcManager.closeStatement();
			dstManager.closeStatement();
		}
	}

	private boolean isEmpty(Object obj) {
		return obj == null ? true : "".equals(obj);
	}

	private Connection getSrcConnection() throws SQLException {
		Connection con = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/TEST1", "TEST1", "TEST1");
		con.setAutoCommit(false);
		return con;
	}

	private Connection getDstConnection() throws SQLException {
		Connection con = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/TEST2", "TEST2", "TEST2");
		con.setAutoCommit(false);
		return con;
	}
}
