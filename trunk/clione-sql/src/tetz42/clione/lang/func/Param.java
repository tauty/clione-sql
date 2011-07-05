package tetz42.clione.lang.func;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.util.Pair.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.Pair;
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
		Pair<? extends Collection<?>, Boolean> pair = convToCol(paramMap
				.get(key));
		Instruction inst = new Instruction().status(pair.getSecond()
				^ isNegative);
		inst.params.addAll(pair.getFirst());
		return inst.next(getNextInstruction(paramMap));
	}

	private Pair<? extends Collection<?>, Boolean> convToCol(Object val) {
		if (isNegative(val)) {
			return pair(Arrays.asList(val), false);
		} else if (val.getClass().isArray()) {
			ArrayList<Object> list = new ArrayList<Object>();
			int length = Array.getLength(val);
			boolean isTrue = false;
			;
			for (int i = 0; i < length; i++) {
				Object e = Array.get(val, i);
				if (!isNegative(e))
					isTrue = true;
				list.add(e);
			}
			return pair(list, isTrue);
		} else if (val instanceof Collection<?>) {
			Collection<?> col = (Collection<?>) val;
			return pair(col, !isNegative(col));
		} else
			return pair(Arrays.asList(val), true);
	}

	@Override
	public String getSrc() {
		return key;
	}

}
