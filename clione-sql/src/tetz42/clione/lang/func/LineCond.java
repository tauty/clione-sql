package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class LineCond extends AbstractParam {

	public LineCond(String key, boolean isNegative)  {
		super(key, isNegative);
	}

	public LineCond(ClioneFunction inside, boolean isNegative)  {
		super(inside, isNegative);
	}

	@Override
	protected Instruction caseParamExists(ParamMap paramMap,
			Instruction paramInst) {
		Instruction instruction = getInstruction(paramMap);
		instruction.params.clear();
		if(instruction.replacement == null) {
			instruction.doNothing = true;
		}
		return instruction;
	}

	@Override
	public String getSrc() {
		return "&" + super.getSrc();
	}
}
