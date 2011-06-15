package tetz42.clione.lang;

import static tetz42.clione.lang.LangUtil.*;
import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.clione.util.ContextUtil.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.lang.func.Parenthesises;
import tetz42.clione.loader.LoaderUtil;
import tetz42.clione.node.SQLNode;
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
		putFunction("C", getFunction("CONCAT"));
		putFunction("COMPACT", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				Instruction resultInst = inst;
				while (inst != null) {
					List<Object> newParams = new ArrayList<Object>();
					for (Object e : inst.params) {
						if (!isNil(e))
							newParams.add(e);
					}
					inst.params = newParams;
					inst = inst.next;
				}
				return resultInst;
			}
		});
		putFunction("if", new ExtFunction() {

			@Override
			public Instruction perform() {
				Instruction condition = getInsideInstruction();
				if (condition != null) {
					if (isParamExists(condition.merge()) ^ isNegative()) {
						Instruction nextInst = getNextInstruction();
						return nextInst != null ? nextInst : new Instruction()
								.nodeDispose(condition.isNodeDisposed);
					} else {
						return doElse(condition);
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
						return doElse(condition);
					}
				}
			}

			private Instruction doElse(Instruction condition) {
				ClioneFunction cf = searchFunc(new Filter() {

					@Override
					public boolean isMatch(ClioneFunction cf) {
						if (!Extention.class.isInstance(cf))
							return false;
						Extention ext = (Extention) cf;
						if (!isContain(ext.func, "elseif", "else", "ELSEIF",
								"ELSE")) {
							return false;
						}
						return true;
					}
				});
				if (cf != null) {
					return cf.perform(new ExtendedParamMap(getParamMap())
							.caller(getFuncName()));
				} else {
					return new Instruction().useValueInBack().doNothing()
							.nodeDispose(condition.isNodeDisposed);
				}
			}
		});
		putFunction("elseif", new ExtFunction() {

			@Override
			public Instruction perform() {
				ParamMap paramMap = getParamMap();
				if (ExtendedParamMap.class.isInstance(paramMap)) {
					ExtendedParamMap extMap = (ExtendedParamMap) paramMap;
					if (isContain(extMap.getCaller(), "if", "elseif", "IF",
							"ELSEIF")) {
						extMap.caller(null);
						return getFunction("if").perform();
					}
				}
				return null;
			}
		});
		putFunction("else", new ExtFunction() {

			@Override
			public Instruction perform() {
				ParamMap paramMap = getParamMap();
				if (ExtendedParamMap.class.isInstance(paramMap)) {
					ExtendedParamMap extMap = (ExtendedParamMap) paramMap;
					if (isContain(extMap.getCaller(), "if", "elseif", "IF",
							"ELSEIF")) {
						return getNextInstruction();
					}
				}
				return null;
			}
		});
		putFunction("IF", new ExtFunction() {

			@Override
			public Instruction perform() {
				Instruction inst = getFunction("if").perform();
				if (inst.doNothing)
					inst.nodeDispose();
				return inst;
			}
		});
		putFunction("ELSEIF", new ExtFunction() {

			@Override
			public Instruction perform() {
				Instruction inst = getFunction("elseif").perform();
				if (inst.doNothing)
					inst.nodeDispose();
				return inst;
			}
		});
		putFunction("ELSE", getFunction("else"));
		putFunction("PUT", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				Instruction result = new Instruction();
				String key = null;
				while (inst != null) {
					if (key == null)
						key = inst.replacement;
					else {
						result.$(key, inst.replacement);
						key = null;
					}
					inst = inst.next;
				}
				return result;
			}
		});
		putFunction("ON", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				Instruction result = new Instruction();
				while (inst != null) {
					result.$(inst.replacement, Boolean.TRUE);
					inst = inst.next;
				}
				return result;
			}
		});
		putFunction("INCLUDE", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst.merge();
				SQLNode sqlNode = LoaderUtil.getNodeByPath(inst.replacement);
				SQLGenerator generator = new SQLGenerator();
				ParamMap paramMap = new ParamMap();
				if (inst.map != null)
					paramMap.putAll(inst.map);
				paramMap.putAll(getParamMap());
				String sql = generator.genSql(paramMap, sqlNode);
				Instruction result = new Instruction().replacement(sql);
				if (generator.params != null && generator.params.size() != 0)
					result.params.addAll(generator.params);
				return result;
			}
		});
		putFunction("SQL", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = getFunction("CONCAT").perform(inst);
				return new Instruction().replacement(
						String.valueOf(inst.params.get(0))).nodeDispose(
						inst.isNodeDisposed);
			}
		});
		putFunction("STR", getFunction("SQL"));
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
	protected final ExtFunction extFunction;

	public Extention(String key, boolean isNegative) {
		this.isNegative = isNegative;
		this.func = key;
		extFunction = funcMap.get(this.func);
		if (extFunction == null) {
			throw new ClioneFormatException("Unknown function name '"
					+ this.func + "'\nsrc:" + getSrc() + "\nResource info:"
					+ getResourceInfo());
		}
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
		try {
			// initial process
			ExtFunction.push(this, paramMap);

			return extFunction.perform();
		} finally {
			// finally process
			ExtFunction.pop();
		}
	}

	@Override
	public void check() {
		try {
			// initial process
			ExtFunction.push(this, null);

			extFunction.check();
		} finally {
			// finally process
			ExtFunction.pop();
		}
	}

}
