package tetz42.clione.lang;

import static tetz42.clione.lang.ContextUtil.*;

import java.util.List;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public abstract class ExtFunction {

	static void push(Extention extention, ParamMap paramMap) {
		getCurExtensions().add(extention);
		getCurParamMaps().add(paramMap);
	}

	static void pop() {
		pop(getCurExtensions());
		pop(getCurParamMaps());
	}

	private static void pop(List<?> list) {
		list.remove(list.size() - 1);
	}

	private static Extention getLatestCf() {
		List<Extention> list = getCurExtensions();
		return list.get(list.size() - 1);
	}

	private static ParamMap getLatestMap() {
		List<ParamMap> list = getCurParamMaps();
		return list.get(list.size() - 1);
	}

	protected Instruction getInsideInstruction() {
		ClioneFunction inside = getLatestCf().getInside();
		return inside == null ? null : inside.perform(getLatestMap());
	}

	protected Instruction getNextInstruction() {
		ClioneFunction next = getLatestCf().getNext();
		return next == null ? null : next.perform(getLatestMap());
	}

	protected ClioneFunction getInside() {
		return getLatestCf().getInside();
	}

	protected ClioneFunction getNext() {
		return getLatestCf().getNext();
	}

	protected String getFuncName() {
		return getLatestCf().func;
	}

	protected String getSrc() {
		return getLatestCf().getSrc();
	}

	protected ParamMap getParamMap() {
		return getLatestMap();
	}

	protected boolean isNegative() {
		return getLatestCf().isNegative;
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
			throw new ClioneFormatException("The function, '" + getSrc()
					+ "', must have some parameters.\nResource info:"
					+ getResourceInfo());
	}

	protected ClioneFunction searchFunc(Filter filter) {
		ClioneFunction cf = getLatestCf().getNext();
		while (cf != null) {
			if (filter.isMatch(cf))
				return cf;
			cf = cf.getNext();
		}
		return null;
	}

	interface Filter {
		boolean isMatch(ClioneFunction cf);
	}
}
