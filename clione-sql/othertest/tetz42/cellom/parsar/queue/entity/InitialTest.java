package tetz42.cellom.parsar.queue.entity;

import tetz42.cellom.parsar.queue.annotation.QueueCell;

public class InitialTest {

	public static final String TEST_STR = "122333444455555666666777777788888888999999999";

	@QueueCell(order = 1, size = 1, regexp = "[0-9]+")
	private String f1;

	@QueueCell(order = 2, size = 2, regexp = "[0-9]+")
	private String f2;

	@QueueCell(order = 3, size = 3, regexp = "[0-9]+")
	private String f3;

	@QueueCell(order = 4, size = 4, regexp = "[0-9]+")
	private String f4;

	@QueueCell(order = 5, size = 5, regexp = "[0-9]+")
	private String f5;

	@QueueCell(order = 6, size = 6, regexp = "[0-9]+")
	private String f6;

	@QueueCell(order = 7, size = 7, regexp = "[0-9]+")
	private String f7;

	@QueueCell(order = 8, size = 8, regexp = "[0-9]+")
	private String f8;

	@QueueCell(order = 9, size = 9, regexp = "[0-9]+")
	private String f9;

	public String getF1() {
		return f1;
	}

	public void setF1(String f1) {
		this.f1 = f1;
	}

	public String getF2() {
		return f2;
	}

	public void setF2(String f2) {
		this.f2 = f2;
	}

	public String getF3() {
		return f3;
	}

	public void setF3(String f3) {
		this.f3 = f3;
	}

	public String getF4() {
		return f4;
	}

	public void setF4(String f4) {
		this.f4 = f4;
	}

	public String getF5() {
		return f5;
	}

	public void setF5(String f5) {
		this.f5 = f5;
	}

	public String getF6() {
		return f6;
	}

	public void setF6(String f6) {
		this.f6 = f6;
	}

	public String getF7() {
		return f7;
	}

	public void setF7(String f7) {
		this.f7 = f7;
	}

	public String getF8() {
		return f8;
	}

	public void setF8(String f8) {
		this.f8 = f8;
	}

	public String getF9() {
		return f9;
	}

	public void setF9(String f9) {
		this.f9 = f9;
	}
}
