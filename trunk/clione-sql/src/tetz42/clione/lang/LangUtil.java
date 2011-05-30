package tetz42.clione.lang;

public class LangUtil {
	public static boolean isParamExists(Instruction instruction) {
		for (Object e : instruction.params) {
			if (e != null)
				return true;
		}
		return false;
	}
}
