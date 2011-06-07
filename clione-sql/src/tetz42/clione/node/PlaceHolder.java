package tetz42.clione.node;

import static tetz42.clione.lang.LangUtil.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.lang.ClioneFuncFactory;
import tetz42.clione.lang.Instruction;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public class PlaceHolder {
	private static Pattern ptn = Pattern.compile("\\A(=|in|is)\\s+.+",
			Pattern.CASE_INSENSITIVE);

	public int begin;
	public int length = 0;
	private String valueInBack;

	private final ClioneFunction clione;

	public PlaceHolder(String src, String valueInBack, int begin) {
		this.valueInBack = valueInBack;
		this.clione = ClioneFuncFactory.get().parse(src);
	}

	public Instruction perform(ParamMap paramMap) {
		Instruction inst = clione.perform(paramMap).merge();
		if (inst.useValueInBack) {
			return inst.clearParams().replacement(valueInBack);
		}
		Matcher m = ptn.matcher(valueInBack);
		if (m.matches()) {
			if (!isParamExists(inst)) {
				inst.replacement(" IS NULL ").clearParams();
			} else if (inst.params.size() == 1) {
				inst.replacement(" = " + inst.getReplacement() + " ");
			} else {
				inst.replacement(" IN (" + inst.getReplacement() + ") ");
			}
		}
		return inst;
	}

}
