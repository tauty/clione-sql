package tetz42.clione.lang;

import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.clione.lang.LangUtil.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.lang.func.Parenthesises;
import tetz42.clione.lang.func.SQLLiteral;
import tetz42.clione.util.ParamMap;

public class Extention extends ClioneFunction {

	private static final Map<String, ExtFunction> funcMap = Collections
			.synchronizedMap(new HashMap<String, ExtFunction>());

	static {
		putFunction("L", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = getFunction("ESCLIKE").perform(inst);
				inst = getFunction("CONCAT").perform(inst);
				inst.replacement = "? ESCAPE '#'";
				return inst;
			}
		});
		putFunction("ESCLIKE", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				Instruction resultInst = inst;
				while (inst != null) {
					for (int i = 0; i < inst.params.size(); i++) {
						inst.params.set(i, escapeBySharp(inst.params.get(i)));
					}
					inst = inst.next;
				}
				return resultInst;
			}
		});
		putFunction("CONCAT", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				StringBuilder sb = new StringBuilder();
				Instruction resultInst = inst;
				while (inst != null) {
					if (inst.replacement != null) {
						sb.append(inst.replacement);
					} else {
						for (Object param : inst.params) {
							if (!isEmpty(param))
								sb.append(param);
						}
					}
					inst = inst.next;
				}
				resultInst.merge().replacement(null).clearParams();
				resultInst.params.add(sb.toString());
				return resultInst;
			}
		});
		putFunction("COMPACT", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				Instruction resultInst = inst;
				while (inst != null) {
					List<Object> newParams = new ArrayList<Object>();
					for (Object e : inst.params) {
						if (e != null)
							newParams.add(e);
					}
					inst.params = newParams;
					inst = inst.next;
				}
				return resultInst;
			}
		});
		putFunction("IF", new ExtFunction() {

			@Override
			public Instruction perform() {
				Instruction condition = getInsideInstruction();
				if (condition != null) {
					if (isParamExists(condition.merge()) ^ isNegative()) {
						Instruction nextInst = getNextInstruction();
						return nextInst != null ? nextInst : new Instruction()
								.nodeDispose(condition.isNodeDisposed);
					} else {
						return new Instruction().doNothing().nodeDispose(
								condition.isNodeDisposed);
					}
				} else {
					condition = getNextInstruction();
					if (condition == null)
						throw new ClioneFormatException(joinByCrlf("%"
								+ getFuncName()
								+ " must have next parameter like below:", "%"
								+ getFuncName() + " PARAM1 or %"
								+ getFuncName() + " PARAM1 :text"));
					if (isParamExists(condition) ^ isNegative()) {
						Instruction nextInst = condition.clearNext();
						return nextInst != null ? nextInst : new Instruction()
								.nodeDispose(condition.isNodeDisposed);
					} else {
						return new Instruction().doNothing().nodeDispose(
								condition.isNodeDisposed);
					}
				}
			}
		});
		putFunction("IFLN", new ExtFunction() {

			@Override
			public Instruction perform() {
				Instruction inst = getFunction("IF").perform();
				if (inst.doNothing)
					inst.nodeDispose();
				return inst;
			}
		});
		putFunction("INCLUDE", new ExtFunction() {

			@Override
			public Instruction perform() {
				// TODO implementation
				return null;
			}
		});
		putFunction("TO_SQL", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = getFunction("CONCAT").perform(inst);
				Instruction sqlInst = new SQLLiteral(inst.params.get(0)
						.toString()).resourceInfo(getResourceInfo()).perform(
						getParamMap());
				return inst.clearParams().merge(sqlInst);
			}
		});
		putFunction("TO_STR", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = getFunction("CONCAT").perform(inst);
				return new Instruction().replacement(
						inst.params.get(0).toString()).nodeDispose(
						inst.isNodeDisposed);
			}
		});
	}

	public static ExtFunction putFunction(String keyword, ExtFunction f) {
		return funcMap.put(keyword, f);
	}

	public static ExtFunction getFunction(String keyword) {
		return funcMap.get(keyword);
	}

	protected final String func;
	protected final boolean isNegative;
	protected ClioneFunction inside;

	public Extention(String key, boolean isNegative, String literal) {
		this.isNegative = isNegative;
		this.func = key;
	}

	@Override
	public ClioneFunction inside(ClioneFunction inside) {
		if (!Parenthesises.class.isInstance(inside)) {
			super.inside(inside);
		}
		this.inside = inside;
		return this;
	}

	@Override
	public ClioneFunction getInside() {
		return this.inside;
	}

	@Override
	public String getSrc() {
		return "%" + (isNegative ? "!" : "") + func
				+ (inside == null ? "" : inside.getSrc());
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		ExtFunction extFunction = funcMap.get(this.func);
		if (extFunction == null) {
			throw new ClioneFormatException("Unknown function name '"
					+ this.func + "'\nsrc:" + getSrc() + "\nResource info:"
					+ this.resourceInfo);
		}
		try {
			// initial process
			ExtFunction.set(this, paramMap);

			return extFunction.perform();
		} finally {
			// finally process
			ExtFunction.clear();
		}
	}
}
