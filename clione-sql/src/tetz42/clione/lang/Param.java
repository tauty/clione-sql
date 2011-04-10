package tetz42.clione.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import tetz42.clione.util.ParamMap;

public class Param extends ClioneFunction {

	protected final String key;

	public Param(String key) {
		this.key = key;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction instruction = getInstruction(paramMap);
		Object val = paramMap.get(key);
		if (val == null)
			return instruction;
		Collection<?> vals = convToCol(val);
		if (vals == null) {
			instruction.params.add(val);
		} else {
			if (vals.size() == 0)
				return instruction;
			instruction.params.addAll(vals);
		}
		return instruction;
	}

	private Collection<?> convToCol(Object val) {
		if (val.getClass().isArray()) {
			ArrayList<Object> list = new ArrayList<Object>();
			for (int i = 0; i < Array.getLength(val); i++) {
				list.add(Array.get(val, i));
			}
			return list;
		} else if (val instanceof Collection<?>)
			return (Collection<?>) val;
		return null;
	}

}
