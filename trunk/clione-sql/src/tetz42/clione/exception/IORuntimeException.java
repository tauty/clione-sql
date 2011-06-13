package tetz42.clione.exception;

public class IORuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2505946068229719412L;

	public IORuntimeException() {
		super();
	}

	public IORuntimeException(String msg) {
		super(msg);
	}

	public IORuntimeException(Throwable cause) {
		super(cause);
	}

	public IORuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
