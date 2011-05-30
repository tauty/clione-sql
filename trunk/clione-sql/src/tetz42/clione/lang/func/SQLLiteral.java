package tetz42.clione.lang.func;

import java.io.StringReader;
import java.util.List;

import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.lang.Instruction;
import tetz42.clione.node.LineNode;
import tetz42.clione.parsar.SQLParser;
import tetz42.clione.util.ParamMap;

public class SQLLiteral extends ClioneFunction {

	private List<LineNode> nodes;
	private final String literal;

	public SQLLiteral(String literal) {
		this.literal = literal;
	}
	
	@Override
	public ClioneFunction resourceInfo(String resourceInfo) {
		StringReader reader = new StringReader(this.literal);
		this.nodes = new SQLParser(resourceInfo).parse(reader);
		return super.resourceInfo(resourceInfo);
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction inst = new Instruction();
		// TODO nullValues
		SQLGenerator sqlGenerator = new SQLGenerator();
		inst.replacement = sqlGenerator.genSql(paramMap, this.nodes);
		if (sqlGenerator.params != null && sqlGenerator.params.size() != 0) {
			inst.params.addAll(sqlGenerator.params);
		}
		return inst.next(getNextInstruction(paramMap));
	}

	@Override
	public String getSrc() {
		return "\"" + literal + "\"";
	}
}
