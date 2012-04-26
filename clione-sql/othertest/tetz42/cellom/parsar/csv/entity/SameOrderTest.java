package tetz42.cellom.parsar.csv.entity;

import tetz42.cellom.parsar.csv.annotation.CsvCell;

public class SameOrderTest {

	@CsvCell(order=10)
	String tako;
	@CsvCell(order=10)
	String tako2;
	@CsvCell(order=20)
	int takoLeg;
	@CsvCell(order=20)
	int takoLeg2;

	@CsvCell(order=30)
	String ika;
	@CsvCell(order=30)
	String ika2;
	@CsvCell(order=40)
	int ikaLeg;
	@CsvCell(order=40)
	int ikaLeg2;

	@CsvCell(order=50)
	String namako;
	@CsvCell(order=50)
	String namako2;
	@CsvCell(order=60)
	int namakoLeg;
	@CsvCell(order=60)
	int namakoLeg2;

	@CsvCell(order=70)
	String test;
	@CsvCell(order=70)
	String test2;
	@CsvCell(order=80)
	String aaa;
	@CsvCell(order=80)
	String aaa2;
}
