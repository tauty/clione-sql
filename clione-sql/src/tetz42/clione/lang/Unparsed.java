package tetz42.clione.lang;

import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public class Unparsed extends ClioneFunction {

	private String unparsedStr;

	public Unparsed(String unparsedStr) {
		this.unparsedStr = unparsedStr;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		return null;
	}
	
	@Override
	public String getSrc() {
		return unparsedStr;
	}

	@Override
	public String toString() {
		return unparsedStr;
	}
}
