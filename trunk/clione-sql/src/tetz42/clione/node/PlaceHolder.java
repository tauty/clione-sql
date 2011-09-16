package tetz42.clione.node;

import static tetz42.clione.lang.LangUtil.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.lang.ClioneFuncFactory;
import tetz42.clione.lang.Instruction;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public class PlaceHolder implements IPlaceHolder {
	private static final Pattern positivePtn = Pattern.compile(
			"\\A(=|in|is)\\s+", Pattern.CASE_INSENSITIVE);
	private static final Pattern negativePtn = Pattern.compile(
			"\\A(!=|<>|not\\s+in|is\\s+not)\\s+", Pattern.CASE_INSENSITIVE);

	private int begin;
	private int length = 0;
	private INode valueInBack;
	private final ClioneFunction clione;

	public PlaceHolder(String src, String sValueInBack) {
		this.valueInBack = genStrNode(sValueInBack);
		this.clione = ClioneFuncFactory.get().parse(src);
	}
	
	public PlaceHolder(String src, String sValueInBack, int begin) {
		this.valueInBack = genStrNode(sValueInBack);
		this.clione = ClioneFuncFactory.get().parse(src);
		this.begin = begin;
	}
	
	private INode genStrNode(String src) {
		if(src == null)
			return null;
		return new StrNode(src);
	}
	
	@Override
	public ClioneFunction getFunction() {
		return clione;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction inst = clione.perform(paramMap).merge();
		if (inst.isNodeDisposed)
			return inst;
		if (inst.useValueInBack) {
			if (valueInBack == null)
				return inst.doNothing();
			inst.doNothing = false;
			return valueInBack.perform(paramMap).useValueInBack();
		}
		if (valueInBack == null)
			return inst;
		if (inst.isNodeDisposed)
			return inst;
		String sValueInBack = valueInBack.perform(paramMap).getReplacement();
		if (sValueInBack.charAt(0) == '(') {
			return inst.replacement(new StringBuilder().append("(")
					.append(inst.getReplacement()).append(")").toString());
		}
		Matcher m = positivePtn.matcher(sValueInBack);
		if (m.find()) {
			if (!isParamExists(inst)) {
				inst.replacement(" IS NULL ").clearParams();
			} else if (inst.params.size() == 1) {
				inst.replacement(" = " + inst.getReplacement() + " ");
			} else {
				inst.replacement(" IN (" + inst.getReplacement() + ") ");
			}
		} else {
			m = negativePtn.matcher(sValueInBack);
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

	@Override
	public int getPosition() {
		return this.begin;
	}

	@Override
	public void setPosition(int pos) {
		this.begin = pos;
	}

	@Override
	public int getLength() {
		return this.length;
	}

	@Override
	public void movePosition(int num) {
		this.begin += num;
	}
}
