package tetz42.clione.lang;

import static tetz42.clione.util.ClioneUtil.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;

public class ClioneFuncFactory {

	private static final Pattern ptn = Pattern
			.compile("(([$@&?#:%]?)(!?)([a-zA-Z0-9\\.\\-_]*)|'(([^']|'')*)')(\\s+|$)");

	public static ClioneFuncFactory get(String resourceInfo) {
		return new ClioneFuncFactory(resourceInfo);
	}

	private final String resourceInfo;

	private ClioneFuncFactory(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public ClioneFunction parse(String src) {
		src = src.trim();
		return parse(src, ptn.matcher(src));
	}

	private ClioneFunction parse(String src, Matcher m) {
		if (!m.find())
			throw new ClioneFormatException("Unsupported Grammer :" + src);
		ClioneFunction clione = gen(src, m, m.group(2), m.group(3), m.group(4),
				m.group(5));
		if (clione == null)
			return null;
		clione.setResourceInfo(resourceInfo);
		if (clione.isTerminated())
			return clione;
		clione.setNext(parse(src, m));
		return clione;
	}

	private ClioneFunction gen(String src, Matcher m, String func, String not,
			String key, String str) {
		System.out.println("func=" + func + ", not=" + not + ", key=" + key
				+ ", str=" + str);
		if (isAllEmpty(func, not, key, str))
			return null;
		if(isNotEmpty(str))
			return new StrLiteral(str);
		if (isAllEmpty(func, not))
			return new Param(key);
		if (isNotEmpty(func)) {
			if (func.equals(":"))
				return new SQLLiteral(src.substring(m.end(2)), true);
			if (func.equals("$"))
				return new LineParam(key, isNotEmpty(not));
			if (func.equals("@"))
				return new RequireParam(key);
			if (func.equals("?"))
				return new DefaultParam(key);
			if (func.equals("#"))
				return new PartCond(key, isNotEmpty(not));
			if (func.equals("&"))
				return new LineCond(key, isNotEmpty(not));
			if (func.equals("%"))
				return new Extention(key, isNotEmpty(not), src.substring(m
						.end(1)));
		}
		throw new ClioneFormatException("Unsupported Grammer :" + src);
	}

}
