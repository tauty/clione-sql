package tetz42.clione.exception;

public class ImpossibleToCompareException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5766315563857715696L;

	public ImpossibleToCompareException(String msg) {
		super(msg);
	}

	public ImpossibleToCompareException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
