package tetz42.clione.util;


import tetz42.cellom.parsar.queue.annotation.QueueCell;

public class StrAndInt {

	public static String TEST_STR = "123456789043212143211234567890123456789012345678901234567890";

	int intNull;

	String strNull;

	@QueueCell(order = 10, size = 10)
	String str10;

	@QueueCell(order = 11, size = 0)
	class4_2_4 cls424;

	@QueueCell(order = 12, size = 30)
	String str30;

	@QueueCell(order = 13, size = 10)
	int int10;

	static class class4_2_4 {
		@QueueCell(order = 1, size = 4)
		String str4;

		@QueueCell(order = 2, size = 2)
		int int2;

		@QueueCell(order = 3, size = 4)
		int int4;
	}

}
