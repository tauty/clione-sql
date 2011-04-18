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
		Instruction instruction = getInstruction(paramMap);
		instruction.replacement = instruction.replacement == null ? this.str
				: this.str + instruction.replacement;
		return instruction;
	}

	@Override
	public String toString() {
		return "'" + str + "'";
	}
}
