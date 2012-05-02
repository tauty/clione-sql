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
		return new Instruction().replacement(str).next(
				getNextInstruction(paramMap));
	}

	@Override
	public String getSrc() {
		return "'" + str + "'";
	}

	@Override
	public String getLiteral() {
		return str;
	}

	@Override
	public String toString() {
		return "\"" + str + "\"";
	}

}
