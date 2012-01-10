package tetz42.clione.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public class ResultMap extends LinkedHashMap<String, Object> {

	/**
	 *
	 */
	private static final long serialVersionUID = 8871788701353775458L;

	public String getString(String key) {
		Object value = get(key);
		return value == null ? null : String.valueOf(value);
	}

	public Integer getInt(String key) {
		Object obj = get(key);
		if (obj instanceof Integer)
			return (Integer) obj;
		else if (obj instanceof BigDecimal)
			return ((BigDecimal) obj).intValueExact();
		else if (obj instanceof Number)
			return ((Number) obj).intValue();
		else
			return (Integer) obj;
	}
}
