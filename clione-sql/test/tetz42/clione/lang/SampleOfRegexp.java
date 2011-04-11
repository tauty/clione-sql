package tetz42.clione.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleOfRegexp {

	private static final String str_literal = "'(([^']|'')*)'";
	private static final String sql_literal = "\"(([^\"]|\"\")*)\"";
	private static final String function = "([$@&?#%]?)(!?)([a-zA-Z0-9\\.\\-_]*)";
	private static final String parenthesises = "(\\((([^)'\"]*|'[^']*'|\"[^\"]*\")*)\\))?";

	private static final String all_expression = String.format(
			"(((%s)|(%s)|(%s%s))(\\s+|$))|(:)", str_literal, sql_literal,
			function, parenthesises);

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

}
