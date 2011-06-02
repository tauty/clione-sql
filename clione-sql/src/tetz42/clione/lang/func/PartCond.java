package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class PartCond extends AbstractParam {

	public PartCond(String key, boolean isNegative)  {
		super(key, isNegative);
	}

	@Override
	protected Instruction caseParamExists(ParamMap paramMap,
			Instruction paramInst) {
		Instruction instruction = getNextInstruction(paramMap);
		instruction.params.clear();
		if(instruction.replacement == null) {
			instruction.doNothing = true;
		}
		return instruction;
	}

	@Override
	protected Instruction caseParamNotExists(ParamMap paramMap,
			Instruction paramInst) {
		Instruction instruction = getNextInstruction(paramMap);
		instruction.doNothing = true;
		return instruction;
	}

	@Override
	public String getSrc() {
		return "#" + super.getSrc();
	}
}
