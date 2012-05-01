package tetz42.clione.lang.dialect;

public class OracleDialect extends Dialect {

	@Override
	public String needLikeEscape() {
		return super.needLikeEscape() + "％＿";
	}

	@Override
	public int inLimit() {
		return 1000;
	}

}
