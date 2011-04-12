package tetz42.clione.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;

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

		parenthesises("(takoikanamako)aaa");
		parenthesises("(takoi')('kanamako)bbbb");
		parenthesises("(takoi\")aaa(\"kanamako)cccc");
		parenthesises("(takoi\")aaa(\"k(ana)mako)dddd");
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

	private static void parenthesises(String src) {
		Matcher m = delimPtn.matcher(src);
		m.find();
		System.out.println(parenthesises(src, m));
	}

	private static ClioneFunction tako(String src, Matcher m, int begin) {
		if (!m.find())
			return new Unparsed(src.substring(begin));
		ClioneFunction rtnCf = null;
		if (begin < m.start())
			rtnCf = new Unparsed(src.substring(begin, m.start()));
		ClioneFunction cf;
		String delim = m.group(0);
		if (delim.equals("'"))
			cf = new StrLiteral(endSingleQuotation(src, m));
		else if (delim.equals("\""))
			cf = new SQLLiteral(endDoubleQuotation(src, m),false);
		else if (delim.equals("("))
			cf = null;// TODO implementation return src.substring(begin, m.start(0));
		else
			cf = null; // it means ')' is found
		if(rtnCf == null)
			rtnCf = cf;
		else{
			rtnCf.setNext(cf);
		}
		return rtnCf;
			
	}

	private static String parenthesises(String src, Matcher m) {
		int begin = m.end();
		while (m.find()) {
			String delim = m.group(0);
			if (delim.equals("'"))
				endSingleQuotation(src, m);
			else if (delim.equals("\""))
				endDoubleQuotation(src, m);
			else if (delim.equals(")"))
				return src.substring(begin, m.start(0));
			else
				System.out.println(parenthesises(src, m));
		}
		throw new ClioneFormatException("Parenthesises Unmatched! src = " + src);
	}

	private static String endSingleQuotation(String src, Matcher m) {
		return endQuotation(src, m, "'", "Single");
	}

	private static String endDoubleQuotation(String src, Matcher m) {
		return endQuotation(src, m, "\"", "Double");
	}

	private static String endQuotation(String src, Matcher m, String quot,
			String s_d) {
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
		throw new ClioneFormatException(s_d + " quotation Unmatched! data = "
				+ src);
	}

	private static String nextChar(String src, int pos) {
		if (pos >= src.length())
			return null;
		return src.substring(pos, pos + 1);
	}

}
