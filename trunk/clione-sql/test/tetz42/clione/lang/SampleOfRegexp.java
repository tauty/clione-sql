package tetz42.clione.lang;

import static tetz42.util.ObjDumper4j.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.lang.func.Parenthesises;
import tetz42.clione.lang.func.SQLLiteral;
import tetz42.clione.lang.func.StrLiteral;

public class SampleOfRegexp {

	private static final String str_literal = "'(([^']|'')*)'";
	private static final String sql_literal = "\"(([^\"]|\"\")*)\"";
	private static final String function = "([$@&?#%]?)(!?)([a-zA-Z0-9\\.\\-_]*)";
	// private static final String parenthesises =
	// "(\\((([^)'\"]*|'[^']*'|\"[^\"]*\")*)\\))?";

	private static final String all_expression = String.format(
			"(((%s)|(%s)|(%s))([,\\s]+|\\(|$))|(:)", str_literal, sql_literal,
			function);

	private static final Pattern ptn = Pattern.compile(all_expression);

	// private static final Pattern ptn = Pattern
	// .compile("(([$@&?#%]?)(!?)([a-zA-Z0-9\\.\\-_]*)(\\((([^)'\"]*|'[^']*'|\"[^\"]*\")*)\\))?|'(([^']|'')*)'|\"(([^\"]|\"\")*)\"(\\s+|$))|(:)");

	// private static final Pattern subPtn = Pattern
	// .compile("(\\((([^)'\"]*|'[^']*'|\"[^\"]*\")*)\\))?");

	public static void main(String[] args) {

		output("string", "'I''m a man. It''s all right!' /* tako */");
		output("sql",
				"\"She said, \"\"You don't understand myself.\"\"\" /* tako */");
		output("sql2", ":takoika namako /* tako *\\/ sakana");
		output("func1", "%!KEY /* tako */");
		output("func2", "%!KEY(tako ika 'tozi-''k)akko') /* tako */");
		output("func3", "%!KEY(tako ika \"tozi-\"\"k)akko\") /* tako */");
		output("func4", "%KEY");
		output("func5", "%!KEY, $KEY2");

		System.out.println(dumper(parseByDelim("(takoikanamako)aaa")));
		System.out.println(dumper(parseByDelim("(takoi')('kanamako)bbbb")));
		System.out.println(dumper(parseByDelim("(takoi\")aaa(\"kanamako)cccc")));
		System.out
				.println(dumper(parseByDelim("(takoi\")aaa(\"k(ana)mako)dddd")));
	}

	private static void output(String header, String contents) {
		System.out.println("\n[" + header + "]");
		Matcher m = ptn.matcher(contents);
		if (m.find()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= m.groupCount(); i++) {
				sb.append("group(").append(i).append(") = ").append(m.group(i))
						.append("\n");
			}
			System.out.println(sb);
		}
	}

	private static final Pattern delimPtn = Pattern.compile("[()'\":]");

	static class Unit {
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

	private static ClioneFunction parseByDelim(String src) {
		Unit unit = parseByDelim(src, delimPtn.matcher(src), 0);
		if (unit.isEndParenthesis)
			throw new ClioneFormatException("Parenthesises Unmatched! src = "
					+ src);
		return unit.clioneFunc;
	}

	private static Unit parseByDelim(String src, Matcher m, int begin) {
		Unit unit = new Unit();
		if (!m.find())
			return unit.clioneFunc(new Unparsed(src.substring(begin)));
		if (begin < m.start())
			unit.clioneFunc = new Unparsed(src.substring(begin, m.start()));
		Unit resultUnit;
		String delim = m.group(0);
		if (delim.equals("'"))
			resultUnit = genStr(src, m);
		else if (delim.equals("\""))
			resultUnit = genSQL(src, m);
		else if (delim.equals("("))
			resultUnit = parenthesises(src, m);
		else if (delim.equals(")"))
			resultUnit = new Unit().endPar(true);
		else // ':'
			resultUnit = new Unit().clioneFunc(new SQLLiteral(src.substring(m.end()),
					true));
		return unit.clioneFunc == null ? resultUnit : joinUnit(unit, resultUnit);
	}

	private static Unit parenthesises(String src, Matcher m) {
		Unit inside = parseByDelim(src, m, m.end());
		if (!inside.isEndParenthesis)
			throw new ClioneFormatException("Parenthesises Unmatched! src = "
					+ src);
		Parenthesises par = new Parenthesises(inside.clioneFunc);
		Unit unit = parseByDelim(src, m, m.end());
		return unit.clioneFunc(par.nextFunc(unit.clioneFunc));
	}

	private static Unit genStr(String src, Matcher m) {
		return joinUnit(new Unit().clioneFunc(new StrLiteral(endQuotation(src, m, "'"))),
				parseByDelim(src, m, m.end()));
	}

	private static Unit genSQL(String src, Matcher m) {
		return joinUnit(new Unit().clioneFunc(new SQLLiteral(endQuotation(src, m, "\""),
				false)), parseByDelim(src, m, m.end()));
	}

	private static String endQuotation(String src, Matcher m, String quot) {
		int begin = m.end();
		while (m.find()) {
			if (quot.equals(m.group(0))) {
				if (quot.equals(nextChar(src, m.end()))) {
					m.find();
				} else {
					return src.substring(begin, m.start());
				}
			}
		}
		throw new ClioneFormatException(quot.equals("'") ? "Single" : "Double"
				+ " quotation Unmatched! data = " + src);
	}

	private static Unit joinUnit(Unit unit, Unit nextUnit) {
		unit.clioneFunc.nextFunc(nextUnit.clioneFunc);
		return unit.endPar(nextUnit.isEndParenthesis);
	}

	private static String nextChar(String src, int pos) {
		if (pos >= src.length())
			return null;
		return src.substring(pos, pos + 1);
	}

}
