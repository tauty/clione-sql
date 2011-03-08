package tetz42.clione.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClioneFactory {

	private static final Pattern ptn = Pattern
			.compile("([$@&?#:%]?)(!?)([a-zA-Z0-9\\.\\-_]*)");

	public static ClioneFactory get() {
		return new ClioneFactory();
	}

	public Clione parse(String src) {
		src = src.trim();
		return parse(ptn.matcher(src));
	}

	private Clione parse(Matcher m) {
		if (!m.find())
			return null;
		Clione clione = gen(m.group(1), m.group(2), m.group(3));
		if (clione == null || clione.isTerminated())
			return clione;
		clione.setChild(parse(m));
		return clione;
	}

	private Clione gen(String func, String not, String key) {
		System.out.println("f=" + func);
		System.out.println("n=" + not);
		System.out.println("k=" + key);
		if (isAllEmpty(func, not, key))
			return null;
		if (isAllEmpty(func, not))
			return new Param(key);
		return null;
	}

	private boolean isAllEmpty(String... strs) {
		for (String s : strs) {
			if (!isEmpty(s))
				return false;
		}
		return true;
	}

	private boolean isEmpty(String s) {
		return s == null ? true : s.length() == 0 ? true : false;
	}

}
