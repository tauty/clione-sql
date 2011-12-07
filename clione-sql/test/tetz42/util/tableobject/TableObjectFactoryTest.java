package tetz42.util.tableobject;

import static tetz42.test.Auty.*;

import org.junit.Test;

import tetz42.util.tableobject.annotation.ColumnDef;
import tetz42.util.tableobject.tables.TableObject3;
import tetz42.util.tableobject.tables.TableObject4;

public class TableObjectFactoryTest {

	@Test
	public void tableObj3() {
		TableObject3<String, ColumnSet1, ColumnSet2> to3 = TableObjectFactory
				.create(String.class, ColumnSet1.class, ColumnSet2.class);

		to3.setHeader("Header1", "Header2");
		to3.setHeaderAs2("Header3", "Header4", "Header5");
		to3.setHeaderAs3("LastColumn");

		// new row
		to3.newRow();
		to3.get("Header1").set("111");
		to3.get("Header2").set("222");

		to3.getAs2("Header3").get().column1 = "333";
		to3.getAs2("Header3").get().column2 = "444";
		to3.getAs2("Header3").get().column3 = 10;

		to3.getAs3("LastColumn").get().column1 = 1;
		to3.getAs3("LastColumn").get().column2 = 10;
		to3.getAs3("LastColumn").get().column3 = "100";

		// tail
		to3.tail().getAs1("Header1").set("-------");
		to3.tail().getAs1("Header2").set("Summary");
		to3.tail().getAs2("Header3").get().column3 += 10;
		to3.tail().getAs3("LastColumn").get().column3 = "111";

		// new row
		to3.newRow();
		to3.getAs1("Header1").set("1111");
		to3.getAs1("Header2").set("2222");
		to3.getAs2("Header3").get().column1 = "3333";
		to3.getAs2("Header3").get().column2 = "4444";
		to3.getAs2("Header3").get().column3 = 100;
		to3.getAs3("LastColumn").get().column1 = 10;
		to3.getAs3("LastColumn").get().column2 = 100;
		to3.getAs3("LastColumn").get().column3 = "1000";

		// tail
		to3.tail().getAs1("Header1").set("-------");
		to3.tail().getAs1("Header2").set("Summary");
		to3.tail().getAs2("Header3").get().column3 += 10;
		to3.tail().getAs3("LastColumn").get().column3 = "1111";

		to3.setDisplayHeaders(1);
		assertEqualsWithFile(to3.toString(), getClass(), "tableObj3");

		// headerLevel2にして実行
		to3.setDisplayHeaders(0, 1);
		assertEqualsWithFile(to3.toString(), getClass(), "tableObj3-level2");

	}

	@Test
	public void tableObj4() {
		TableObject4<String, Integer, ColumnSet1, ColumnSet2> to = TableObjectFactory
				.create(String.class, Integer.class, ColumnSet1.class,
						ColumnSet2.class);

		to.setHeaderAs2("Header1", "Header2");
		to.setHeaderAs1("Header3");
		to.setHeaderAs3("Header4", "Header5");
		to.setHeaderAs4("Header6");
		to.setHeaderAs1("Last");

		// tako
		to.setRow("tako");
		to.getAs2("Header1").set(1729);
		to.getAs2("Header2").set(42);
		to.getAs1("Header3").set("42と1729ついて");

		to.getAs3("Header4").get().column1 = "宇宙全ての";
		to.getAs3("Header4").get().column2 = "答えは？";
		to.getAs3("Header4").get().column3 = 42;

		to.getAs3("Header5").get().column1 = "二通りの自然数の３乗数の和";
		to.getAs3("Header5").get().column2 = "で表現できる最小数";
		to.getAs3("Header5").get().column3 = 1729;

		to.getAs4("Header6").get().column1 = 42;
		to.getAs4("Header6").get().column2 = 1729;
		to.getAs4("Header6").get().column3 = "もう一度書きます。";

		to.getAs1("Last").set("以上。");

		// final
		to.setTailRow("final");
		to.getAs2("Header1").set(1729);
		to.getAs2("Header2").set(42);
		to.getAs1("Header3").set("42と1729ついて");

		to.getAs3("Header4").get().column1 = "宇宙全ての";
		to.getAs3("Header4").get().column2 = "答えは？";
		to.getAs3("Header4").get().column3 = 42;

		to.getAs3("Header5").get().column1 = "二通りの自然数の３乗数の和";
		to.getAs3("Header5").get().column2 = "で表現できる最小数";
		to.getAs3("Header5").get().column3 = 1729;

		to.getAs4("Header6").get().column1 = 42;
		to.getAs4("Header6").get().column2 = 1729;
		to.getAs4("Header6").get().column3 = "もう一度書きます。";

		to.getAs1("Last").set("以上。");

		// ika
		to.setRow("ika");
		to.getAs2("Header1").set(314);
		to.getAs2("Header2").set(1592);
		to.getAs1("Header3").set("314と1592ついて");

		to.getAs3("Header4").get().column1 = "円周率";
		to.getAs3("Header4").get().column2 = "です。ただの。";
		to.getAs3("Header4").get().column3 = 314;

		to.getAs3("Header5").get().column1 = "円周率の";
		to.getAs3("Header5").get().column2 = "４～７桁目";
		to.getAs3("Header5").get().column3 = 1592;

		to.getAs4("Header6").get().column1 = 314;
		to.getAs4("Header6").get().column2 = 1592;
		to.getAs4("Header6").get().column3 = "もう一度書くんです。";

		to.getAs1("Last").set("終了。");

		// final
		to.setTailRow("final");
		to.getAs2("Header1").set(42 + 314);
		to.getAs2("Header2").set(1729 + 1592);
		to.getAs1("Header3").set("42と1729ついてと314と1592ついて");

		to.getAs3("Header4").get().column1 += "円周率";
		to.getAs3("Header4").get().column2 += "です。ただの。";
		to.getAs3("Header4").get().column3 += 314;

		to.getAs3("Header5").get().column1 += "円周率の";
		to.getAs3("Header5").get().column2 += "４～７桁目";
		to.getAs3("Header5").get().column3 += 1592;

		to.getAs4("Header6").get().column1 += 314;
		to.getAs4("Header6").get().column2 += 1592;
		to.getAs4("Header6").get().column3 += "もう一度書くんです。";

		to.getAs1("Last").set("That's all for today.");

		to.setDisplayHeaders(1);
		assertEqualsWithFile(to.toString(), getClass(), "tableObj4");

		// 書換テスト
		to.row(0).getAs4("Header6").get().column3 += "おーほっほっほっほっ。";
		to.row("ika").getAs4("Header6").get().column3 += "ゲボハハハハ。";
		to.tail().getAs3("Header5").get().column2 += " www";
		to.tail("final").getAs4("Header6").get().column3 += " lol";
		assertEqualsWithFile(to.toString(), getClass(), "tableObj4-after");

		// headerLevel2にして実行
		to.setDisplayHeaders(0, 1);
		assertEqualsWithFile(to.toString(), getClass(), "tableObj4-level2");
	}

	@Test
	public void iteration() {
		TableObject4<String, Integer, ColumnSet1, ColumnSet2> to = TableObjectFactory
				.create(String.class, Integer.class, ColumnSet1.class,
						ColumnSet2.class);

		// ------ tableObj4 と同じ Start -----//

		to.setHeaderAs2("Header1", "Header2");
		to.setHeaderAs1("Header3");
		to.setHeaderAs3("Header4", "Header5");
		to.setHeaderAs4("Header6");
		to.setHeaderAs1("Last");

		// tako
		to.setRow("tako");
		to.getAs2("Header1").set(1729);
		to.getAs2("Header2").set(42);
		to.getAs1("Header3").set("42と1729ついて");

		to.getAs3("Header4").get().column1 = "宇宙全ての";
		to.getAs3("Header4").get().column2 = "答えは？";
		to.getAs3("Header4").get().column3 = 42;

		to.getAs3("Header5").get().column1 = "二通りの自然数の３乗数の和";
		to.getAs3("Header5").get().column2 = "で表現できる最小数";
		to.getAs3("Header5").get().column3 = 1729;

		to.getAs4("Header6").get().column1 = 42;
		to.getAs4("Header6").get().column2 = 1729;
		to.getAs4("Header6").get().column3 = "もう一度書きます。";

		to.getAs1("Last").set("以上。");

		// final
		to.setTailRow("final");
		to.getAs2("Header1").set(1729);
		to.getAs2("Header2").set(42);
		to.getAs1("Header3").set("42と1729ついて");

		to.getAs3("Header4").get().column1 = "宇宙全ての";
		to.getAs3("Header4").get().column2 = "答えは？";
		to.getAs3("Header4").get().column3 = 42;

		to.getAs3("Header5").get().column1 = "二通りの自然数の３乗数の和";
		to.getAs3("Header5").get().column2 = "で表現できる最小数";
		to.getAs3("Header5").get().column3 = 1729;

		to.getAs4("Header6").get().column1 = 42;
		to.getAs4("Header6").get().column2 = 1729;
		to.getAs4("Header6").get().column3 = "もう一度書きます。";

		to.getAs1("Last").set("以上。");

		// ika
		to.setRow("ika");
		to.getAs2("Header1").set(314);
		to.getAs2("Header2").set(1592);
		to.getAs1("Header3").set("314と1592ついて");

		to.getAs3("Header4").get().column1 = "円周率";
		to.getAs3("Header4").get().column2 = "です。ただの。";
		to.getAs3("Header4").get().column3 = 314;

		to.getAs3("Header5").get().column1 = "円周率の";
		to.getAs3("Header5").get().column2 = "４～７桁目";
		to.getAs3("Header5").get().column3 = 1592;

		to.getAs4("Header6").get().column1 = 314;
		to.getAs4("Header6").get().column2 = 1592;
		to.getAs4("Header6").get().column3 = "もう一度書くんです。";

		to.getAs1("Last").set("終了。");

		// final
		to.setTailRow("final");
		to.getAs2("Header1").set(42 + 314);
		to.getAs2("Header2").set(1729 + 1592);
		to.getAs1("Header3").set("42と1729ついてと314と1592ついて");

		to.getAs3("Header4").get().column1 += "円周率";
		to.getAs3("Header4").get().column2 += "です。ただの。";
		to.getAs3("Header4").get().column3 += 314;

		to.getAs3("Header5").get().column1 += "円周率の";
		to.getAs3("Header5").get().column2 += "４～７桁目";
		to.getAs3("Header5").get().column3 += 1592;

		to.getAs4("Header6").get().column1 += 314;
		to.getAs4("Header6").get().column2 += 1592;
		to.getAs4("Header6").get().column3 += "もう一度書くんです。";

		to.getAs1("Last").set("That's all for today.");

		assertEqualsWithFile(to.toString(), getClass(), "iteration-default");

		// 書換テスト
		to.row(0).getAs4("Header6").get().column3 += "おーほっほっほっほっ。";
		to.row("ika").getAs4("Header6").get().column3 += "ゲボハハハハ。";
		to.tail().getAs3("Header5").get().column2 += " www";
		to.tail("final").getAs4("Header6").get().column3 += " lol";

		// ------ tableObj4 と同じ End ------ //
		to.setDisplayHeaders(1);
		assertEqualsWithFile(to.toString(), getClass(), "iteration");

		// headerLevel2にして実行
		to.setDisplayHeaders(0, 1);
		assertEqualsWithFile(to.toString(), getClass(), "iteration-level2");
	}

	@Test
	public void removeTest() {
		TableObject4<String, Integer, ColumnSet1, ColumnSet2> to = TableObjectFactory
				.create(String.class, Integer.class, ColumnSet1.class,
						ColumnSet2.class);

		// ------ tableObj4 と同じ Start -----//

		to.setHeaderAs2("Header1", "Header2");
		to.setHeaderAs1("Header3");
		to.setHeaderAs3("Header4", "Header5");
		to.setHeaderAs4("Header6");
		to.setHeaderAs1("Last");

		// tako
		to.setRow("tako");
		to.getAs2("Header1").set(1729);
		to.getAs2("Header2").set(42);
		to.getAs1("Header3").set("42と1729ついて");

		to.getAs3("Header4").get().column1 = "宇宙全ての";
		to.getAs3("Header4").get().column2 = "答えは？";
		to.getAs3("Header4").get().column3 = 42;

		to.getAs3("Header5").get().column1 = "二通りの自然数の３乗数の和";
		to.getAs3("Header5").get().column2 = "で表現できる最小数";
		to.getAs3("Header5").get().column3 = 1729;

		to.getAs4("Header6").get().column1 = 42;
		to.getAs4("Header6").get().column2 = 1729;
		to.getAs4("Header6").get().column3 = "もう一度書きます。";

		to.getAs1("Last").set("以上。");

		// final
		to.setTailRow("final");
		to.getAs2("Header1").set(1729);
		to.getAs2("Header2").set(42);
		to.getAs1("Header3").set("42と1729ついて");

		to.getAs3("Header4").get().column1 = "宇宙全ての";
		to.getAs3("Header4").get().column2 = "答えは？";
		to.getAs3("Header4").get().column3 = 42;

		to.getAs3("Header5").get().column1 = "二通りの自然数の３乗数の和";
		to.getAs3("Header5").get().column2 = "で表現できる最小数";
		to.getAs3("Header5").get().column3 = 1729;

		to.getAs4("Header6").get().column1 = 42;
		to.getAs4("Header6").get().column2 = 1729;
		to.getAs4("Header6").get().column3 = "もう一度書きます。";

		to.getAs1("Last").set("以上。");

		// ika
		to.setRow("ika");
		to.getAs2("Header1").set(314);
		to.getAs2("Header2").set(1592);
		to.getAs1("Header3").set("314と1592ついて");

		to.getAs3("Header4").get().column1 = "円周率";
		to.getAs3("Header4").get().column2 = "です。ただの。";
		to.getAs3("Header4").get().column3 = 314;

		to.getAs3("Header5").get().column1 = "円周率の";
		to.getAs3("Header5").get().column2 = "４～７桁目";
		to.getAs3("Header5").get().column3 = 1592;

		to.getAs4("Header6").get().column1 = 314;
		to.getAs4("Header6").get().column2 = 1592;
		to.getAs4("Header6").get().column3 = "もう一度書くんです。";

		to.getAs1("Last").set("終了。");

		// final
		to.setTailRow("final");
		to.getAs2("Header1").set(42 + 314);
		to.getAs2("Header2").set(1729 + 1592);
		to.getAs1("Header3").set("42と1729ついてと314と1592ついて");

		to.getAs3("Header4").get().column1 += "円周率";
		to.getAs3("Header4").get().column2 += "です。ただの。";
		to.getAs3("Header4").get().column3 += 314;

		to.getAs3("Header5").get().column1 += "円周率の";
		to.getAs3("Header5").get().column2 += "４～７桁目";
		to.getAs3("Header5").get().column3 += 1592;

		to.getAs4("Header6").get().column1 += 314;
		to.getAs4("Header6").get().column2 += 1592;
		to.getAs4("Header6").get().column3 += "もう一度書くんです。";

		to.getAs1("Last").set("That's all for today.");

		// 書換テスト
		to.row(0).getAs4("Header6").get().column3 += "おーほっほっほっほっ。";
		to.row("ika").getAs4("Header6").get().column3 += "ゲボハハハハ。";
		to.tail().getAs3("Header5").get().column2 += " www";
		to.tail("final").getAs4("Header6").get().column3 += " lol";

		// headerLevel2にして実行
		to.setDisplayHeaders(0, 1);
		assertEqualsWithFile(to.toString(), getClass(), "removeTest-before");

		// "tako"を削除して実行
		to.setRow("tako");
		to.removeRow();
		assertEqualsWithFile(to.toString(), getClass(), "removeTest-after");
	}

	@Test
	public void removeColumnTest() {
		TableObject4<String, Integer, ColumnSet1, ColumnSet2> to = TableObjectFactory
				.create(String.class, Integer.class, ColumnSet1.class,
						ColumnSet2.class);

		// ------ tableObj4 と同じ Start -----//
		to.setHeaderAs2("Header1", "Header2");
		to.setHeaderAs1("Header3");
		to.setHeaderAs3("Header4", "Header5");
		to.setHeaderAs4("Header6");
		to.setHeaderAs1("Last");

		// tako
		to.setRow("tako");
		to.getAs2("Header1").set(1729);
		to.getAs2("Header2").set(42);
		to.getAs1("Header3").set("42と1729ついて");

		to.getAs3("Header4").get().column1 = "宇宙全ての";
		to.getAs3("Header4").get().column2 = "答えは？";
		to.getAs3("Header4").get().column3 = 42;

		to.getAs3("Header5").get().column1 = "二通りの自然数の３乗数の和";
		to.getAs3("Header5").get().column2 = "で表現できる最小数";
		to.getAs3("Header5").get().column3 = 1729;

		to.getAs4("Header6").get().column1 = 42;
		to.getAs4("Header6").get().column2 = 1729;
		to.getAs4("Header6").get().column3 = "もう一度書きます。";

		to.getAs1("Last").set("以上。");

		// final
		to.setTailRow("final");
		to.getAs2("Header1").set(1729);
		to.getAs2("Header2").set(42);
		to.getAs1("Header3").set("42と1729ついて");

		to.getAs3("Header4").get().column1 = "宇宙全ての";
		to.getAs3("Header4").get().column2 = "答えは？";
		to.getAs3("Header4").get().column3 = 42;

		to.getAs3("Header5").get().column1 = "二通りの自然数の３乗数の和";
		to.getAs3("Header5").get().column2 = "で表現できる最小数";
		to.getAs3("Header5").get().column3 = 1729;

		to.getAs4("Header6").get().column1 = 42;
		to.getAs4("Header6").get().column2 = 1729;
		to.getAs4("Header6").get().column3 = "もう一度書きます。";

		to.getAs1("Last").set("以上。");

		// ika
		to.setRow("ika");
		to.getAs2("Header1").set(314);
		to.getAs2("Header2").set(1592);
		to.getAs1("Header3").set("314と1592ついて");

		to.getAs3("Header4").get().column1 = "円周率";
		to.getAs3("Header4").get().column2 = "です。ただの。";
		to.getAs3("Header4").get().column3 = 314;

		to.getAs3("Header5").get().column1 = "円周率の";
		to.getAs3("Header5").get().column2 = "４～７桁目";
		to.getAs3("Header5").get().column3 = 1592;

		to.getAs4("Header6").get().column1 = 314;
		to.getAs4("Header6").get().column2 = 1592;
		to.getAs4("Header6").get().column3 = "もう一度書くんです。";

		to.getAs1("Last").set("終了。");

		// final
		to.setTailRow("final");
		to.getAs2("Header1").set(42 + 314);
		to.getAs2("Header2").set(1729 + 1592);
		to.getAs1("Header3").set("42と1729ついてと314と1592ついて");

		to.getAs3("Header4").get().column1 += "円周率";
		to.getAs3("Header4").get().column2 += "です。ただの。";
		to.getAs3("Header4").get().column3 += 314;

		to.getAs3("Header5").get().column1 += "円周率の";
		to.getAs3("Header5").get().column2 += "４～７桁目";
		to.getAs3("Header5").get().column3 += 1592;

		to.getAs4("Header6").get().column1 += 314;
		to.getAs4("Header6").get().column2 += 1592;
		to.getAs4("Header6").get().column3 += "もう一度書くんです。";

		to.getAs1("Last").set("That's all for today.");

		// 書換テスト
		to.row(0).getAs4("Header6").get().column3 += "おーほっほっほっほっ。";
		to.row("ika").getAs4("Header6").get().column3 += "ゲボハハハハ。";
		to.tail().getAs3("Header5").get().column2 += " www";
		to.tail("final").getAs4("Header6").get().column3 += " lol";

		// ------ tableObj4 と同じ End ------ //

		assertEqualsWithFile(to.toString(), getClass(),
				"removeColumnTest-before");

		to.removeColumnByField(ColumnSet1.class, "column2");
		assertEqualsWithFile(to.toString(), getClass(),
				"removeColumnTest-remove_1-2");

		to.removeColumnByField(ColumnSet2.class, "column1");
		assertEqualsWithFile(to.toString(), getClass(),
				"removeColumnTest-remove_1-2_and_2-1");
	}

	public static class ColumnSet1 {
		@ColumnDef(title = "カラム１。")
		public String column1;
		@ColumnDef(title = "カラム２。")
		public String column2;
		@ColumnDef(title = "カラム３。")
		public int column3;
	}

	public static class ColumnSet2 {
		public int column1;
		public int column2;
		public String column3;
	}

}
