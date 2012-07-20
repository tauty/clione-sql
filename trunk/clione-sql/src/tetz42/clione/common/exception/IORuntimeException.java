package tetz42.clione.common.exception;

public class IORuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2505946068229719412L;

	private static final String CRLF = System.getProperty("line.separator");

	public IORuntimeException() {
		super();
	}

	public IORuntimeException(String msg) {
		super(msg);
	}

	public IORuntimeException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public IORuntimeException(String msg, Throwable cause) {
		super(msg + CRLF + cause.getMessage(), cause);
	}

}
