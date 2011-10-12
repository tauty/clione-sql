package tetz42.clione.node;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class EmptyLineNode extends LineNode {

	public EmptyLineNode(int lineNo) {
		super(lineNo);
	}

	public EmptyLineNode(int startNo, int endNo) {
		super(startNo, endNo);
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		return new Instruction().replacement(" ");
	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}
