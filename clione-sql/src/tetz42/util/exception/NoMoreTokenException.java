package tetz42.util.exception;

import tetz42.util.Const;


public class NoMoreTokenException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -1027160501289177579L;

	public NoMoreTokenException(String src) {
		super("No token is available. The source string is :" + Const.CRLF + src);
	}

}
