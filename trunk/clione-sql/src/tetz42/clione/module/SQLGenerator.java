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
package tetz42.clione.module;

import static tetz42.clione.module.GetBackReader.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLGenerator {

	private static final Pattern delimPtn = Pattern.compile(
			"\\A(\\s*(,|and\\s|or\\s))", Pattern.CASE_INSENSITIVE);
	private static final Pattern parenthesisClosePtn = Pattern
			.compile("^\\s*\\)");

	public String sql;
	public ArrayList<Object> params;

	public String genSql(Map<String, Object> paramMap, List<LineTree> lineTreeList) {
		if (paramMap == null)
			paramMap = new ParamMap();
		else if (!ParamMap.class.isInstance(paramMap)) {
			ParamMap map = new ParamMap();
			map.putAll(paramMap);
			paramMap = map;
		}

		StringBuilder sb = new StringBuilder();
		ArrayList<Object> params = new ArrayList<Object>();

		this.genSql(lineTreeList, paramMap, sb, params);
		this.params = params;
		return this.sql = sb.toString();
	}

	private void genSql(List<LineTree> blockList, Map<String, Object> paramMap,
			StringBuilder sb, ArrayList<Object> params) {
		int startTimeLength = sb.length();
		for (LineTree block : blockList) {
			ArrayList<Object> subParams = new ArrayList<Object>();
			StringBuilder subSql = genSubSql(block, subParams, paramMap);
			if (subSql == null)
				continue;
			if (!block.childBlocks.isEmpty()) {
				StringBuilder subSb = new StringBuilder();
				this.genSql(block.childBlocks, paramMap, subSb, subParams);
				if (subSb.length() == 0)
					continue;
				subSql.append(CRLF).append(removeFirstDelimiter(subSb));
			}
			if (sb.length() == startTimeLength
					&& parenthesisClosePtn.matcher(subSql).find())
				continue; // ignore ')'
			if (sb.length() != 0)
				sb.append(CRLF);
			sb.append(subSql);
			params.addAll(subParams);
		}
	}

	private StringBuilder genSubSql(LineTree block,
			ArrayList<Object> subParams, Map<String, Object> paramMap) {
		StringBuilder subSql = new StringBuilder(block.sql);
		int pos = 0;
		for (int i = 0; i < block.keys.size(); i++) {
			String key = block.keys.get(i);
			Object val = paramMap.get(key);
			Collection<?> vals = convToCol(val);
			boolean isNoParam = val == null || (vals != null && vals.size() == 0);
			if (isNoParam) {
				if (key.startsWith("$") || key.startsWith("&"))
					return null;
				if (key.startsWith("@"))
					throw new ParameterNotFoundException("The parameter, '"
							+ key + "', is required.");
			}

			// '&' means without replace parameter.
			if (key.startsWith("&"))
				continue;

			int begin = subSql.indexOf("?", pos);
			if (isNoParam) {
				String defVal = block.vals.get(i);
				if (defVal.endsWith(")")) {
					subSql.replace(begin - 1, begin + 2, block.vals.get(i));
					pos = begin + block.vals.get(i).length() - 1;
				} else {
					subSql.replace(begin, begin + 1, block.vals.get(i));
					pos = begin + block.vals.get(i).length();
				}
			} else {
				if (vals != null) {
					String questions = genQuestions(vals);
					subSql.replace(begin, begin + 1, questions);
					pos = begin + questions.length();
					subParams.addAll(vals);
				} else {
					pos = begin + 1;
					subParams.add(val);
				}
			}
		}
		return subSql;
	}

	private String removeFirstDelimiter(StringBuilder subSb) {
		Matcher m = delimPtn.matcher(subSb);
		return m.find() ? subSb.substring(m.group(1).length()) : subSb
				.toString();
	}

	private Collection<?> convToCol(Object val) {
		if (val == null)
			return null;
		if (val.getClass().isArray()) {
			ArrayList<Object> list = new ArrayList<Object>();
			for (int i = 0; i < Array.getLength(val); i++) {
				list.add(Array.get(val, i));
			}
			return list;
		} else if (val instanceof Collection)
			return (Collection<?>) val;
		return null;
	}

	private String genQuestions(Collection<?> params) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			if (i != 0)
				sb.append(", ");
			sb.append("?");
		}
		return sb.toString();
	}
}
