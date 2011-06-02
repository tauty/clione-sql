package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class LineCond extends AbstractParam {

	public LineCond(String key, boolean isNegative)  {
		super(key, isNegative);
	}

	@Override
	protected Instruction caseParamExists(ParamMap paramMap,
			Instruction paramInst) {
		return new Instruction().doNothing();
	}

	@Override
	public String getSrc() {
		return "&" + super.getSrc();
	}
}
