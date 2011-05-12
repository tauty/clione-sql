package tetz42.clione.lang.func;

import java.io.StringReader;
import java.util.List;

import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.lang.Instruction;
import tetz42.clione.node.LineNode;
import tetz42.clione.parsar.SQLParser;
import tetz42.clione.util.ParamMap;

public class SQLLiteral extends ClioneFunction {

	private final List<LineNode> nodes;
	private final String literal;

	public SQLLiteral(String literal) {
		this.literal = literal.replaceAll("\\\\(.)", "$1");
		StringReader reader = new StringReader(this.literal);
		// TODO resourceInfo is null. fix this bug.
		this.nodes = new SQLParser(resourceInfo).parse(reader);
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction instruction = getInstruction(paramMap);
		SQLGenerator sqlGenerator = new SQLGenerator(null);
		instruction.replacement = sqlGenerator.genSql(paramMap, this.nodes);
		if (sqlGenerator.params != null && sqlGenerator.params.size() != 0) {
			instruction.params.addAll(sqlGenerator.params);
		}
		return instruction;
	}

	@Override
	public String getSrc() {
		return "\"" + literal + "\"";
	}
}
