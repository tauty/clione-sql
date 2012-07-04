package tetz42.clione.lang.func;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.util.ReflectionUtil.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ListWithDelim;
import tetz42.clione.util.ParamMap;

public class Param extends ClioneFunction {

	protected final String key;
	protected final boolean isNegative;

	public Param(String key) {
		this(key, false);
	}

	public Param(String key, boolean isNegative) {
		this.key = key;
		this.isNegative = isNegative;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction inst = genInstruction(paramMap.get(key));
		return inst.next(getNextInstruction(paramMap));
	}

	private Instruction genInstruction(Object val) {
		if (isNegative(val)) {
			return genInstruction(val, false);
		} else if (val instanceof Iterable<?>) {
			Iterable<?> ite = (Iterable<?>) val;
			return genInstruction(ite);
		} else if (val.getClass().isArray()
				&& val.getClass().getComponentType() != Byte.TYPE) {
			ArrayList<Object> params = new ArrayList<Object>();
			int length = Array.getLength(val);
			boolean isNum = true;
			boolean status = false;
			for (int i = 0; i < length; i++) {
				Object e = Array.get(val, i);
				isNum = isNum ? isNumber(e) : false;
				status = status ? true : !isNegative(e);
				params.add(e);
			}
			return genInstruction(params, status, isNum);
		} else {
			return genInstruction(val, true);
		}
	}

	private Instruction genInstruction(Object val, boolean status) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(val);
		return genInstruction(params, status, isNumber(val));
	}

	private Instruction genInstruction(Iterable<?> ite) {
		List<Object> params;
		if (ListWithDelim.class.isInstance(ite)) {
			ListWithDelim<Object> lwd = new ListWithDelim<Object>();
			lwd.setDelim(((ListWithDelim<?>) ite).getDelim());
			params = lwd;
		} else {
			params = new ArrayList<Object>();
		}
		boolean isNum = true;
		boolean status = false;
		for (Object e : ite) {
			isNum = isNum ? isNumber(e) : false;
			status = status ? true : !isNegative(e);
			params.add(e);
		}
		return genInstruction(params, status, isNum);
	}

	private Instruction genInstruction(List<Object> params, boolean status,
			boolean isNum) {
		Instruction inst = new Instruction(params).number(isNum);
		inst.status(status ^ isNegative);
		return inst;
	}

	@Override
	public String getSrc() {
		return key;
	}

}
