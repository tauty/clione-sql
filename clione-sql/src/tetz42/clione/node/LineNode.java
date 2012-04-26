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
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.lang.func.SQLLiteral;
import tetz42.clione.lang.func.StrLiteral;
import tetz42.clione.util.ParamMap;
import tetz42.util.Util;

public class LineNode extends Node {

	private static final Pattern emptyLinePtn = Pattern
			.compile("\\A[ \\t]*\\z");
	private static final Pattern emptyPtn = Pattern.compile("\\s*");

	public List<LineNode> childBlocks = new ArrayList<LineNode>();
	private int beginLineNo = 0;
	private int endLineNo = 0;

	public LineNode(int lineNo) {
		beginLineNo = lineNo;
		endLineNo = lineNo;
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
		// LineNode firstNode = childBlocks.get(0);
		// LineNode lastNode = childBlocks.get(childBlocks.size() - 1);
		LineNode firstNode = null;
		LineNode lastNode = null;
		LineNode firstMergedNode = null;
		LineNode lastMergedNode = null;
		boolean isDisposeExsists = false;
		for (LineNode child : this.childBlocks) {
			if (!EmptyLineNode.class.isInstance(child)) {
				lastNode = child;
				if (firstNode == null)
					firstNode = child;
			}
			Instruction inst = child.perform(paramMap);
			if (inst.isNodeDisposed) {
				isDisposeExsists = true;
				continue;
			}
			if (result == null) {
				result = inst;
			} else {
				result.mergeLine(inst);
			}
			if (firstMergedNode == null
					&& !EmptyLineNode.class.isInstance(child))
				firstMergedNode = child;
			if (!EmptyLineNode.class.isInstance(child))
				lastMergedNode = child;
		}
		if (isDisposeExsists
				&& (result == null || result.replacement == null || emptyPtn
						.matcher(result.replacement).matches()))
			return new Instruction().nodeDispose();
		return removeDelimiters(result, firstNode, firstMergedNode, lastNode,
				lastMergedNode);
	}

	private Instruction removeDelimiters(Instruction result,
			LineNode firstNode, LineNode firstMergedNode, LineNode lastNode,
			LineNode lastMergedNode) {
		if (firstNode == null || firstMergedNode == null)
			return result;
		if (firstNode != firstMergedNode && !firstNode.isFirstDelim()
				&& firstMergedNode.isFirstDelim()) {
			// remove first Delim
			Matcher m = firstDelimPtn.matcher(result.replacement);
			if (m.find())
				removeDelimiter(result, m, 2);
		}
		if (lastNode != lastMergedNode && !lastNode.isLastDelim()
				&& lastMergedNode.isLastDelim()) {
			// remove last Delimiter
			Matcher m = lastDelimPtn.matcher(result.replacement);
			if (m.find())
				removeDelimiter(result, m, 1);
		}
		return result;
	}

	private void removeDelimiter(Instruction result, Matcher m, int group) {
		String s = result.replacement;
		result.replacement(s.substring(0, m.start(group))
				+ s.substring(m.end(group)));
	}

	public boolean isEmpty() {
		return emptyLinePtn.matcher(this.sql).find()
				&& this.holders.size() == 0 && this.childBlocks.size() == 0;
	}

	private static final Pattern firstDelimPtn = Pattern.compile(
			"\\A(\\s*)(,|(and|or)\\s+|union\\s+(all\\s+)?)?",
			Pattern.CASE_INSENSITIVE);

	public boolean isFirstDelim() {
		Matcher m = firstDelimPtn.matcher(sql);
		m.find();
		String delim = m.group(2);

		if (holders.size() != 0) {
			IPlaceHolder holder = holders.get(0);
			if (holder.getPosition() <= m.end(1)) {
				ClioneFunction cf = holder.getFunction();
				if (cf instanceof SQLLiteral || cf instanceof StrLiteral) {
					Matcher m2 = firstDelimPtn.matcher(cf.getLiteral());
					return m2.find() && !Util.isEmpty(m2.group(2));
				}
				return false;
			}
		}
		return !Util.isEmpty(delim);
	}

	private static final Pattern lastDelimPtn = Pattern
			.compile("(,|and|or|union(\\s+all)?)?([ \\t]*)\\z",
					Pattern.CASE_INSENSITIVE);

	public boolean isLastDelim() {
		Matcher m = lastDelimPtn.matcher(sql);
		m.find();
		String delim = m.group(1);

		if (holders.size() != 0) {
			IPlaceHolder holder = holders.get(holders.size() - 1);
			if (holder.getPosition() >= m.start(2)) {
				ClioneFunction cf = holder.getFunction();
				if (cf instanceof SQLLiteral || cf instanceof StrLiteral) {
					Matcher m2 = lastDelimPtn.matcher(cf.getLiteral());
					return m2.find() && Util.isEmpty(m.group(1));
				}
				return false;
			}
		}
		return !Util.isEmpty(delim);
	}
}
