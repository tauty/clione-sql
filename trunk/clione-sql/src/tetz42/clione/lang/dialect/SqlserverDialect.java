package tetz42.clione.lang.dialect;

import java.util.regex.Pattern;

public class SqlserverDialect extends Dialect {

	protected static final Pattern escapePtn = Pattern.compile("([#%_\\[])");

	@Override
	public Pattern needLikeEscape() {
		return escapePtn;
	}
}
