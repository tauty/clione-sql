package tetz42.clione.lang;

import static tetz42.clione.util.ContextUtil.*;
import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public abstract class ExtFunction {

	private static ThreadLocal<Extention> curExtention = new ThreadLocal<Extention>();
	private static ThreadLocal<ParamMap> curParamMap = new ThreadLocal<ParamMap>();

	static void set(Extention extention, ParamMap paramMap) {
		curExtention.set(extention);
		curParamMap.set(paramMap);
	}

	static void clear() {
		curExtention.set(null);
		curParamMap.set(null);
	}

	protected Instruction getInsideInstruction() {
		ClioneFunction inside = curExtention.get().getInside();
		return inside == null ? null : inside.perform(curParamMap.get());
	}

	protected Instruction getNextInstruction() {
		ClioneFunction next = curExtention.get().getNext();
		return next == null ? null : next.perform(curParamMap.get());
	}

	protected ClioneFunction getInside() {
		return curExtention.get().getInside();
	}

	protected ClioneFunction getNext() {
		return curExtention.get().getNext();
	}

	protected String getFuncName() {
		return curExtention.get().func;
	}

	protected String getSrc() {
		return curExtention.get().getSrc();
	}

	protected ParamMap getParamMap() {
		return curParamMap.get();
	}

	protected boolean isNegative() {
		return curExtention.get().isNegative;
	}

	public Instruction perform() {
		Instruction insideInst = getInsideInstruction();
		Instruction nextInst = getNextInstruction();
		if (insideInst != null) {
			insideInst = perform(insideInst);
			if (nextInst == null)
				return insideInst;
			Instruction inst = insideInst;
			while (inst.next != null) {
				inst = inst.next;
			}
			inst.next = nextInst;
			return insideInst;
		}
		if (nextInst != null)
			return perform(nextInst);
		return new Instruction();
	}

	protected Instruction perform(Instruction inst) {
		return inst;
	}

	public void check() {
		if (getInside() == null && getNext() == null)
			throw new ClioneFormatException("Unknown function name '"
					+ getFuncName() + "'\nsrc:" + getSrc() + "\nResource info:"
					+ getResourceInfo());
	}
}
