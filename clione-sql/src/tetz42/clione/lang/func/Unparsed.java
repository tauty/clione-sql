package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class Unparsed extends ClioneFunction {
	
	private String unparsedStr;

	public Unparsed(String unparsedStr){
		this.unparsedStr = unparsedStr;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return unparsedStr;
	}
}
