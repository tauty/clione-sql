package tetz42.clione.lang.func;

public class EscapedSQLLiteral extends SQLLiteral {

	public EscapedSQLLiteral(String literal) {
		super(literal);
	}

	@Override
	public String getSrc() {
		return "\"" + literal + "\"";
	}
}
