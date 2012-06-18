package tetz42.clione.lang.func;

import static tetz42.clione.lang.ContextUtil.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ListWithDelim;
import tetz42.clione.util.ParamMap;
import tetz42.util.ReflectionUtil;

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
		Instruction inst = convToCol(paramMap.get(key));
		return inst.next(getNextInstruction(paramMap));
	}

	@SuppressWarnings("unchecked")
	private Instruction convToCol(Object val) {
		if (isNegative(val)) {
			return genInstruction(Arrays.asList(val), false);
		} else if (val.getClass().isArray()
				&& val.getClass().getComponentType() != Byte.TYPE) {
			ArrayList<Object> list = new ArrayList<Object>();
			int length = Array.getLength(val);
			boolean isTrue = false;
			for (int i = 0; i < length; i++) {
				Object e = Array.get(val, i);
				if (!isNegative(e))
					isTrue = true;
				list.add(e);
			}
			return genInstruction(list, isTrue);
		} else if (val instanceof Collection<?>) {
			Collection<Object> col = (Collection<Object>) val;
			return genInstruction(col, !isAllNegative(col));
		} else
			return genInstruction(Arrays.asList(val), true);
	}

	private Instruction genInstruction(Collection<Object> vals, boolean status) {
		List<Object> params;
		if (ListWithDelim.class.isInstance(vals)) {
			params = new ListWithDelim<Object>(vals);
		} else {
			params = new ArrayList<Object>(vals);
		}
		boolean isNum = true;
		for (Object param : params) {
			if (!ReflectionUtil.isNumber(param))
				isNum = false;
		}
		Instruction inst = new Instruction(params).number(isNum);
		inst.status(status ^ isNegative);
		return inst;
	}

	@Override
	public String getSrc() {
		return key;
	}

}
