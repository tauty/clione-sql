package tetz42.clione.lang;

import java.io.StringReader;
import java.util.List;

import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.node.LineNode;
import tetz42.clione.parsar.SQLParser;
import tetz42.clione.util.ParamMap;

public class SQLLiteral extends ClioneFunction {
	
	private final List<LineNode> nodes;
	private final boolean isTerminated;
	
	public SQLLiteral(String literal, boolean isTerminated){
		System.out.println("\tliteral=" + literal);
		StringReader reader = new StringReader(literal);
		this.nodes = new SQLParser(resourceInfo).parse(reader);
		this.isTerminated = isTerminated;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction instruction = getInstruction(paramMap);
		SQLGenerator sqlGenerator = new SQLGenerator(null);
		instruction.replacement = sqlGenerator.genSql(paramMap, this.nodes);
		if(sqlGenerator.params != null && sqlGenerator.params.size() != 0){
			instruction.params.addAll(sqlGenerator.params);
		}
		return instruction;
	}
	
	@Override
	protected boolean isTerminated() {
		return isTerminated;
	}
}
