package tetz42.clione.lang;

import static tetz42.clione.lang.ContextUtil.*;

public class LangUtil {
	public static boolean isParamExists(Instruction instruction) {
		return !isAllNegative(instruction.params);
	}
}
