package tetz42.clione.lang;

public interface ExtFunction {
	Instruction perform(Instruction insideInst, Instruction nextInst,
			boolean isNegative);
}
