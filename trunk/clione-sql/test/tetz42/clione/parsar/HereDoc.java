package tetz42.clione.parsar;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.io.IOUtil;
import tetz42.util.RegexpTokenizer;

public class HereDoc {

	private static Pattern ptn = Pattern.compile("^<<([^<>]+)>>(\r\n|\r|\n)?",
			Pattern.MULTILINE);

	public static Map<String, String> get(InputStream in) {
		byte[] bs = IOUtil.loadFromStream(in);
		String s = new String(bs);
		return parse(s);
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
