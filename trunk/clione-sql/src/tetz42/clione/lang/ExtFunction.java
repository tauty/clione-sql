package tetz42.clione.lang;

import static tetz42.clione.lang.ContextUtil.*;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public abstract class ExtFunction {

	private static ThreadLocal<List<Extention>> curExtention = new ThreadLocal<List<Extention>>() {

		@Override
		protected List<Extention> initialValue() {
			return new ArrayList<Extention>();
		}

	};
	private static ThreadLocal<List<ParamMap>> curParamMap = new ThreadLocal<List<ParamMap>>() {

		@Override
		protected List<ParamMap> initialValue() {
			return new ArrayList<ParamMap>();
		}
	};

	static void push(Extention extention, ParamMap paramMap) {
		curExtention.get().add(extention);
		curParamMap.get().add(paramMap);
	}

	static void pop() {
		pop(curExtention.get());
		pop(curParamMap.get());
	}

	private static void pop(List<?> list) {
		list.remove(list.size() - 1);
	}

	private static Extention getLatestCf() {
		List<Extention> list = curExtention.get();
		return list.get(list.size() - 1);
	}

	private static ParamMap getLatestMap() {
		List<ParamMap> list = curParamMap.get();
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
			throw new ClioneFormatException("Unknown function name '"
					+ getFuncName() + "'\nsrc:" + getSrc() + "\nResource info:"
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
