package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
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

	@Override
	public ClioneFunction inside(ClioneFunction inside) {
		this.inside = inside;
		return this;
	}

	@Override
	public ClioneFunction getInside() {
		return inside;
	}
	
	@Override
	public String getSrc() {
		StringBuilder sb = new StringBuilder("(");
		ClioneFunction cf = inside;
		while(cf != null){
			if(sb.length() != 1)
				sb.append(" ");
			sb.append(cf.getSrc());
			cf = cf.getNext();
		}
		return sb.append(")").toString();
	}
	

}
