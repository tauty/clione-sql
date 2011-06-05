package tetz42.clione;

import static tetz42.clione.SQLManager.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import tetz42.clione.util.ParamMap;
import tetz42.clione.util.ResultMap;

public class DBCopyOra {

	private static final String CRLF = System.getProperty("line.separator");

	public static void main(String[] args) {
		try {
			new DBCopyOra().dropTables();
			new DBCopyOra().copyTables();
			new DBCopyOra().copyDatas();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void dropTables() throws SQLException {
		SQLManager dstManager = sqlManager(getDstConnection());
		try {
			for (String tableName : dstManager.useSQL("select TNAME From tab")
					.each(String.class)) {
				// create
				if (tableName.startsWith("BIN"))
					continue;
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

	private void copyTables() throws SQLException {
		SQLManager srcManager = sqlManager(getSrcConnection());
		SQLManager dstManager = sqlManager(getDstConnection());
		try {
			for (String tableName : srcManager.useSQL("select TNAME From tab")
					.each(String.class)) {
				// create
				StringBuilder createSQL = new StringBuilder();
				createSQL.append("CREATE TABLE ").append(tableName).append("(")
						.append(CRLF);
				for (ResultMap map : srcManager.useSQL(
						"select * from USER_TAB_COLUMNS where TABLE_NAME = '"
								+ tableName + "' order by COLUMN_ID").each()) {
					Object type = map.get("DATA_TYPE");
					createSQL.append("\t").append(map.get("COLUMN_NAME"))
							.append("\t").append(type);
					if (!"DATE".equals(type) && !"BLOB".equals(type)
							&& !"CLOB".equals(type))
						createSQL.append("(").append(map.get("DATA_LENGTH"))
								.append(")");
					if (!isEmpty(map.get("DATA_DEFAULT")))
						createSQL.append("\t").append("DEFAULT ")
								.append(map.get("DATA_DEFAULT"));
					if ("Y".equals(map.get("NULLABLE")))
						createSQL.append("\t").append("NOT NULL");
					// if (map.get("Extra") != null
					// && !"".equals(map.get("Extra")))
					// createSQL.append("\t").append(map.get("Extra"));
					createSQL.append(",").append(CRLF);
				}
				createSQL.append("\t").append("PRIMARY\tKEY(");
				for (String key : srcManager
						.useSQL("select COLUMN_NAME from USER_CONSTRAINTS c, USER_CONS_COLUMNS uc"
								+ " where c.TABLE_NAME = '"
								+ tableName
								+ "' and c.CONSTRAINT_TYPE = 'P'"
								+ " and c.CONSTRAINT_NAME = uc.CONSTRAINT_NAME")
						.each(String.class)) {
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

	private void copyDatas() throws SQLException {
		SQLManager srcManager = sqlManager(getSrcConnection());
		SQLManager dstManager = sqlManager(getDstConnection());
		try {
			for (String tableName : srcManager.useSQL("select TNAME From tab")
					.each(String.class)) {
				System.out.println("[[[" + tableName + "]]]");
				// create
				for (ResultMap map : srcManager.useSQL(
						"select * from " + tableName).each()) {
					StringBuilder insertSQL = new StringBuilder();
					StringBuilder valuesSQL = new StringBuilder();
					ParamMap paramMap = new ParamMap();
					insertSQL.append("INSERT INTO ").append(tableName)
							.append("(").append(CRLF);
					for (Map.Entry<String, Object> e : map.entrySet()) {
						insertSQL.append("\t").append(e.getKey()).append(",")
								.append(CRLF);
						valuesSQL.append("\t").append("/* ").append(e.getKey())
								.append(" */,").append(CRLF);
						paramMap.$(e.getKey(), e.getValue());
					}
					valuesSQL.deleteCharAt(valuesSQL.length() - 3);
					insertSQL.deleteCharAt(insertSQL.length() - 3)
							.append(") VALUES( ").append(CRLF)
							.append(valuesSQL).append(")");
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
				"jdbc:oracle:thin:@10.23.1.208:1523:LISNARDB", "CSX_UT_OOTA",
				"CSX_UT_OOTA");
		con.setAutoCommit(false);
		return con;
	}

	private Connection getDstConnection() throws SQLException {
		Connection con = DriverManager.getConnection(
				"jdbc:oracle:thin:@10.23.1.208:1523:LISNARDB",
				"TSY_LEVEL_UP_OODA", "TSY_LEVEL_UP_OODA");
		con.setAutoCommit(false);
		return con;
	}
}
