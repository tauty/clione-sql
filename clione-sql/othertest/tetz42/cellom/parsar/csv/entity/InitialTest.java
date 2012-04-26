package tetz42.cellom.parsar.csv.entity;

import tetz42.cellom.parsar.csv.annotation.CsvCell;

public class InitialTest {

	@CsvCell(order=10)
	String tako;
	@CsvCell(order=20)
	int takoLeg;

	@CsvCell(order=30)
	String ika;
	@CsvCell(order=40)
	int ikaLeg;

	@CsvCell(order=50)
	String namako;
	@CsvCell(order=60)
	int namakoLeg;

	@CsvCell(order=70)
	String test;
	@CsvCell(order=80)
	String aaa;
}
