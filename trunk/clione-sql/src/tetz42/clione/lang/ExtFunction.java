package tetz42.clione.lang;

import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public abstract class ExtFunction {

	private static ThreadLocal<Extention> curExtention = new ThreadLocal<Extention>();
	private static ThreadLocal<ParamMap> curParamMap = new ThreadLocal<ParamMap>();
	private static ThreadLocal<String> curFuncName = new ThreadLocal<String>();

	static void set(Extention extention, ParamMap paramMap, String funcName) {
		curExtention.set(extention);
		curParamMap.set(paramMap);
		curFuncName.set(funcName);
	}

	static void clear() {
		curExtention.set(null);
		curParamMap.set(null);
		curFuncName.set(null);
	}

	protected Instruction getInsideInstruction() {
		ClioneFunction inside = curExtention.get().getInside();
		return inside == null ? null : inside.perform(curParamMap.get());
	}

	protected Instruction getNextInstruction() {
		ClioneFunction next = curExtention.get().getNext();
		return next == null ? null : next.perform(curParamMap.get());
	}

	protected String getFuncName() {
		return curFuncName.get();
	}

	protected boolean isNegative() {
		return curExtention.get().isNegative;
	}

	public Instruction perform() {
		Instruction insideInst = getInsideInstruction();
		Instruction nextInst = getNextInstruction();
		if (insideInst != null) {
			insideInst = perform(insideInst);
			if(nextInst == null)
				return insideInst;
			Instruction inst = insideInst;
			while(inst.next != null) {
				inst = inst.next;
			}
			inst.next = nextInst;
			return insideInst;
		}
		if(nextInst != null)
			return perform(nextInst);
		return new Instruction();
	}

	protected Instruction perform(Instruction inst) {
		return inst;
	}
}
