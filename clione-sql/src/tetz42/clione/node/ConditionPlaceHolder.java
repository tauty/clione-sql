package tetz42.clione.node;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class ConditionPlaceHolder extends PlaceHolder implements IPlaceHolder {

	private final Node node;
	private final String operator;
	private final boolean isPositive;

	public ConditionPlaceHolder(Node node, String comment, boolean isPositive,
			String operator, INode valueInBack) {
		super(comment, valueInBack);
		this.node = node;
		this.operator = operator;
		this.isPositive = isPositive;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		// TODO implementation!
		return super.perform(paramMap);
	}

}
