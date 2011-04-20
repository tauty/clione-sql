package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class DefaultParam extends AbstractParam {

	public DefaultParam(String key) {
		super(key, false);
	}

	public DefaultParam(ClioneFunction inside) {
		super(inside, false);
	}

	@Override
	protected Instruction caseParamNotExists(ParamMap paramMap,
			Instruction paramInst) {
		Instruction instruction = getInstruction(paramMap);
		if(instruction.params.size() == 0) {
			instruction.useValueInBack = true;
		}
		return instruction;
	}

}
