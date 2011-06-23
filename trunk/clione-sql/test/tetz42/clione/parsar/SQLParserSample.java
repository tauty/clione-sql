package tetz42.clione.parsar;

import static tetz42.clione.util.ClioneUtil.*;

public class SQLParserSample {

	public static void main(String args[]) {
		new SQLParser("1111").parse("aaaa/* bbb */ccc");
		System.out.println("---------------------------");
		new SQLParser("2222").parse(new StringBuilder().append("aaa/*bbbccc")
				.append(CRLF).append("dddd*/eeee").toString());
		System.out.println("---------------------------");
		new SQLParser("3333").parse(new StringBuilder().append("aaa/*bbbccc")
				.append(CRLF).append("dddd*/eeee/* tako */ aaa").toString());
		System.out.println("---------------------------");
		new SQLParser("4444").parse(new StringBuilder().append("aaa/*bbbccc")
				.append(CRLF).append("dddd*/eeee/*! tako */ aaa").toString());
		System.out.println("---------------------------");
		new SQLParser("5555").parse(new StringBuilder().append("aaa/*bbbccc")
				.append(CRLF).append("dddd*/'tako*/ika' eeee/* tako */ aaa")
				.toString());
		System.out.println("---------------------------");
		new SQLParser("6666").parse(new StringBuilder().append("aaa/*bbbccc")
				.append(CRLF).append("dddd*/'tako*/ika' eeee/* tako */is null")
				.toString());
		System.out.println("---------------------------");
		new SQLParser("7777")
				.parse(new StringBuilder()
						.append("aaa/*bbbccc")
						.append(CRLF)
						.append("dddd*/'tako*/ika' eeee/* tako */not in ('aaa', 'b)b', 'ccc')")
						.toString());
		System.out.println("---------------------------");
		new SQLParser("8888").parse(new StringBuilder().append("WHERE")
				.append(CRLF).append("  ID = /* $ID */'912387'").append(CRLF)
				.append("  AND (").append(CRLF)
				.append("    PREF /* $PREF */= 'TOKYO'").append(CRLF)
				.append("    OR COUNTORY = /* $CONTORY */'JAPAN'").append(CRLF)
				.append("  )").toString());
	}
}
