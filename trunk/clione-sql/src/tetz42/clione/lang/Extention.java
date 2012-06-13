package tetz42.clione.lang;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.util.Util.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.exception.ImpossibleToCompareException;
import tetz42.clione.exception.SQLFileNotFoundException;
import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.lang.ContextUtil.IFStatus;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.lang.func.Parenthesises;
import tetz42.clione.loader.LoaderUtil;
import tetz42.clione.node.SQLNode;
import tetz42.clione.util.ParamMap;

public class Extention extends ClioneFunction {

	private static final Map<String, ExtFunction> funcMap = Collections
			.synchronizedMap(new HashMap<String, ExtFunction>());

	static class Cycler<T> {
		private final List<T> list;
		int index = 0;
		boolean hasNext = true;

		Cycler(List<T> list) {
			if (list == null || list.isEmpty())
				throw new UnsupportedOperationException(
						"Cycler does not support neither null nor empty list.");
			this.list = list;
		}

		Cycler(T[] ts) {
			this.list = Arrays.asList(ts);
		}

		T next() {
			T ele = list.get(index++);
			if (index >= list.size()) {
				index = 0;
				hasNext = false;
			}
			return ele;
		}

		boolean hasNext() {
			return hasNext;
		}
	}

	static {
		putFunction("L", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = getFunction("esc_like").perform(inst);
				inst = getFunction("concat").perform(inst);
				return new Instruction() {
					@Override
					public String getReplacement() {
						return genQuestions() + " ESCAPE '#'";
					}
				}.merge(inst);
			}
		});
		putFunction("esc_like", new ExtFunction() {

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
		putFunction("concat", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				ArrayList<Cycler<Object>> list = new ArrayList<Cycler<Object>>();
				Instruction resultInst = inst;
				while (inst != null) {
					if (inst.replacement != null) {
						list.add(new Cycler<Object>(Arrays
								.asList((Object) inst.replacement)));
					} else {
						list.add(new Cycler<Object>(inst.params));
					}
					inst = inst.next;
				}
				ArrayList<Object> paramList = new ArrayList<Object>();
				while (true) {
					boolean hasNext = false;
					StringBuilder sb = new StringBuilder();
					for (Cycler<Object> cycler : list) {
						Object e = cycler.next();
						if (isNotEmpty(e))
							sb.append(e);
						hasNext = hasNext || cycler.hasNext;
					}
					paramList.add(sb.toString());
					if (!hasNext)
						break;
				}
				resultInst.merge().replacement(null).clearParams();
				resultInst.params.addAll(paramList);
				return resultInst;
			}
		});
		putFunction("C", getFunction("concat"));
		putFunction("del_negative", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				Instruction resultInst = inst;
				while (inst != null) {
					List<Object> newParams = new ArrayList<Object>();
					for (Object e : inst.params) {
						if (!ContextUtil.isNegative(e))
							newParams.add(e);
					}
					inst.params.clear();
					inst.params.addAll(newParams);
					inst = inst.next;
				}
				return resultInst;
			}
		});

		// %if - %elseif - %else
		putFunction("if", new ExtFunction() {

			@Override
			public Instruction perform() {
				Instruction condition = getInsideInstruction();
				if (condition != null) {
					// if (isParamExists(condition.merge()) ^ isNegative()) {
					if (condition.merge().and() ^ isNegative()) {
						Instruction nextInst = getNextInstruction();
						return nextInst != null ? nextInst : new Instruction()
								.nodeDispose(condition.isNodeDisposed);
					} else {
						return doElse(condition);
					}
				} else {
					condition = getNextInstruction();
					Instruction nextInst = condition.clearNext();
					// if (isParamExists(condition) ^ isNegative()) {
					if (condition.and() ^ isNegative()) {
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
						if (!contains(ext.func, "elseif", "else")) {
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
					if (contains(extMap.getCaller(), "if", "elseif")) {
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
					if (contains(extMap.getCaller(), "if", "elseif")) {
						return getNextInstruction();
					}
				}
				return null;
			}
		});

		// %IF - %ELSEIF - %ELSE
		putFunction("IF", new ExtFunction() {

			@Override
			public Instruction perform() {
				Instruction condition = getInsideInstruction();
				if (condition != null) {
					if (condition.merge().and() ^ isNegative()) {
						Instruction nextInst = getNextInstruction();
						return nextInst != null ? nextInst : new Instruction()
								.nodeDispose(condition.isNodeDisposed);
					} else {
						ContextUtil.setIFStatus(IFStatus.DO_ELSE_NEXT);
						return new Instruction().useValueInBack().doNothing()
								.nodeDispose();
					}
				} else {
					condition = getNextInstruction();
					Instruction nextInst = condition.clearNext();
					if (condition.and() ^ isNegative()) {
						return nextInst != null ? nextInst : new Instruction()
								.nodeDispose(condition.isNodeDisposed);
					} else {
						ContextUtil.setIFStatus(IFStatus.DO_ELSE_NEXT);
						return new Instruction().useValueInBack().doNothing()
								.nodeDispose();
					}
				}
			}
		});
		putFunction("ELSEIF", new ExtFunction() {

			@Override
			public Instruction perform() {
				if (ContextUtil.getIFStatus() != IFStatus.DO_ELSE) {
					return new Instruction().useValueInBack().doNothing()
							.nodeDispose();
				}
				return getFunction("IF").perform();
			}
		});
		putFunction("ELSE", new ExtFunction() {
			@Override
			public Instruction perform() {
				if (ContextUtil.getIFStatus() != IFStatus.DO_ELSE) {
					return new Instruction().useValueInBack().doNothing()
							.nodeDispose();
				} else {
					Instruction nextInst = getNextInstruction();
					return nextInst != null ? nextInst : new Instruction();
				}
			}

			@Override
			public void check() {
				// no check
			}
		});

		// compare
		putFunction("equals", new CompFunction(Type.EQ));
		putFunction("greaterThan", new CompFunction(Type.GT));
		putFunction("greaterEqual", new CompFunction(Type.GE));
		putFunction("lessThan", new CompFunction(Type.LT));
		putFunction("lessEqual", new CompFunction(Type.LE));
		putFunction("eq", getFunction("equals"));
		putFunction("gt", getFunction("greaterThan"));
		putFunction("ge", getFunction("greaterEqual"));
		putFunction("lt", getFunction("lessThan"));
		putFunction("le", getFunction("lessEqual"));

		putFunction("and", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				boolean result = inst.and();
				return inst.merge().status(result);
			}
		});
		putFunction("or", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				boolean result = inst.or();
				return inst.merge().status(result);
			}
		});
		putFunction("put", new ExtFunction() {

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
		putFunction("on", new ExtFunction() {

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
		putFunction("include", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				SQLNode sqlNode;
				if (inst instanceof SQLNodeInstruction) {
					sqlNode = ((SQLNodeInstruction) inst).sqlNode;
				} else {
					String path = inst.replacement;
					sqlNode = getSQLNode(path, getFuncName());
				}
				inst.merge();
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
		putFunction("path", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = concat_all(inst);
				String path = "" + inst.params.get(0);
				inst.params.clear();
				SQLNodeInstruction ret = new SQLNodeInstruction();
				ret.sqlNode = getSQLNode(path, getFuncName());
				return ret.merge(inst);
			}
		});
		putFunction("STR", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = concat_all(inst);
				// TODO to delete.
				return new Instruction().replacement(
						String.valueOf(inst.params.get(0))).nodeDispose(
						inst.isNodeDisposed);
			}
		});
		putFunction("STR!", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = concat_all(inst);
				String src = String.valueOf(inst.params.get(0));
				LangUtil.check(src);
				return new Instruction().replacement(src).nodeDispose(
						inst.isNodeDisposed);
			}
		});
		putFunction("STR!!", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = concat_all(inst);
				return new Instruction().replacement(
						String.valueOf(inst.params.get(0))).nodeDispose(
						inst.isNodeDisposed);
			}
		});
		putFunction("SQL!", new ExtFunction() {

			@Override
			protected Instruction perform(Instruction inst) {
				inst = concat_all(inst);
				Instruction retInst = new Instruction();
				SQLGenerator sqlGenerator = new SQLGenerator();
				retInst.replacement = sqlGenerator.genSql(getParamMap(),
						LoaderUtil.getNodeBySQL(
								String.valueOf(inst.params.get(0)),
								"[WARN] Java String passed as parameter!!"));
				if (sqlGenerator.params != null
						&& sqlGenerator.params.size() != 0) {
					retInst.params.addAll(sqlGenerator.params);
				}
				return retInst;
			}
		});
	}

	private static ExtFunction putFunction(String keyword, ExtFunction f) {
		return funcMap.put(keyword, f);
	}

	private static ExtFunction getFunction(String keyword) {
		return funcMap.get(keyword);
	}

	private static SQLNode getSQLNode(String path, String funcName) {
		if (path == null)
			throw new ClioneFormatException(mkStringByCRLF("The parameter of %"
					+ funcName + " must be String literal.", getResourceInfo()));
		if (path.startsWith(".")) {
			String res = getResourcePath();
			if (res == null) {
				throw new SQLFileNotFoundException(mkStringByCRLF(
						"The relative path,'" + res + "' , can not found.",
						getResourceInfo()));
			}
			path = fusionPath(res, path);
		}
		return LoaderUtil.getNodeByPath(path, getProductName());
	}

	private static Instruction concat_all(Instruction inst) {
		StringBuilder sb = new StringBuilder();
		Instruction resultInst = inst;
		while (inst != null) {
			if (inst.replacement != null) {
				sb.append(inst.replacement);
			} else {
				for (Object param : inst.params) {
					if (isNotEmpty(param)) {
						sb.append(param);
						break;
					}
				}
			}
			inst = inst.next;
		}
		resultInst.merge().replacement(null).clearParams();
		resultInst.params.add(sb.toString());
		return resultInst;
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
	public void compile() {
		try {
			// initial process
			ExtFunction.push(this, null);

			extFunction.check();
		} finally {
			// finally process
			ExtFunction.pop();
		}
	}

	private enum Type {
		EQ, GT, GE, LT, LE
	}

	private static class CompFunction extends ExtFunction {

		private final Type type;

		private CompFunction(Type type) {
			this.type = type;
		}

		@Override
		protected Instruction perform(Instruction instruction) {
			ArrayList<Object> list = new ArrayList<Object>();
			Instruction inst = instruction;
			while (inst != null) {
				if (inst.params == null || inst.params.isEmpty())
					list.add(convIfNumber(inst.replacement, inst.isNumber));
				else {
					list.add(convIfNumber(inst.params.get(0), inst.isNumber));
				}
				inst = inst.next;
			}
			for (int i = 0; i < (list.size() - 1); i++) {
				if (!isOK(compare(list.get(i), list.get(i + 1))))
					return instruction.merge().status(false);
			}
			return instruction.merge().status(true);
		}

		private Object convIfNumber(Object obj, boolean isNumber) {
			if (obj != null && isNumber) {
				return new BigDecimal("" + obj);
			} else {
				return obj;
			}
		}

		public int compare(Object o1, Object o2) {
			if (o1 == null && o2 == null)
				return 0;
			else if (o1 == null)
				return -1;
			else if (o2 == null)
				return 1;
			return compareTask(o1, o2);
		}

		@SuppressWarnings("unchecked")
		protected int compareTask(Object o1, Object o2) {
			Exception cause = null;
			try {
				if (o1.equals(o2)) {
					return 0;
				} else if (this.type == Type.EQ) {
					return 1;
				} else if (o1 instanceof Comparable<?>) {
					return ((Comparable) o1).compareTo(o2);
				} else if (o2 instanceof Comparable<?>) {
					return ((Comparable) o2).compareTo(o1);
				}
			} catch (Exception e) {
				cause = e;
			}
			throw new ImpossibleToCompareException(o1.getClass()
					.getSimpleName()
					+ " and "
					+ o2.getClass().getSimpleName()
					+ " cannot be compared.(" + o1 + ", " + o2 + ")", cause);
		}

		private boolean isOK(int res) {
			if (type == Type.EQ)
				return res == 0;
			else if (type == Type.GT)
				return res > 0;
			else if (type == Type.GE)
				return res >= 0;
			else if (type == Type.LT)
				return res < 0;
			else
				return res <= 0;
		}
	}
}
