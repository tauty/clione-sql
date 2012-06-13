package tetz42.clione.lang.dialect;

import java.util.regex.Pattern;

public class Dialect {

	private static final Pattern basicEscapePtn = Pattern.compile("([#%_])");

	public Pattern needLikeEscape() {
		return basicEscapePtn;
	}

	public int inLimit() {
		return Integer.MAX_VALUE;
	}

	public boolean backslashWorkAsEscape() {
		return false;
	}
}
