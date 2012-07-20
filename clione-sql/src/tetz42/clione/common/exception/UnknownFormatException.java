package tetz42.clione.common.exception;

public class UnknownFormatException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String CRLF = System.getProperty("line.separator");

	public UnknownFormatException(String msg) {
		super(msg);
	}

	public UnknownFormatException(Throwable e) {
		super(e.getMessage(), e);
	}

	public UnknownFormatException(String msg, Throwable e) {
		super(msg + CRLF + e.getMessage(), e);
	}

}
