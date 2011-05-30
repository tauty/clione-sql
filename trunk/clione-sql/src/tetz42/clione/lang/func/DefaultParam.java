package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class DefaultParam extends AbstractParam {

	public DefaultParam(String key, boolean isNegative) {
		super(key, isNegative);
	}

	public DefaultParam(ClioneFunction inside, boolean isNegative) {
		super(inside, isNegative);
	}

	@Override
	protected Instruction caseParamExists(ParamMap paramMap,
			Instruction paramInst) {
		return paramInst;
	}
			
	@Override
	protected Instruction caseParamNotExists(ParamMap paramMap,
			Instruction paramInst) {
		Instruction nextInst = getNextInstruction(paramMap);
		if(nextInst == null)
			return new Instruction().useValueInBack();
		return nextInst;
	}
	
	@Override
	public String getSrc() {
		return "?" + super.getSrc();
	}
}
