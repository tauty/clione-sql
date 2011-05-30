package tetz42.clione.lang;

import static tetz42.clione.util.ClioneUtil.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.lang.func.DefaultParam;
import tetz42.clione.lang.func.LineCond;
import tetz42.clione.lang.func.LineParam;
import tetz42.clione.lang.func.Param;
import tetz42.clione.lang.func.Parenthesises;
import tetz42.clione.lang.func.PartCond;
import tetz42.clione.lang.func.RequireParam;
import tetz42.clione.lang.func.SQLLiteral;
import tetz42.clione.lang.func.StrLiteral;

public class ClioneFuncFactory {

	private static final Pattern delimPtn = Pattern.compile("[()'\":;]");
	private static final Pattern funcPtn = Pattern
			.compile("\\s*([$@&?#%]?)(!?)([a-zA-Z0-9\\.\\-_]*)([,\\s]+|$)");
	private static final Pattern backslashPtn = Pattern.compile("\\\\(.)");

	public static ClioneFuncFactory get(String resourceInfo) {
		return new ClioneFuncFactory(resourceInfo);
	}

	private final String resourceInfo;
	private String src;

	private ClioneFuncFactory(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public ClioneFunction parse(String src) {
		this.src = src;
		ClioneFunction cf = parseFunc(parseByDelim());
		if (cf != null)
			cf.check();
		return cf;
	}

	private ClioneFunction parseByDelim() {
		Unit unit = parseByDelim(delimPtn.matcher(src), 0);
		if (unit.isEndParenthesis)
			throw new ClioneFormatException("Parenthesises Unmatched! src = "
					+ src + "\nResouce info:" + resourceInfo);
		return unit.clioneFunc;
	}

	private Unit parseByDelim(Matcher m, int begin) {
		Unit unit = new Unit();
		if (!m.find())
			return unit.clioneFunc(new Unparsed(src.substring(begin))
					.resourceInfo(resourceInfo));
		if (begin < m.start())
			unit.clioneFunc = new Unparsed(src.substring(begin, m.start()))
					.resourceInfo(resourceInfo);
		Unit resultUnit;
		String delim = m.group(0);
		if (delim.equals("'"))
			resultUnit = genStr(m);
		else if (delim.equals("\""))
			resultUnit = genSQL(m);
		else if (delim.equals("("))
			resultUnit = parenthesises(m);
		else if (delim.equals(")"))
			resultUnit = new Unit().endPar(true);
		else if (delim.equals(":"))
			resultUnit = new Unit().clioneFunc(new SQLLiteral(unesc(src
					.substring(m.end()))).resourceInfo(resourceInfo));
		else
			resultUnit = new Unit().clioneFunc(new SQLLiteral(src.substring(m
					.end())).resourceInfo(resourceInfo)); // ';'
		return unit.clioneFunc == null ? resultUnit
				: joinUnit(unit, resultUnit);
	}

	private Unit parenthesises(Matcher m) {
		Unit inside = parseByDelim(m, m.end());
		if (!inside.isEndParenthesis)
			throw new ClioneFormatException("Parenthesises Unmatched! src = "
					+ src);
		ClioneFunction par = new Parenthesises(inside.clioneFunc)
				.resourceInfo(resourceInfo);
		Unit unit = parseByDelim(m, m.end());
		return unit.clioneFunc(par.nextFunc(unit.clioneFunc));
	}

	private Unit genStr(Matcher m) {
		return joinUnit(new Unit().clioneFunc(new StrLiteral(
				unesc(endQuotation(m, "'"))).resourceInfo(resourceInfo)),
				parseByDelim(m, m.end()));
	}

	private Unit genSQL(Matcher m) {
		return joinUnit(new Unit().clioneFunc(new SQLLiteral(
				unesc(endQuotation(m, "\""))).resourceInfo(resourceInfo)),
				parseByDelim(m, m.end()));
	}

	private String endQuotation(Matcher m, String quot) {
		int begin = m.end();
		while (m.find()) {
			// TODO consider about backslash escape
			if (quot.equals(m.group(0))) {
				if (quot.equals(nextChar(src, m.end()))) {
					m.find();
				} else {
					return src.substring(begin, m.start()).replace(quot + quot,
							quot);
				}
			}
		}
		throw new ClioneFormatException(quot.equals("'") ? "Single" : "Double"
				+ " quotation Unmatched! data = " + src);
	}

	private Unit joinUnit(Unit unit, Unit nextUnit) {
		unit.clioneFunc.nextFunc(nextUnit.clioneFunc);
		return unit.endPar(nextUnit.isEndParenthesis);
	}

	private String nextChar(String src, int pos) {
		if (pos >= src.length())
			return null;
		return src.substring(pos, pos + 1);
	}

	private static class Unit {
		Unit clioneFunc(ClioneFunction clioneFunc) {
			this.clioneFunc = clioneFunc;
			return this;
		}

		Unit endPar(boolean isEndParenthesis) {
			this.isEndParenthesis = isEndParenthesis;
			return this;
		}

		ClioneFunction clioneFunc = null;
		boolean isEndParenthesis = false;
	}

	private ClioneFunction parseFunc(ClioneFunction cf) {
		if (cf instanceof Unparsed)
			cf = parseFunc(funcPtn.matcher(cf.getSrc()), 0, cf.getNext());
		if (cf == null)
			return null;
		cf.inside(parseFunc(cf.getInside()));
		cf.nextFunc(parseFunc(cf.getNext()));
		return cf;
	}

	private ClioneFunction parseFunc(Matcher m, int pos, ClioneFunction last) {
		if (!m.find() || m.start() > pos)
			throw new ClioneFormatException("Unsupported Grammer :\n" + src
					+ "\nResouce info:" + resourceInfo);
		ClioneFunction clione = gen(m, m.group(1), m.group(2), m.group(3));
		if (clione == null)
			return last;
		clione.resourceInfo(resourceInfo);
		if ("".equals(m.group(4))) { // it means group(4) matched as '$'.
			if (last != null) {
				clione.nextFunc(last.getNext());
				last.nextFunc(null);
			}
			return clione.inside(last);
		}
		return clione.nextFunc(parseFunc(m, m.end(), last));
	}

	private ClioneFunction gen(Matcher m, String func, String not, String key) {
		if (isAllEmpty(func, not, key))
			return null;
		if (isAllEmpty(func, not))
			return new Param(key);
		if (isNotEmpty(func)) {
			if (func.equals("$"))
				return new LineParam(key, isNotEmpty(not));
			if (func.equals("@"))
				return new RequireParam(key, isNotEmpty(not));
			if (func.equals("?"))
				return new DefaultParam(key, isNotEmpty(not));
			if (func.equals("#"))
				return new PartCond(key, isNotEmpty(not));
			if (func.equals("&"))
				return new LineCond(key, isNotEmpty(not));
			if (func.equals("%"))
				return new Extention(key, isNotEmpty(not), src.substring(m
						.end(2)));
		}
		throw new ClioneFormatException("Unsupported Grammer :" + src);
	}

	private static String unesc(String s) {
		return backslashPtn.matcher(s).replaceAll("$1");
	}
}
