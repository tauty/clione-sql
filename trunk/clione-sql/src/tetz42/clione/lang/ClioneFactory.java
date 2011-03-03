package tetz42.clione.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClioneFactory {

	private static final Pattern ptn = Pattern
			.compile("^(\\$|@|&|\\?|#|:|%)?(!)?([a-zA-Z0-9\\.-_])*");

	public static ClioneFactory get() {
		return new ClioneFactory();
	}

	public Clione parse(String src) {
		src = src.trim();
		return parse(ptn.matcher(src));
	}

	private Clione parse(Matcher m) {
		m.find();
		Clione clione = gen(m.group(1), m.group(2), m.group(3));
		if (clione == null)
			return null;
		return null;
	}

	private Clione gen(String func,String not, String key) {
		if(func==null && not == null && key == null)
			return null;
		if(func == null && not == null){
			// PARAM
			
		}
		return null;
		
	}

}
