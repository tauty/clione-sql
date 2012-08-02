/*
 * Copyright 2011 - 2012 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tetz42.clione.util;

import static tetz42.clione.common.ReflectionUtil.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.common.Util;
import tetz42.clione.exception.DuplicateKeyException;

/**
 *
 * @author tetz
 */
public class ParamMap extends HashMap<String, Object> {

	/***/
	private static final long serialVersionUID = -6103186429868779178L;

	public static final Pattern KEY_PTN = Pattern.compile("([\"-),-~ ])[ -~]*");
	public static final Pattern SYMBOL_PTN = Pattern.compile("[^A-Za-z0-9]+");

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key. <br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 */
	@Override
	public Object get(Object key) {
		return super.get(convKey(key));
	}

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation). If the map previously contained a mapping for the
	 * key, the old value is replaced by the specified value. (A map m is said
	 * to contain a mapping for a key k if and only if m.containsKey(k) would
	 * return true.)<br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
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
		return super.put(convKey(key), value);
	}

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation). If the map previously contained a mapping for the
	 * key, the old value is replaced by the specified value. (A map m is said
	 * to contain a mapping for a key k if and only if m.containsKey(k) would
	 * return true.)<br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return this
	 * @see ParamMap#put(String, Object)
	 */
	public ParamMap $(String key, Object value) {
		this.put(key, value);
		return this;
	}

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation). If the map previously contained a mapping for the
	 * key, the old value is replaced by the specified value. (A map m is said
	 * to contain a mapping for a key k if and only if m.containsKey(k) would
	 * return true.)<br>
	 * If the specified value is empty string, "", the key and the value does
	 * not associate.<br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return this
	 * @see ParamMap#put(String, Object)
	 */
	public ParamMap $e(String key, Object value) {
		return this.$nv(key, value, "");
	}

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation). If the map previously contained a mapping for the
	 * key, the old value is replaced by the specified value. (A map m is said
	 * to contain a mapping for a key k if and only if m.containsKey(k) would
	 * return true.)<br>
	 * If the specified negatives contains the specified value, the key and the
	 * value is not associated.<br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @param negatives
	 * @return this
	 * @see ParamMap#put(String, Object)
	 */
	public ParamMap $nv(String key, Object value, Object... negatives) {
		for (Object negative : negatives) {
			if (negative == null)
				continue;
			if (negative.equals(value))
				return this;
		}
		return this.$(key, value);
	}

	/**
	 * The specified keys are associated with the value 'Boolean.TRUE'.<br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param keys
	 * @return this
	 * @see ParamMap#put(String, Object)
	 */
	public ParamMap $on(String... keys) {
		for (String key : keys)
			this.put(key, Boolean.TRUE);
		return this;
	}

	/**
	 * Associates the the generated {@link ListWithDelim} instance with the
	 * specified key in this map (optional operation). If the map previously
	 * contained a mapping for the key, the old instance is replaced by the
	 * generated instance. (A map m is said to contain a mapping for a key k if
	 * and only if m.containsKey(k) would return true.)<br>
	 * The specified elements are stored in the generated {@link ListWithDelim}
	 * instance.<br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param key
	 *            key with which the generated {@link ListWithDelim} instance is
	 *            to be associated
	 * @param eles
	 *            elements to be stored in the {@link ListWithDelim} instance
	 *            with delimiter 'or'
	 * @return this
	 */
	public ParamMap $orList(String key, Iterable<?> eles) {
		return this.$(key, ListWithDelim.genList(eles).or());
	}

	/**
	 * Associates the the generated {@link ListWithDelim} instance with the
	 * specified key in this map (optional operation). If the map previously
	 * contained a mapping for the key, the old instance is replaced by the
	 * generated instance. (A map m is said to contain a mapping for a key k if
	 * and only if m.containsKey(k) would return true.)<br>
	 * The specified elements are stored in the generated {@link ListWithDelim}
	 * instance. <br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param key
	 *            key with which the generated {@link ListWithDelim} instance is
	 *            to be associated
	 * @param eles
	 *            elements to be stored in the {@link ListWithDelim} instance
	 *            with delimiter 'or'
	 * @return this
	 */
	public <T> ParamMap $orList(String key, T... eles) {
		return this.$(key, ListWithDelim.genList(eles).or());
	}

	/**
	 * Associates the the generated {@link ListWithDelim} instance with the
	 * specified key in this map (optional operation). If the map previously
	 * contained a mapping for the key, the old instance is replaced by the
	 * generated instance. (A map m is said to contain a mapping for a key k if
	 * and only if m.containsKey(k) would return true.)<br>
	 * The specified elements are stored in the generated {@link ListWithDelim}
	 * instance. <br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param key
	 *            key with which the generated {@link ListWithDelim} instance is
	 *            to be associated
	 * @param eles
	 *            elements to be stored in the {@link ListWithDelim} instance
	 *            with delimiter 'and'
	 * @return this
	 */
	public ParamMap $andList(String key, Iterable<?> eles) {
		return this.$(key, ListWithDelim.genList(eles).and());
	}

	/**
	 * Associates the the generated {@link ListWithDelim} instance with the
	 * specified key in this map (optional operation). If the map previously
	 * contained a mapping for the key, the old instance is replaced by the
	 * generated instance. (A map m is said to contain a mapping for a key k if
	 * and only if m.containsKey(k) would return true.)<br>
	 * The specified elements are stored in the generated {@link ListWithDelim}
	 * instance. <br>
	 * Before the key is used, the first character of the key is removed if it
	 * is a symbol.<br>
	 *
	 * @param key
	 *            key with which the generated {@link ListWithDelim} instance is
	 *            to be associated
	 * @param eles
	 *            elements to be stored in the {@link ListWithDelim} instance
	 *            with delimiter 'and'
	 * @return this
	 */
	public <T> ParamMap $andList(String key, T... eles) {
		return this.$(key, ListWithDelim.genList(eles).and());
	}

	/**
	 * Inspects the specified object and associates its result.<br>
	 * <br>
	 * example:
	 *
	 * <pre>
	 * [parameter]
	 * FooClass {
	 * 	field1 = 10
	 * 	field2 = "str1"
	 * 	field3 = BarClass {
	 * 		field31 = "str2"
	 * 		field32 = 200
	 * 	}
	 * 	field4 = BazMap {
	 * 		"key41": "value1"
	 * 		"key42": "value2"
	 * 	}
	 * }
	 *
	 * [keys and values to be associated]
	 * 	"field1": 10
	 * 	"field2": "str1"
	 * 	"field3": BarClass {
	 * 		field31 = "str2"
	 * 		field32 = 200
	 * 	}
	 * 	"field3Field31": "str2"
	 * 	"field3Field32": 200
	 *  "field3_field31": "str2"
	 *  "field3_field32": 200
	 * 	"field4" = BazMap {
	 * 		"key41": "value1"
	 * 		"key42": "value2"
	 * 	}
	 * 	"field4Key41": "value1"
	 * 	"field4Key42": "value2"
	 * 	"field4_key41": "value1"
	 * 	"field4_key42": "value2"
	 * </pre>
	 *
	 * @param obj
	 *            the object to be inspect
	 * @return this
	 * @see ParamMap#map(Map)
	 * @see ParamMap#bean(Object)
	 */
	public ParamMap object(Object obj) {
		if (obj == null)
			return this;
		else if (obj instanceof Map<?, ?>)
			map((Map<?, ?>) obj);
		else
			bean(obj);
		return this;
	}

	/**
	 * Inspects the specified map and associates its keys and values.
	 *
	 * @param map
	 *            the map to be inspect
	 * @return this
	 * @see ParamMap#object(Object)
	 */
	public ParamMap map(Map<?, ?> map) {
		return map(map, 0);
	}

	private ParamMap map(Map<?, ?> map, int depth) {
		for (Entry<?, ?> e : map.entrySet()) {
			setValue(String.valueOf(e.getKey()), e.getValue(), depth);
		}
		return this;
	}

	/**
	 * Inspects the specified object and associates its result.<br>
	 *
	 * @param bean
	 *            the object to be inspect
	 * @return this
	 * @see ParamMap#object(Object)
	 */
	public ParamMap bean(Object bean) {
		return bean(bean, 0);
	}

	private ParamMap bean(Object bean, int depth) {
		for (Field f : getFields(bean.getClass())) {
			f.setAccessible(true);
			setValue(f.getName(), getValue(bean, f), depth);
		}
		return this;
	}

	private void setValue(String key, Object obj, int depth) {
		if (!isSupported(key))
			return;
		if (containsKey(key))
			throw new DuplicateKeyException("The key, '" + key
					+ "' is duplicate. One of value:" + get(key) + ", Another:"
					+ obj);
		this.put(key, obj);
		if (!isJDBCSetterType(obj) && !isEachable(obj)
				&& depth < Config.get().ENTITY_DEPTH_LIMIT) {
			ParamMap subMap;
			if (obj instanceof Map<?, ?>) {
				subMap = new ParamMap().map((Map<?, ?>) obj, depth + 1);
			} else {
				subMap = new ParamMap().bean(obj, depth + 1);
			}
			for (Entry<String, Object> e : subMap.entrySet()) {
				if (!isSupported(e.getKey()))
					continue;
				this.put(key + "_" + e.getKey(), e.getValue());
			}
		}
	}

	private String convKey(Object key) {
		if (!String.class.isInstance(key))
			throw new UnsupportedOperationException(
					"Parameter key must be String.");
		return convKey((String) key);
	}

	private String convKey(String key) {
		if (key == null)
			throw new NullPointerException("Parameter key must not be null.");
		Matcher keyM = KEY_PTN.matcher(key);
		if (!keyM.matches())
			throw new UnsupportedOperationException("Unsupported key : " + key);
		Matcher symM = SYMBOL_PTN.matcher(keyM.group(1));
		if (symM.matches())
			key = key.substring(keyM.group(1).length());
		return key;
	}

	private boolean isSupported(String key) {
		return Util.isNotEmpty(key) && key.indexOf('$') == -1;
	}
}
