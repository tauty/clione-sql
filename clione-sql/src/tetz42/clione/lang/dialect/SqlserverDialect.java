package tetz42.clione.lang.dialect;

public class SqlserverDialect extends Dialect {

	@Override
	public String needLikeEscape() {
		return super.needLikeEscape() + "\\[";
	}
}
