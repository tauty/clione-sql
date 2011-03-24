package tetz42.clione.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClioneFactory {

	private static final Pattern ptn = Pattern
			.compile("([$@&?#:%]?)(!?)([a-zA-Z0-9\\.\\-_]*)(\\s*)");

	public static ClioneFactory get() {
		return new ClioneFactory();
	}

	public Clione parse(String src) {
		src = src.trim();
		return parse(src, ptn.matcher(src));
	}

	private Clione parse(String src, Matcher m) {
		if (!m.find())
			return null;
		Clione clione = gen(src, m, m.group(1), m.group(2), m.group(3));
		if (clione == null || clione.isTerminated())
			return clione;
		clione.setChild(parse(src, m));
		return clione;
	}

	private Clione gen(String src, Matcher m, String func, String not, String key) {
		System.out.println("f=" + func);
		System.out.println("n=" + not);
		System.out.println("k=" + key);
		if (isAllEmpty(func, not, key))
			return null;
		if (isAllEmpty(func, not))
			return new Param(key);
		if(isNotEmpty(func)){
			if(func.equals(":"))
				return new Literal(src.substring(m.end(1)), true);
			if(func.equals("$"))
				return new DynamicParam(key, isEmpty(not));
		}
		return null;
	}

	private static boolean isAllNotEmpty(String... strs) {
		for (String s : strs) {
			if (isEmpty(s))
				return false;
		}
		return true;
	}

	private static boolean isAllEmpty(String... strs) {
		for (String s : strs) {
			if (isNotEmpty(s))
				return false;
		}
		return true;
	}

	private static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}
	
	private static boolean isEmpty(String s) {
		return s == null ? true : s.length() == 0 ? true : false;
	}

}
