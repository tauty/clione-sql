package tetz42.util.exception;


public class SQLRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3990864477820776624L;

	private static final String CRLF = System.getProperty("line.separator");
	
	public SQLRuntimeException(Throwable e) {
		super(e.getMessage(), e);
	}
	
	public SQLRuntimeException(String msg, Throwable e) {
		super(msg + CRLF + e.getMessage(), e);
	}
}
