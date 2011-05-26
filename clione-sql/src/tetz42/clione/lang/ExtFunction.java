package tetz42.clione.lang;

import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public abstract class ExtFunction {
	
	private static ThreadLocal<Extention> curExtention = new ThreadLocal<Extention>();
	private static ThreadLocal<ParamMap> curParamMap = new ThreadLocal<ParamMap>();
	
	static void set(Extention extention, ParamMap paramMap){
		curExtention.set(extention);
		curParamMap.set(paramMap);
	}

	static void clear() {
		curExtention.set(null);
		curParamMap.set(null);
	}
	
	protected Instruction getInsideInstruction(){
		ClioneFunction cf = curExtention.get().getInside();
		return cf == null ? null:cf.getInstruction(curParamMap.get());
	}
	
	protected Instruction getNextInstruction(){
		ClioneFunction cf = curExtention.get().getNext();
		return cf == null ? null:cf.getInstruction(curParamMap.get());
	}
	
	protected Instruction getValidInstruction(){
		Instruction inst = getInsideInstruction();
		return inst != null ? inst : getNextInstruction();
	}
	
	protected boolean isNegative(){
		return curExtention.get().isNegative;
	}
	
	public Instruction perform(){
		Instruction inst = getValidInstruction();
		if(inst == null)
			return new Instruction();
		return perform(inst);
	}
	
	protected Instruction perform(Instruction inst) {
		return inst;
	}
}
