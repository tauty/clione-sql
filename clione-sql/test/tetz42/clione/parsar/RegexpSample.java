package tetz42.clione.parsar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpSample {

	private static final Pattern crlfPtn = Pattern.compile("\r|\n|\r\n");
	private static final Pattern lineEndPtn = Pattern.compile("(.*)$",
			Pattern.MULTILINE);

	public static void main(String[] args) {

		String s = "0123456789\n9876543210";
		Matcher m = lineEndPtn.matcher(s);
		System.out.println("lineEndPtn#find:" + m.find(0));
		System.out.println("lineEndPtn#group(1):" + m.group(1));

		Matcher m2 = crlfPtn.matcher(s);
		System.out.println("crlfPtn#find:" + m2.find(m.end()));
		System.out.println("crlfPtn#group(1):" + m2.group());
	}

}
