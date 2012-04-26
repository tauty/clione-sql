package tetz42.cellom;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Query {

	public static final Pattern numPtn = Pattern.compile("\\[([0-9]+)\\]");

	public static final String PATH_SEPARATOR = "\\|";
	public static final String FIELD_SEPARATOR = ",";
	public static final String ANY = "*";
	// public static final String ANY_PATH = "**";
	public static final String CURRENT_ROW = ".";
	public static final String TERMINATE = "@";
	public static final String ESCAPE = "'";

	public static final Pattern escPtn = Pattern.compile(ESCAPE + "(([^"
			+ ESCAPE + "]|" + ESCAPE + ESCAPE + ")*)" + ESCAPE);

	List<String[]> list = new ArrayList<String[]>();

	public static Query parse(String src) {
		Query query = new Query();
		for (String s : src.split(PATH_SEPARATOR)) {
			query.append(s.split(FIELD_SEPARATOR));
		}
		return query;
	}

	public Query append(String... fieldNames) {
		list.add(fieldNames);
		return this;
	}

	public String[] get(int index) {
		if (index < 0 || list.size() <= index)
			return null;
		return list.get(index);
	}

	public static String encode(String src) {

		return src;
	}

	public static String decode(String src) {
		return src;
	}

}
