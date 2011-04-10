package tetz42.clione.lang;

import tetz42.clione.util.ParamMap;

public abstract class ClioneFunction {

	private ClioneFunction next;
	protected String resourceInfo;

	public void setNext(ClioneFunction next) {
		this.next = next;
	}
	
	public void setResourceInfo(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	protected Instruction getInstruction(ParamMap paramMap) {
		return next == null ? new Instruction() : next.perform(paramMap);
	}
	
	protected boolean isTerminated() {
		if(next != null)
			return next.isTerminated();
		return false;
	}

	public abstract Instruction perform(ParamMap paramMap);

	@Override
	public String toString() {
		return "?";
	}
}
