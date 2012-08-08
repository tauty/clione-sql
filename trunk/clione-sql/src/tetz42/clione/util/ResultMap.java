package tetz42.clione.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultMap extends LinkedHashMap<String, Object> {

	/**  */
	private static final long serialVersionUID = 8871788701353775458L;

	private final Map<String, Object> upperMap = new HashMap<String, Object>();

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key. The key is converted to lower case
	 * before it is used.
	 *
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 */
	@Override
	public Object get(Object key) {
		Object val = super.get(key);
		if (val == null && key instanceof String)
			val = upperMap.get(((String) key).toUpperCase());
		return val;
	}

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation). If the map previously contained a mapping for the
	 * key, the old value is replaced by the specified value. (A map m is said
	 * to contain a mapping for a key k if and only if m.containsKey(k) would
	 * return true.) The key parameter is converted to lower case before it is
	 * used.
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key. (A null return can also indicate that the map
	 *         previously associated null with key, if the implementation
	 *         supports null values.)
	 */
	@Override
	public Object put(String key, Object value) {
		Object preOrg = super.put(key, value);
		Object preUpper = null;
		if (key != null)
			preUpper = upperMap.put(key.toUpperCase(), value);
		return preOrg != null ? preOrg : preUpper;
	}

	/**
	 * Returns the string value to which the specified key is mapped, or null if
	 * this map contains no mapping for the key.
	 *
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return string value
	 * @see ResultMap#get(Object)
	 */
	public String getString(String key) {
		Object value = get(key);
		return value == null ? null : String.valueOf(value);
	}

	/**
	 * Returns the integer value to which the specified key is mapped, or null
	 * if this map contains no mapping for the key.
	 *
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return integer value
	 * @see ResultMap#get(Object)
	 */
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
