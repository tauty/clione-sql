package tetz42.cellom.generator;

import static tetz42.cellom.CelloUtil.*;

import tetz42.cellom.ICell;
import tetz42.cellom.IRow;
import tetz42.cellom.ITableManager;

public class HtmlGenerator {

	public String generate(ITableManager tm) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table border=\"1\">").append(CRLF);
		sb.append("\t<thead>").append(CRLF);
		for (Iterable<ICell> row : tm.header().each()) {
			sb.append("\t\t<tr>").append(CRLF);
			for (ICell cell : row) {
				if (cell.isSkipped())
					continue;
				genTh(sb, cell);
			}
			sb.append("\t\t</tr>").append(CRLF);
		}
		sb.append("\t</thead>").append(CRLF);
		sb.append("\t<tbody>").append(CRLF);
		for (IRow row : tm.eachRow()) {
			sb.append("\t\t<tr>").append(CRLF);
			for (ICell cell : row.each()) {
				if (cell.isSkipped())
					continue;
				genTd(sb, cell);
			}
			sb.append("\t\t</tr>").append(CRLF);
		}
		sb.append("\t</tbody>").append(CRLF);
		sb.append("</table>").append(CRLF);
		return sb.toString();
	}

	private void genTh(StringBuilder sb, ICell cell) {
		genCell(sb, "th", cell);
	}

	private void genTd(StringBuilder sb, ICell cell) {
		genCell(sb, "td", cell);
	}

	private void genCell(StringBuilder sb, String thtd, ICell cell) {
		// th/td tag start
		sb.append("\t\t\t<").append(thtd);
		if (cell.getX() != 1)
			sb.append(" colspan=\"").append(cell.getX()).append("\"");
		if (cell.getY() != 1)
			sb.append(" rowspan=\"").append(cell.getY()).append("\"");
		if (isNotEmpty(cell.getStyle()))
			sb.append(" style=\"").append(cell.getStyle()).append("\"");
		sb.append(">");

		// value
		sb.append(cell.getValue());

		// th/td tag end
		sb.append("</").append(thtd).append("td>").append(CRLF);
	}
}
