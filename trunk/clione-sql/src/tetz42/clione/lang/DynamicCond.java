package tetz42.clione.lang;

import tetz42.clione.util.ParamMap;

public class DynamicCond extends Clione {

	private boolean isNegative;
	private Param param;

	public DynamicCond(String key, boolean isNegative)  {
		this.isNegative = isNegative;
		this.param = new Param(key);
	}

	@Override
	public Egg perform(ParamMap paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
