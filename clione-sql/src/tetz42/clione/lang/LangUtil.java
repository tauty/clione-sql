package tetz42.clione.lang;

import static tetz42.clione.lang.ContextUtil.*;

import java.util.regex.Pattern;

public class LangUtil {
	public static boolean isParamExists(Instruction instruction) {
		return !isAllNegative(instruction.params);
	}

	private static Pattern safePtnLv1 = Pattern.compile("[a-zA-Z0-9_\\.]+");
	private static Pattern safePtnLv2 = Pattern.compile("[a-zA-Z0-9_\\.]+");

	public static boolean isSafeSTR(String src) {
		return true;
	}
}
