package tetz42.clione.lang;

import static tetz42.clione.util.ContextUtil.*;

public class LangUtil {
	public static boolean isParamExists(Instruction instruction) {
		return !isAllNil(instruction.params);
	}
}
