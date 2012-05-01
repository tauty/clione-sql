package tetz42.clione.lang.dialect;

public class Db2Dialect extends Dialect {
	@Override
	public String needLikeEscape() {
		return super.needLikeEscape() + "％＿";
	}
}
