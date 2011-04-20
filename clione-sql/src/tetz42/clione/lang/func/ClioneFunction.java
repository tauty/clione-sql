package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public abstract class ClioneFunction {

	private ClioneFunction next;
	protected String resourceInfo;

	public ClioneFunction nextFunc(ClioneFunction next) {
		this.next = next;
		return this;
	}
	
	public ClioneFunction getNext(){
		return this.next;
	}
	
	public ClioneFunction inside(ClioneFunction inside) {
		return null;
	}
	
	public ClioneFunction getInside(){
		return null;
	}
	
	public ClioneFunction resourceInfo(String resourceInfo) {
		this.resourceInfo = resourceInfo;
		return this;
	}

	public String getResourceInfo(){
		return this.resourceInfo;
	}
	
	protected Instruction getInstruction(ParamMap paramMap) {
		return next == null ? new Instruction() : next.perform(paramMap);
	}
	
	public boolean isTerminated() {
		if(next != null)
			return next.isTerminated();
		return false;
	}

	public abstract Instruction perform(ParamMap paramMap);
	
	public String getString(){
		return "";
	}

	@Override
	public String toString() {
		return "?";
	}
}
