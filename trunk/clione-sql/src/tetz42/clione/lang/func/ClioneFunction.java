package tetz42.clione.lang.func;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public abstract class ClioneFunction {

	private ClioneFunction next;
	protected String resourceInfo;

	public ClioneFunction nextFunc(ClioneFunction next) {
		this.next = next;
		return this;
	}

	public ClioneFunction getNext() {
		return this.next;
	}

	public ClioneFunction inside(ClioneFunction inside) {
		throw new ClioneFormatException(getSrc() + " can not have "
				+ inside.getSrc()
				+ ". Use other function or insert white space between them."
				+ "\nResource info:" + resourceInfo);
	}

	public ClioneFunction getInside() {
		return null;
	}

	public ClioneFunction resourceInfo(String resourceInfo) {
		this.resourceInfo = resourceInfo;
		return this;
	}

	public String getResourceInfo() {
		return this.resourceInfo;
	}

	public void check() {
	}

	protected Instruction getInstruction(ParamMap paramMap) {
		return next == null ? new Instruction() : next.perform(paramMap);
	}

	public abstract Instruction perform(ParamMap paramMap);

	public abstract String getSrc();
}
