package tetz42.clione.lang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public class Extention extends ClioneFunction {

	private static final Map<String, ExtFunction> funcMap = Collections
			.synchronizedMap(new HashMap<String, ExtFunction>());
	
	static{
		funcMap.put("ESCL", new ExtFunction() {
			
			@Override
			public Instruction perform(Instruction inst) {
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
		Instruction instruction;
		if (inside == null)
			instruction = getInstruction(paramMap);
		else
			instruction = inside.getInstruction(paramMap);
		ExtFunction extFunction = funcMap.get(this.func);
		if (extFunction == null) {
			throw new ClioneFormatException("Unknown function name '"
					+ this.func + "'\nsrc:" + getSrc() + "\nresourceInfo:"
					+ this.resourceInfo);
		}
		return extFunction.perform(instruction);
	}
	
}
