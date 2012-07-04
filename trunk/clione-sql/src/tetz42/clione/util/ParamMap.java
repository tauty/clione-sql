/*
 * Copyright 2011 tetsuo.ohta[at]gmail.com
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

import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.util.ReflectionUtil.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.DuplicateKeyException;
import tetz42.clione.setting.Config;

public class ParamMap extends HashMap<String, Object> {

	/**
	 *
	 */
	private static final long serialVersionUID = -6103186429868779178L;

	public static final Pattern KEY_PTN = Pattern.compile("([\"-),-~ ])[ -~]*");
	public static final Pattern SYMBOL_PTN = Pattern.compile("[^A-Za-z0-9]+");

	@Override
	public Object get(Object key) {
		return super.get(convKey(key));
	}

	@Override
	public Object put(String key, Object value) {
		return super.put(convKey(key), value);
	}

	public ParamMap $(String key, Object value) {
		this.put(key, value);
		return this;
	}

	public ParamMap $e(String key, Object value) {
		return this.$nv(key, value, "");
	}

	public ParamMap $nv(String key, Object value, Object... negatives) {
		for (Object negative : negatives) {
			if (negative == null || negative.equals(value))
				return this;
		}
		return this.$(key, value);
	}

	public ParamMap $on(String... keys) {
		for (String key : keys)
			this.put(key, Boolean.TRUE);
		return this;
	}

	public ParamMap $orList(String key, Iterable<?> eles) {
		return this.$(key, ListWithDelim.genList(eles).or());
	}

	public <T> ParamMap $orList(String key, T... eles) {
		return this.$(key, ListWithDelim.genList(eles).or());
	}

	public ParamMap $andList(String key, Iterable<?> eles) {
		return this.$(key, ListWithDelim.genList(eles).and());
	}

	public <T> ParamMap $andList(String key, T... eles) {
		return this.$(key, ListWithDelim.genList(eles).and());
	}

	public ParamMap object(Object obj) {
		if (obj == null)
			return this;
		else if (obj instanceof Map<?, ?>)
			map((Map<?, ?>) obj);
		else
			bean(obj);
		return this;
	}

	public ParamMap map(Map<?, ?> map) {
		return map(map, 0);
	}

	private ParamMap map(Map<?, ?> map, int depth) {
		for (Entry<?, ?> e : map.entrySet()) {
			setValue(String.valueOf(e.getKey()), e.getValue(), depth);
		}
		return this;
	}

	public ParamMap bean(Object bean) {
		return bean(bean, 0);
	}

	public ParamMap bean(Object bean, int depth) {
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
		if (!isSetSQLType(obj) && !isEachable(obj)
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
		return key.indexOf('$') == -1;
	}
}
