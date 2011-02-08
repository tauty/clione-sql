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
package tetz42.clione.node;

import java.util.ArrayList;
import java.util.List;

public class LineNode {
	public StringBuilder sql = new StringBuilder();
	public ArrayList<String> keys = new ArrayList<String>();
	public ArrayList<String> vals = new ArrayList<String>();
	public List<LineNode> childBlocks = new ArrayList<LineNode>();
}
