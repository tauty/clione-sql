package tetz42.clione.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import tetz42.clione.util.ParamMap;

public class Param extends Clione {

	private final String key;

	public Param(String key) {
		this.key = key;
	}

	@Override
	public Egg perform(ParamMap paramMap) {
		Egg egg = deliver(paramMap);
		Object val = paramMap.get(key);
		if (val == null)
			return egg;
		Collection<?> vals = convToCol(val);
		if (vals == null) {
			egg.params.add(val);
		} else {
			if (vals.size() == 0)
				return egg;
			// TODO implement correctly
			egg.replacement = null; // genQuestions(vals);
			egg.params.addAll(vals);
		}
		return egg;
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

	private String genQuestions(Collection<?> params) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			if (i != 0)
				sb.append(", ");
			sb.append("?");
		}
		return sb.toString();
	}
}
