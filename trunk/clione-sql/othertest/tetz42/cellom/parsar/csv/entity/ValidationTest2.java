package tetz42.cellom.parsar.csv.entity;

import static tetz42.validation.Format.*;
import tetz42.cellom.parsar.csv.annotation.CsvCell;
import tetz42.validation.annotation.Valid;

public class ValidationTest2 {

	@CsvCell(order = 10)
	@Valid(name = "Octopus", isIn = { "tako", "octopus", "TAKO" })
	String tako;

	@CsvCell(order = 20)
	@Valid(name = "Octopus Leg", atLeastOne = "leg", format = NUMERIC, length = 1)
	int takoLeg;

	@CsvCell(order = 30)
	@Valid(name = "Squid", isIn = { "ika", "squid", "IKA" })
	String ika;

	@CsvCell(order = 40)
	@Valid(name = "Squid Leg", atLeastOne = "leg", format = NUMERIC, length = 2)
	int ikaLeg;

	@CsvCell(order = 50)
	@Valid(name = "Sea Cucumber", isIn = { "namako", "sea cucumber", "NAMAKO" })
	String namako;

	@Valid(name = "Sea Cucumber Leg", atLeastOne = "leg", format = NUMERIC, length = 1)
	@CsvCell(order = 60)
	int namakoLeg;

	@CsvCell(order = 70)
	@Valid(name = "Test", maxLength = 20)
	String test;

	@CsvCell(order = 80)
	@Valid(name = "AAA", minLength = 3)
	String aaa;
}
