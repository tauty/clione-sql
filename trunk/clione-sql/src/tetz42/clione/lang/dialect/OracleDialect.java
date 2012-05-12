package tetz42.clione.lang.dialect;

public class OracleDialect extends Db2Dialect {

	@Override
	public int inLimit() {
		return 1000;
	}
}
