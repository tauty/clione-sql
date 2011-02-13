package tetz42.clione.exception;

public class ConnectionNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5766315563857715696L;

	public ConnectionNotFoundException(String msg) {
		super(msg);
	}
}
