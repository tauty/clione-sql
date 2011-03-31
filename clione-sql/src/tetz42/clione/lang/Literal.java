package tetz42.clione.lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tetz42.clione.node.LineNode;
import tetz42.clione.util.ParamMap;

public class Literal extends Clione {
	
	private final String literal;
	private final boolean isTerminated;
	
	public Literal(String literal, boolean isTerminated){
		this.literal = literal;
		this.isTerminated = isTerminated;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		LineNode node = new LineNode();
		
		// TODO implement literal parsing.
		node.sql.append(this.literal);
		List<LineNode> list = Arrays.asList(node);
		
		Instruction instruction = getInstruction(paramMap);
		instruction.replacement = list;
		return instruction;
	}
	
	@Override
	protected boolean isTerminated() {
		return isTerminated;
	}
}
