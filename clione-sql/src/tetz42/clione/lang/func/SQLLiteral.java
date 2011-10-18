package tetz42.clione.lang.func;

import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.lang.Instruction;
import tetz42.clione.node.SQLNode;
import tetz42.clione.parsar.SQLParser;
import tetz42.clione.util.ParamMap;

public class SQLLiteral extends ClioneFunction {

	private final SQLNode sqlNode;
	protected final String literal;

	public SQLLiteral(String literal) {
		this.literal = literal;
		// TODO better resource comment
		this.sqlNode = new SQLParser("Inside of SQL comment ").parse(literal);
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction inst = new Instruction();
		SQLGenerator sqlGenerator = new SQLGenerator();
		inst.replacement = sqlGenerator.genSql(paramMap, this.sqlNode);
		if (sqlGenerator.params != null && sqlGenerator.params.size() != 0) {
			inst.params.addAll(sqlGenerator.params);
		}
		return inst.next(getNextInstruction(paramMap));
	}

	@Override
	public String getSrc() {
		return "\"" + literal + "\"";
	}

	@Override
	public String getLiteral() {
		return literal;
	}
}
