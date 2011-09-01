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

import static tetz42.clione.lang.ContextUtil.*;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class LineNode implements INode {
	public String sql;
	public List<IPlaceHolder> holders = new ArrayList<IPlaceHolder>();
	public List<LineNode> childBlocks = new ArrayList<LineNode>();
	private int beginLineNo = 0;
	private int endLineNo = 0;

	public LineNode(int lineNo) {
		this(lineNo, lineNo);
	}

	public LineNode(int startNo, int endNo) {
		beginLineNo = startNo;
		setBeginLineNo(startNo);
		endLineNo = endNo;
		setEndLineNo(endNo);
	}

	public LineNode curLineNo(int lineNo) {
		endLineNo = lineNo;
		setEndLineNo(lineNo);
		return this;
	}

	public void setLineNo() {
		setBeginLineNo(beginLineNo);
		setEndLineNo(endLineNo);
	}

	public boolean isDisposable = false;

	public void merge(LineNode node) {
		// TODO implementation
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		// TODO Auto-generated method stub
		return null;
	}
}
