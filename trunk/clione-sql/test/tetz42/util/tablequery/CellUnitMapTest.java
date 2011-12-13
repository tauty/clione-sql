package tetz42.util.tablequery;

import org.junit.Test;


public class CellUnitMapTest {
	
	@Test
	public void test(){
		CellUnitMap<Aaa> map = new CellUnitMap<Aaa>(Aaa.class);
		
	}
	
	public static class Aaa{
		int i;
		String s;
	}

}
