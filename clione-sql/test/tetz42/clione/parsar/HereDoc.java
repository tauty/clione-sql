package tetz42.clione.parsar;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.io.IOUtil;
import tetz42.util.MatcherHolder;

public class HereDoc {

	private static Pattern ptn = Pattern.compile("^<<([^<>]+)>>(\r\n|\r|\n)",
			Pattern.MULTILINE);

	public static Map<String, String> get(InputStream in) {
		byte[] bs = IOUtil.loadFromStream(in);
		String s = new String(bs);
		return parse(s);
	}

	private static Map<String, String> parse(String s) {
		Map<String, String> map = new HashMap<String, String>();
		MatcherHolder mh = new MatcherHolder(s, ptn);
		while (mh.find()) {
			String key = mh.get().group(1);
			mh.remember();
			Pattern endPtn = Pattern.compile("<</" + key + ">>");
			mh.bind("END", endPtn);
			if (!mh.find("END"))
				throw new ClioneFormatException("The tag, <<" + key
						+ ">>, must be closed by <</" + key + ">>!");
			String val = mh.getRememberedToStart();
			map.put(key, val);
		}
		return map;
	}

}
