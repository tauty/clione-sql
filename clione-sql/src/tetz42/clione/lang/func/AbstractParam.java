package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

abstract public class AbstractParam extends ClioneFunction {

	protected final Param param;
	protected final boolean isNegative;

	public AbstractParam(String key, boolean isNegative) {
		this.isNegative = isNegative;
		this.param = new Param(key);
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		return performTask(paramMap, this.param.perform(paramMap));
	}

	@Override
	public void setResourceInfo(String resourceInfo) {
		super.setResourceInfo(resourceInfo);
		this.param.setResourceInfo(resourceInfo);
	}

	protected Instruction performTask(ParamMap paramMap, Instruction paramInst) {
		if (isParamExists(paramInst) ^ isNegative) {
			return caseParamExists(paramMap, paramInst);
		} else {
			return caseParamNotExists(paramMap, paramInst);
		}
	}

	protected Instruction caseParamExists(ParamMap paramMap,
			Instruction paramInst) {
		return getInstruction(paramMap).merge(paramInst);
	}

	protected Instruction caseParamNotExists(ParamMap paramMap,
			Instruction paramInst) {
		paramInst.isNodeRequired = false;
		return paramInst;
	}

	protected final boolean isParamExists(Instruction instruction) {
		for (Object e : instruction.params) {
			if (e != null)
				return true;
		}
		return false;
	}

}
