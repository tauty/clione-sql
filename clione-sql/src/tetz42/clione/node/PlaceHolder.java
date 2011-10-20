package tetz42.clione.node;

import static tetz42.clione.lang.LangUtil.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.lang.ClioneFuncFactory;
import tetz42.clione.lang.Instruction;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ClioneUtil;
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
		this(src, sValueInBack, 0);
	}

	public PlaceHolder(String src, String sValueInBack, int begin) {
		this(src, genStrNode(sValueInBack), begin);
	}

	public PlaceHolder(String src, INode valueInBack) {
		this(src, valueInBack, 0);
	}

	public PlaceHolder(String src, INode valueInBack, int begin) {
		this.valueInBack = valueInBack;
		this.clione = ClioneFuncFactory.get().parse(src);
		this.begin = begin;
	}

	private static INode genStrNode(String src) {
		if(src == null)
			return null;
		return new StrNode(src);
	}

	@Override
	public ClioneFunction getFunction() {
		return clione;
	}

	private Instruction convInst(Instruction inst) {
		if (valueInBack != null && valueInBack instanceof SQLNode) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			if(valueInBack.isMultiLine())
				sb.append(ClioneUtil.CRLF);
			sb.append(inst.getReplacement()).append(")");
			inst.replacement(sb.toString());
		}
		return inst;
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
			return convInst(valueInBack.perform(paramMap).useValueInBack());
		}
		return convInst(inst);
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
