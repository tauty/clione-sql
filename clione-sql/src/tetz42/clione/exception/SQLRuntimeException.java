package tetz42.clione.exception;

import java.sql.SQLException;

public class SQLRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3990864477820776624L;

	public SQLRuntimeException(SQLException e) {
		super(e.getMessage(), e);
	}
	
	public SQLRuntimeException(String msg, SQLException e) {
		super(msg, e);
	}
}
