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
import java.util.regex.Pattern;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class LineNode extends Node {

	private static final Pattern emptyLinePtn = Pattern
			.compile("\\A[ \\t]*\\z");
	private static final Pattern firstDelimPtn = Pattern.compile(
			"\\A([ \\t]*)(,|(and|or|)\\s+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern lastDelimPtn = Pattern.compile(
			"(,|and|or)\\z", Pattern.CASE_INSENSITIVE);

	public List<LineNode> childBlocks = new ArrayList<LineNode>();
	private int beginLineNo = 0;
	private int endLineNo = 0;

	public LineNode(int lineNo) {
		this(lineNo, lineNo);
	}

	public LineNode(int startNo, int endNo) {
		beginLineNo = startNo;
		endLineNo = endNo;
		setLineNo();
	}

	@Override
	public boolean isMultiLine() {
		if((endLineNo - beginLineNo) > 0)
			return true;
		if(childBlocks.size() > 0)
			return true;
		return false;
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
		System.out.println(node);
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		this.setLineNo();
		Instruction myInst = super.perform(paramMap);
		if (myInst.isNodeDisposed)
			return myInst;
		if (this.childBlocks.isEmpty())
			return myInst;
		return myInst.mergeLine(mergeChildren(paramMap));
	}

	protected Instruction mergeChildren(ParamMap paramMap) {
		Instruction result = null;
		for (LineNode child : this.childBlocks) {
			Instruction inst = child.perform(paramMap);
			if (inst.isNodeDisposed)
				continue;
			if(result == null)
				result = inst;
			else
				result.mergeLine(inst);
		}
		if(result == null)
			result = new Instruction().nodeDispose();
		return result;
	}

	public boolean isEmpty() {
		return emptyLinePtn.matcher(this.sql).find()
				&& this.holders.size() == 0 && this.childBlocks.size() == 0;
	}

	public boolean isFirstDelim() {
		// TODO implementation
		
		return false;
	}

	public boolean isLastDelim() {
		// TODO implementation
		return false;
	}
}
