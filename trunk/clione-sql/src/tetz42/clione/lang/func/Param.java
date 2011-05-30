package tetz42.clione.lang.func;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class Param extends ClioneFunction {

	protected final String key;

	public Param(String key) {
		this.key = key;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction inst = new Instruction();
		inst.params.addAll(convToCol(paramMap.get(key)));
		return inst.next(getNextInstruction(paramMap));
	}

	private Collection<?> convToCol(Object val) {
		if (val == null) {
			return Arrays.asList(val);
		} else if (val.getClass().isArray()) {
			ArrayList<Object> list = new ArrayList<Object>();
			for (int i = 0; i < Array.getLength(val); i++) {
				list.add(Array.get(val, i));
			}
			return list;
		} else if (val instanceof Collection<?>)
			return (Collection<?>) val;
		else
			return Arrays.asList(val);
	}

	@Override
	public String getSrc() {
		return key;
	}

}
