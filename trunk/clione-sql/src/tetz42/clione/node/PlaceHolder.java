package tetz42.clione.node;

import static tetz42.clione.lang.LangUtil.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.lang.ClioneFuncFactory;
import tetz42.clione.lang.Instruction;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public class PlaceHolder {
	private static final Pattern positivePtn = Pattern.compile(
			"\\A(=|in|is)\\s+", Pattern.CASE_INSENSITIVE);
	private static final Pattern negativePtn = Pattern.compile(
			"\\A(!=|<>|not\\s+in|is\\s+not)\\s+", Pattern.CASE_INSENSITIVE);

	public int begin;
	public int length = 0;
	private String valueInBack;

	private final ClioneFunction clione;

	public PlaceHolder(String src, String valueInBack, int begin) {
		this.valueInBack = valueInBack;
		this.clione = ClioneFuncFactory.get().parse(src);
		this.begin = begin;
	}

	public Instruction perform(ParamMap paramMap) {
		Instruction inst = clione.perform(paramMap).merge();
		if (inst.isNodeDisposed)
			return inst;
		if (inst.useValueInBack) {
			if(valueInBack == null)
				return inst.doNothing();
			inst.doNothing = false;
			return inst.clearParams().replacement(valueInBack);
		}
		if(valueInBack == null)
			return inst;
		if (valueInBack.charAt(0) == '(') {
			return inst.replacement(new StringBuilder().append("(").append(
					inst.getReplacement()).append(")").toString());
		}
		Matcher m = positivePtn.matcher(valueInBack);
		if (m.find()) {
			if (!isParamExists(inst)) {
				inst.replacement(" IS NULL ").clearParams();
			} else if (inst.params.size() == 1) {
				inst.replacement(" = " + inst.getReplacement() + " ");
			} else {
				inst.replacement(" IN (" + inst.getReplacement() + ") ");
			}
		} else {
			m = negativePtn.matcher(valueInBack);
			if (m.find()) {
				if (!isParamExists(inst)) {
					inst.replacement(" IS NOT NULL ").clearParams();
				} else if (inst.params.size() == 1) {
					inst.replacement(" <> " + inst.getReplacement() + " ");
				} else {
					inst.replacement(" NOT IN (" + inst.getReplacement() + ") ");
				}
			}
		}
		return inst;
	}

}
