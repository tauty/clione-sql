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
package tetz42.clione.gen;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.lang.Instruction;
import tetz42.clione.node.LineNode;
import tetz42.clione.node.PlaceHolder;
import tetz42.clione.node.SQLNode;
import tetz42.clione.util.ParamMap;

public class SQLGenerator {

	private static final Pattern delimPtn = Pattern.compile(
			"\\A(\\s*)(,|and\\s+|or\\s+)(.+)\\z", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL);
	private static final Pattern parenthesisClosePtn = Pattern
			.compile("^\\s*\\)");

	public String sql;
	public ArrayList<Object> params;
	private final Object[] nilValues;

	public SQLGenerator() {
		this(null);
	}

	public SQLGenerator(Object[] nilValues) {
		this.nilValues = nilValues;
	}

	public String genSql(Map<String, Object> map, SQLNode sqlNode) {
		pushResouceInfo(sqlNode.resourceInfo);
		addNil(nilValues);
		try {
			ParamMap paramMap;
			if (map == null)
				paramMap = new ParamMap();
			else if (map instanceof ParamMap) {
				paramMap = (ParamMap) map;
			} else {
				paramMap = new ParamMap();
				paramMap.putAll(map);
			}

			StringBuilder sb = new StringBuilder();
			ArrayList<Object> params = new ArrayList<Object>();

			this.genSql(sqlNode.nodes, paramMap, sb, params);
			this.params = params;
			return this.sql = sb.toString();
		} finally {
			popResourceInfo();
			clearNil();
		}
	}

	private void genSql(List<LineNode> lineNodes, ParamMap paramMap,
			StringBuilder sb, ArrayList<Object> params) {
		int startTimeLength = sb.length();
		boolean isThereFirstDelim = false;
		if(lineNodes.size() > 0){
			if(delimPtn.matcher(lineNodes.get(0).sql).find())
				isThereFirstDelim = true;
		}
		for (LineNode lineNode : lineNodes) {
			lineNode.setLineNo(); // for line No. information of resourceInfo
			ArrayList<Object> subParams = new ArrayList<Object>();
			StringBuilder subSql = genSubSql(lineNode, subParams, paramMap);
			if (subSql == null)
				continue;
			if (!lineNode.childBlocks.isEmpty()) {
				StringBuilder subSb = new StringBuilder();
				this.genSql(lineNode.childBlocks, paramMap, subSb, subParams);
				if (subSb.length() == 0)
					continue;
				subSql.append(CRLF).append(subSb);
			}
			if (sb.length() == startTimeLength
					&& parenthesisClosePtn.matcher(subSql).find())
				continue; // ignore ')'
			if (sb.length() != 0)
				sb.append(CRLF);
			sb.append(subSql);
			params.addAll(subParams);
		}
		if (!isThereFirstDelim)
			removeFirstDelimiter(sb);
	}

	private StringBuilder genSubSql(LineNode block,
			ArrayList<Object> subParams, ParamMap paramMap) {
		StringBuilder subSql = new StringBuilder(block.sql);
		int sabun = 0;
		for (PlaceHolder holder : block.holders) {
			Instruction inst = holder.perform(paramMap);
			if (inst.isNodeDisposed)
				return null;
			if (inst.doNothing)
				continue;
			String replacement = inst.getReplacement();
			subSql.replace(holder.begin + sabun, holder.begin + holder.length
					+ sabun, replacement);
			subParams.addAll(inst.params);
			sabun += replacement.length();
		}
		return subSql;
	}

	private void removeFirstDelimiter(StringBuilder subSb) {
		Matcher m = delimPtn.matcher(subSb);
		if (m.find()) {
			int len1 = m.group(1).length();
			int len2 = m.group(2).length();
			subSb.delete(len1, len1 + len2);
		}
	}
}
