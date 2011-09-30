package tetz42.clione.parsar;

import java.util.LinkedList;
import java.util.regex.Matcher;

import tetz42.clione.node.IPlaceHolder;
import tetz42.clione.node.LineNode;
import tetz42.clione.node.Node;

public class LineInfo {

	LinkedList<LineInfo> stack = new LinkedList<LineInfo>();
	Node node;
	LineNode lineNode;
	StringBuilder nodeSb;
	StringBuilder lineSb;
	int lineNo;

	void addPlaceHolder(IPlaceHolder holder) {
		holder.setPosition(this.nodeSb.length());
		this.node.holders.add(holder);
	}

	LineInfo(int lineNo) {
		this.node = new Node();
		this.nodeSb = new StringBuilder();
		this.lineNode = new LineNode(lineNo);
		this.lineSb = new StringBuilder();
		this.lineNo = lineNo;
	}

	void mergeNode() {
		for (IPlaceHolder h : this.node.holders) {
			h.movePosition(this.lineSb.length());
			lineNode.holders.add(h);
		}
		this.lineSb.append(this.nodeSb);
		this.node = new Node();
		this.nodeSb.setLength(0);
	}

	Node fixNode() {
		Matcher m = SQLParserSample.indentPtn.matcher(this.nodeSb);
		if (m.find()) {
			String indent = m.group();
			this.lineSb.append(indent);
			this.node.sql = this.nodeSb.substring(indent.length());
		} else {
			this.node.sql = this.nodeSb.toString();
		}

		Node node = this.node;
		this.node = new Node();
		this.nodeSb.setLength(0);
		return node;
	}

	LineNode fixLineNode() {
		this.lineNode.sql = this.lineSb.toString();
		LineNode lineNode = this.lineNode;
		this.lineNo++;
		this.clear();
		return lineNode;
	}

	void addLineNo() {
		this.lineNo++;
		this.lineNode.curLineNo(lineNo);
	}

	void clear() {
		this.lineNode = new LineNode(lineNo);
		this.lineSb.setLength(0);
	}

	LineInfo push() {
		// backup
		LineInfo backup = new LineInfo(0);
		backup.node = this.node;
		backup.lineNode = this.lineNode;
		backup.nodeSb = this.nodeSb;
		backup.lineSb = this.lineSb;
		stack.push(backup);

		// initialize
		this.node = new Node();
		this.lineNode = new LineNode(this.lineNo);
		this.nodeSb = new StringBuilder();
		this.lineSb = new StringBuilder();
		return this;
	}

	LineInfo pop() {

		// reset to backup
		LineInfo backup = stack.pop();
		this.node = backup.node;
		this.lineNode = backup.lineNode;
		this.lineNode.curLineNo(this.lineNo);
		this.nodeSb = backup.nodeSb;
		this.lineSb = backup.lineSb;

		return this;
	}
}

