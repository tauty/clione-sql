package tetz42.clione.lang;

import tetz42.clione.util.ParamMap;

public class DynamicParam extends Clione {

	private final Param param;
	private final boolean isNegative;

	public DynamicParam(String key, boolean isNegative) {
		this.isNegative = isNegative;
		this.param = new Param(key);
	}

	@Override
	public Egg perform(ParamMap paramMap) {
		Egg egg = this.param.perform(paramMap);
		egg.isNodeRequired = !(isNoneParam(egg) ^ isNegative);
		return egg;
	}

	protected boolean isNoneParam(Egg egg) {
		for (Object e : egg.params) {
			if (e != null)
				return false;
		}
		return true;
	}

}
