package tetz42.clione.util;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Auty.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import tetz42.cellom.parsar.queue.QueueParsar;
import tetz42.cellom.parsar.queue.entity.StrAndInt;
import tetz42.util.ObjDumper4j;


public class ParamMapTest {

	@Test
	public void hasChildObject() throws IOException, SQLException {

		StrAndInt si = QueueParsar.parse(StrAndInt.class, StrAndInt.TEST_STR);
		System.out.println(ObjDumper4j.dump(si));
		assertEqualsWithFile(params(si), getClass(), "hasChildObject");
	}


}
