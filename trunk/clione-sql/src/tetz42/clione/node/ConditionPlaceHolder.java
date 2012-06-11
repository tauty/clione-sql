package tetz42.clione.node;

import static tetz42.clione.lang.LangUtil.*;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.lang.ContextUtil;
import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ClioneUtil;
import tetz42.clione.util.ListWithDelim;
import tetz42.clione.util.ParamMap;

public class ConditionPlaceHolder extends PlaceHolder implements IPlaceHolder {

	private final INode node;
	private final String operator;
	private final boolean isPositive;
	private final boolean isLike;

	public ConditionPlaceHolder(INode node, String comment, boolean isPositive,
			String operator, INode valueInBack) {
		super(comment, valueInBack);
		this.node = node;
		this.operator = operator;
		this.isPositive = isPositive;
		this.isLike = operator.toLowerCase().contains("like");
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction inst = super.perform(paramMap);
		if (inst.isNodeDisposed || inst.doNothing)
			return inst;
		Instruction nodeInst = node.perform(paramMap);
		if (nodeInst.isNodeDisposed || nodeInst.doNothing)
			return nodeInst;

		if (inst.useValueInBack) {
			nodeInst.addReplacement(" " + operator + " ");
			return nodeInst.useValueInBack().merge(inst);
		}

		final int LIMIT = isLike ? 1 : ContextUtil.getDialect().inLimit();

		if (inst.params.size() <= LIMIT)
			return build(nodeInst, inst);

		Instruction result = new Instruction().replacement("("
				+ ClioneUtil.CRLF);

		final String delim = delim(inst.params, isPositive);
		System.out.println("delim:" + delim);
		final Object[] paramAry = inst.params.toArray();
		for (int i = 0; i * LIMIT <= paramAry.length; i++) {

			int start = i * LIMIT;
			int end = (i + 1) * LIMIT;
			end = end < inst.params.size() ? end : paramAry.length;

			ArrayList<Object> list = new ArrayList<Object>();
			for (int j = start; j < end; j++)
				list.add(inst.params.get(j));

			if (!list.isEmpty()) {
				result.addReplacement("\t").addReplacement(i == 0 ? "" : delim);
				Instruction subInst = new Instruction(list);
				result.merge(build(nodeInst, subInst)).addReplacement(
						ClioneUtil.CRLF);
			}
		}
		result.addReplacement(")");

		return result;
	}

	private Instruction build(Instruction nodeInst, Instruction inst) {
		Instruction result = new Instruction().merge(nodeInst);
		result.status = inst.status;
		if (!isParamExists(inst)) {
			result.addReplacement(isPositive ? " IS NULL" : " IS NOT NULL");
			return result;
		} else if (inst.params.size() == 1) {
			result.addReplacement(isPositive ? isLike ? " LIKE " : " = "
					: isLike ? " NOT LIKE " : " <> ");
			return result.merge(inst);
		} else {
			result.addReplacement(isPositive ? " IN " : " NOT IN ");
			if (inst.getReplacement().startsWith("("))
				return result.merge(inst);
			else
				return result.addReplacement("(").merge(inst)
						.addReplacement(")");
		}
	}

	private String delim(List<Object> params, boolean isPositive) {
		if(ListWithDelim.class.isInstance(params)) {
			return ((ListWithDelim<?>)params).getDelim() + " ";
		}
		return isPositive ? "OR " : "AND ";
	}
}
