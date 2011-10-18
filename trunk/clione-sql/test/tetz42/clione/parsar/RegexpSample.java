package tetz42.clione.parsar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpSample {

	private static final Pattern crlfPtn = Pattern.compile("\r|\n|\r\n");
	private static final Pattern lineEndPtn = Pattern.compile("(.*)$",
			Pattern.MULTILINE);
	private static final Pattern endPtn = Pattern.compile("\\z");

	public static void main(String[] args) {

		String s = "0123456789\n9876543210";
		Matcher m = lineEndPtn.matcher(s);
		System.out.println("lineEndPtn#find:" + m.find(0));
		System.out.println("lineEndPtn#group(1):" + m.group(1));

		Matcher m2 = crlfPtn.matcher(s);
		System.out.println("crlfPtn#find:" + m2.find(m.end()));
		System.out.println("crlfPtn#group():" + m2.group());

		Matcher m3 = endPtn.matcher(s);
		System.out.println("crlfPtn#find:" + m3.find(m.end()));
		System.out.println("crlfPtn#end:" + m3.end());
		System.out.println("crlfPtn#group():" + m3.group());

		System.out.println("crlfPtn#find(end):" + m3.find(m3.end()));
		System.out.println("crlfPtn#end:" + m3.end());
		System.out.println("crlfPtn#group():" + m3.group());

		System.out.println("crlfPtn#find(s.length):" + m3.find(s.length()));
		System.out.println("crlfPtn#end:" + m3.end());
		System.out.println("crlfPtn#group():" + m3.group());

		System.out.println("crlfPtn#find(end+1):" + m3.find(m3.end() + 1));
		System.out.println("crlfPtn#end:" + m3.end());
		System.out.println("crlfPtn#group():" + m3.group());
	}

}
