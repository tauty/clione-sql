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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class LineNode extends Node {

	private static final Pattern emptyLinePtn = Pattern
			.compile("\\A[ \\t]*\\z");
	private static final Pattern firstDelimPtn = Pattern.compile(
			"\\A[ \\t]*(,|(and|or)\\s+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern lastDelimPtn = Pattern.compile(
			"(,|and|or)[ \\t]*\\z", Pattern.CASE_INSENSITIVE);

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
		if ((endLineNo - beginLineNo) > 0)
			return true;
		if (childBlocks.size() > 0)
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
		Instruction inst = mergeChildren(paramMap);
		return inst.doNothing && !inst.isNodeDisposed ? myInst : myInst
				.mergeLine(inst);
	}

	protected Instruction mergeChildren(ParamMap paramMap) {
		if (this.childBlocks.isEmpty())
			return new Instruction().doNothing();
		Instruction result = null;
		LineNode firstNode = childBlocks.get(0);
		LineNode lastNode = childBlocks.get(childBlocks.size() - 1);
		LineNode firstMergedNode = null;
		LineNode lastMergedNode = null;
		boolean isAllEmpty = true;
		for (LineNode child : this.childBlocks) {
			Instruction inst = child.perform(paramMap);
			if (inst.isNodeDisposed)
				continue;
			if (result == null) {
				result = inst;
				firstMergedNode = child;
			} else {
				result.mergeLine(inst);
			}
			lastMergedNode = child;
			if (!EmptyLineNode.class.isInstance(child))
				isAllEmpty = false;
		}
		if (result == null || isAllEmpty)
			return new Instruction().nodeDispose();
		return removeDelimiters(result, firstNode, firstMergedNode, lastNode,
				lastMergedNode);
	}

	private Instruction removeDelimiters(Instruction result,
			LineNode firstNode, LineNode firstMergedNode, LineNode lastNode,
			LineNode lastMergedNode) {
		if (firstNode != firstMergedNode && !firstNode.isFirstDelim()
				&& firstMergedNode.isFirstDelim()) {
			// remove first Delim
			Matcher m = firstDelimPtn.matcher(result.replacement);
			if (m.find())
				removeDelimiter(result, m);
		}
		if (lastNode != lastMergedNode && !lastNode.isLastDelim()
				&& lastMergedNode.isLastDelim()) {
			// remove last Delim
			Matcher m = lastDelimPtn.matcher(result.replacement);
			if (m.find())
				removeDelimiter(result, m);
		}
		return result;
	}

	private void removeDelimiter(Instruction result, Matcher m) {
		String s = result.replacement;
		result.replacement(s.substring(0, m.start(1)) + s.substring(m.end(1)));
	}

	public boolean isEmpty() {
		return emptyLinePtn.matcher(this.sql).find()
				&& this.holders.size() == 0 && this.childBlocks.size() == 0;
	}

	public boolean isFirstDelim() {
		Matcher m = firstDelimPtn.matcher(sql);
		if (!m.find())
			return false;
		if (holders.isEmpty())
			return true;
		return m.start(1) < holders.get(0).getPosition();
	}

	public boolean isLastDelim() {
		Matcher m = lastDelimPtn.matcher(sql);
		if (!m.find())
			return false;
		if (holders.isEmpty())
			return true;
		return m.end(1) >= holders.get(holders.size() - 1).getPosition();
	}
}
