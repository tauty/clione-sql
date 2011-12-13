package tetz42.util.tablequery;

import static tetz42.test.Auty.*;
import org.junit.Test;

public class RowTest {
	
	@Test
	public void test() throws Exception {
		Context<AClass> context = new Context<AClass>(AClass.class);
		QRow<AClass> row = new QRow<AClass>(AClass.class, context);
		
		row.get().intField = 10;
		row.get().strField = "tako";
		row.get().subField.intField = 100;
		row.get().subField.strField = "octopus";
		
		assertEqualsWithFile(row.get(), getClass(), "test");
	}

	public static class AClass {
		int intField;
		String strField;
		Child subField;
		CellUnitMap<Child> unitMapField = new CellUnitMap<Child>(Child.class);
	}

	public static class Child {
		int intField;
		String strField;
	}

}
