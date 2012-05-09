package tetz42.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;

public class HereDoc {

	private static Pattern ptn = Pattern.compile("^<<([^<>]+)>>(\r\n|\r|\n)?",
			Pattern.MULTILINE);

	public static Map<String, String> get(Class<?> clazz) {
		return get(clazz.getResourceAsStream(clazz.getSimpleName() + ".txt"));
	}

	public static Map<String, String> get(Class<?> clazz, String encoding) {
		return get(clazz.getResourceAsStream(clazz.getSimpleName() + ".txt"), encoding);
	}

	public static Map<String, String> get(InputStream in) {
		return parse(IOUtil.toString(in));
	}

	public static Map<String, String> get(InputStream in, String encoding) {
		return parse(IOUtil.toString(in, encoding));
	}

	private static Map<String, String> parse(String s) {
		Map<String, String> map = new HashMap<String, String>();
		RegexpTokenizer mh = new RegexpTokenizer(s, ptn);
		while (mh.find()) {
			String key = mh.matcher().group(1);
			mh.updateTokenPosition();
			Pattern endPtn = Pattern.compile("(\r\n|\r|\n)?<</" + key + ">>",
					Pattern.MULTILINE);
			mh.bind("END", endPtn);
			if (!mh.find("END"))
				throw new ClioneFormatException("The tag, <<" + key
						+ ">>, must be closed by <</" + key + ">>!");
			String val = mh.nextToken();
			map.put(key, val);
		}
		return map;
	}

}
