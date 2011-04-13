package tetz42.clione.lang;

import tetz42.clione.util.ParamMap;

public class Parenthesises extends ClioneFunction {
	
	private ClioneFunction inside;
	
	public Parenthesises(ClioneFunction inside) {
		this.inside = inside;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
