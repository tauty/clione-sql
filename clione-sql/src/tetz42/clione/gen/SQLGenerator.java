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

import java.util.List;
import java.util.Map;

import tetz42.clione.lang.Instruction;
import tetz42.clione.node.SQLNode;
import tetz42.clione.util.ParamMap;

public class SQLGenerator {

	public String sql;
	public List<Object> params;
	public boolean isSqlOutputed = false;

	public String execute(Map<String, Object> map, SQLNode sqlNode) {
		pushResouceInfo(sqlNode.resourceInfo);
		try {
			ParamMap paramMap;
			if (map == null) {
				paramMap = new ParamMap();
			} else if (map instanceof ParamMap) {
				paramMap = (ParamMap) map;
			} else {
				paramMap = new ParamMap(map);
			}

			Instruction inst = sqlNode.perform(paramMap);
			this.params = inst.params;
			return this.sql = inst.replacement;
		} finally {
			popResourceInfo();
		}
	}
}
