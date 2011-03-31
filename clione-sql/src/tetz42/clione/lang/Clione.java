package tetz42.clione.lang;

import tetz42.clione.util.ParamMap;

public abstract class Clione {

	private Clione next;
	protected String resourceInfo;

	public void setNext(Clione next) {
		this.next = next;
	}
	
	public void setResourceInfo(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	protected Instruction getInstruction(ParamMap paramMap) {
		return next == null ? new Instruction() : next.perform(paramMap);
	}
	
	protected boolean isTerminated() {
		return false;
	}

	public abstract Instruction perform(ParamMap paramMap);
}
