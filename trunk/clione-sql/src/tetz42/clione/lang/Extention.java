package tetz42.clione.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.lang.func.Parenthesises;
import tetz42.clione.util.ParamMap;

public class Extention extends ClioneFunction {

	private static final Map<String, ExtFunction> funcMap = Collections
			.synchronizedMap(new HashMap<String, ExtFunction>());

	static {
		funcMap.put("ESC_LIKE", new ExtFunction() {

			@Override
			public Instruction perform(Instruction insideInst,
					Instruction nextInst) {
				if (insideInst != null) {
					for (int i = 0; i < insideInst.params.size(); i++) {
						String value = String.valueOf(insideInst.params.get(i));
						// TODO temporally implementation
						value = value.replaceAll("([\\%_])", "\\$1");
						insideInst.params.set(i, value);
					}
					if (insideInst.next != null)
						perform(insideInst.next, null);
				}else if(nextInst != null){
					
				}
				return insideInst;
			}
			
		});
		funcMap.put("IF", new ExtFunction() {

			@Override
			public Instruction perform(Instruction insideInst,
					Instruction nextInst) {
				return null;
			}
		});
		funcMap.put("REMOVE_UNLESS", new ExtFunction() {

			@Override
			public Instruction perform(Instruction insideInst,
					Instruction nextInst) {

				return null;
			}
		});
		funcMap.put("AVOID_NULL", new ExtFunction() {

			@Override
			public Instruction perform(Instruction insideInst,
					Instruction nextInst) {
				Instruction inst = insideInst != null ? insideInst : nextInst;
				if (inst == null)
					return new Instruction();
				List<Object> newParams = new ArrayList<Object>();
				for (Object e : inst.params) {
					if (e != null)
						newParams.add(e);
				}
				inst.params = newParams;
				return inst;
			}
		});
		funcMap.put("CONCAT", new ExtFunction() {

			@Override
			public Instruction perform(Instruction insideInst,
					Instruction nextInst) {
				return null;
			}
		});
		funcMap.put("TO_SQL", new ExtFunction() {

			@Override
			public Instruction perform(Instruction insideInst,
					Instruction nextInst) {
				return null;
			}
		});
		funcMap.put("TO_STR", new ExtFunction() {

			@Override
			public Instruction perform(Instruction insideInst,
					Instruction nextInst) {
				return null;
			}
		});
	}

	public static void setFunction(String keyword, ExtFunction f) {
		funcMap.put(keyword, f);
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
		Instruction insideInst = inside == null ? null : inside
				.getInstruction(paramMap);
		return extFunction.perform(insideInst, getInstruction(paramMap));
	}

}
