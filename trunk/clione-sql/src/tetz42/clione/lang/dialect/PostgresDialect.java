package tetz42.clione.lang.dialect;

public class PostgresDialect extends Dialect {
	@Override
	public boolean backslashWorkAsEscape() {
		return true;
	}
}
