package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public abstract class ClioneFunction {

	private ClioneFunction next;
	protected String resourceInfo;

	public void setNext(ClioneFunction next) {
		this.next = next;
	}
	
	public ClioneFunction $next(ClioneFunction next) {
		this.next = next;
		return this;
	}
	
	public void setResourceInfo(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public ClioneFunction $resourceInfo(String resourceInfo) {
		this.resourceInfo = resourceInfo;
		return this;
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

	@Override
	public String toString() {
		return "?";
	}
}
