package tetz42.clione.lang.func;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.lang.LangUtil.*;
import static tetz42.clione.util.ClioneUtil.*;
import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

abstract public class AbstractParam extends ClioneFunction {

	protected ClioneFunction param;
	protected final boolean isNegative;

	public AbstractParam(String key, boolean isNegative) {
		this.isNegative = isNegative;
		if (isNotEmpty(key))
			this.param = new Param(key);
	}

	@Override
	public ClioneFunction inside(ClioneFunction inside) {
		if (inside != null && inside != this.param) {
			if (!Parenthesises.class.isInstance(inside)) {
				super.inside(inside);
			}
			if (param != null) {
				throw new ClioneFormatException(
						getSrc()
								+ " can not have both of "
								+ param.getSrc()
								+ " and "
								+ inside.getSrc()
								+ ". Probably you can solve this by deleting one of them"
								+ " or inserting white space between them."
								+ "\nResource info:" + getResourceInfo());
			}
			this.param = inside;
		}
		return this;
	}

	@Override
	public ClioneFunction getInside() {
		return this.param;
	}

	@Override
	public String getSrc() {
		return (isNegative ? "!" : "") + (param == null ? "" : param.getSrc());
	}

	@Override
	public void compile() {
		if (param == null) {
			throw new ClioneFormatException(getSrc()
					+ " do not have parameter(s). "
					+ "It must be like bolow:\n" + getSrc() + "PARAM or "
					+ getSrc() + "(PARAM1, PARAM2, PARAM3)"
					+ "\nResource info:" + getResourceInfo());
		}
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		return performTask(paramMap, this.param.perform(paramMap));
	}

	protected Instruction performTask(ParamMap paramMap, Instruction paramInst) {
		if (isParamExists(paramInst) ^ isNegative) {
			return caseParamExists(paramMap, paramInst).status(true);
		} else {
			return caseParamNotExists(paramMap, paramInst).status(false);
		}
	}

	protected Instruction caseParamExists(ParamMap paramMap,
			Instruction paramInst) {
		return paramInst.next(getNextInstruction(paramMap));
	}

	protected Instruction caseParamNotExists(ParamMap paramMap,
			Instruction paramInst) {
		return paramInst.nodeDispose();
	}


}
