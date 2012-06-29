package tetz42.clione.exception;

public class DuplicateKeyException extends RuntimeException {

	/***/
	private static final long serialVersionUID = 1L;

	public DuplicateKeyException(String msg) {
		super(msg);
	}

}
