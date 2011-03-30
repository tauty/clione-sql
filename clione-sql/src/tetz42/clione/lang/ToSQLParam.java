package tetz42.clione.lang;

import tetz42.clione.util.ParamMap;

public class ToSQLParam extends Clione {

	private Param param;
	private boolean isNegative;

	public ToSQLParam(String key, boolean isNegative)  {
		this.isNegative = isNegative;
		this.param = new Param(key);
	}

	@Override
	public Egg perform(ParamMap paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
