package tetz42.clione.lang.func;

import static tetz42.clione.lang.ContextUtil.*;
import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

abstract public class ClioneFunction {

	private ClioneFunction next;
	
	public ClioneFunction nextFunc(ClioneFunction next) {
		this.next = next;
		return this;
	}

	public ClioneFunction getNext() {
		return this.next;
	}

	public ClioneFunction inside(ClioneFunction inside) {
		if (inside != null)
			throw new ClioneFormatException(getSrc() + " can not have "
					+ inside.getSrc()
					+ ". Probably you can solve this by deleting one of them"
					+ " or inserting white space between them."
					+ "\nResource info:" + getResourceInfo());
		return this;
	}

	public ClioneFunction getInside() {
		return null;
	}

	public void compile() {
	}

	public Instruction getNextInstruction(ParamMap paramMap) {
		return next == null ? null : next.perform(paramMap);
	}

	public abstract Instruction perform(ParamMap paramMap);

	public abstract String getSrc();
	
	public String getLiteral() {
		throw new UnsupportedOperationException("Unsupported.");
	}
}
