package tetz42.cellom.parsar.csv.entity;

import tetz42.cellom.parsar.csv.annotation.CsvCell;

public class SameOrderBeanTest {

	@CsvCell(order=10)
	SameOrderTest sm1;

	@CsvCell(order=10)
	SameOrderTest sm2;

	@CsvCell(order=30)
	SameOrderTest sm3;
}
