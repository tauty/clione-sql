package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class StrLiteral extends ClioneFunction {

	private String str;

	public StrLiteral(String str) {
		this.str = str;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction inst = new Instruction();
		inst.replacement = str;
		return inst.next(getNextInstruction(paramMap));
	}

	@Override
	public String getSrc() {
		return "'" + str + "'";
	}
}
