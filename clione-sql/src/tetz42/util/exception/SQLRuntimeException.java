package tetz42.util.exception;

import static tetz42.util.Const.*;

import java.sql.SQLException;

public class SQLRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 8654542030680560507L;

	public SQLRuntimeException(SQLException e) {
		super(e.getMessage(), e);
	}

	public SQLRuntimeException(String msg, SQLException e) {
		super(msg + CRLF + e.getMessage(), e);
	}

	public SQLException getSQLException() {
		return (SQLException) getCause();
	}
}
