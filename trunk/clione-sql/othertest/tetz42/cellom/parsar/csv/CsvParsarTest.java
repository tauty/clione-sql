package tetz42.cellom.parsar.csv;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.test.Auty.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import tetz42.cellom.parsar.csv.CsvParsar.Result;
import tetz42.cellom.parsar.csv.entity.InitialTest;
import tetz42.cellom.parsar.csv.entity.SameOrderTest;
import tetz42.cellom.parsar.csv.entity.ValidationTest;
import tetz42.cellom.parsar.csv.entity.ValidationTest2;
import tetz42.clione.parsar.HereDoc;

public class CsvParsarTest {

	private static final Map<String, String> map = HereDoc
			.get(CsvParsarTest.class.getResourceAsStream("CsvParsarTest.txt"));

	private InputStream toIn(String key) {
		return new ByteArrayInputStream(map.get(key).getBytes());
	}

	@Test
	public void initialTest() {
		InputStream in = toIn("InitialTest");
		CsvParsar parsar = new CsvParsar(in);
		InitialTest ini = parsar.parse(InitialTest.class);
		assertEqualsWithFile(ini, getClass(), "InitialTest");
	}

	@Test
	public void initialTest2() {
		InputStream in = toIn("InitialTest");
		CsvParsar parsar = new CsvParsar(in);
		InitialTest ini = parsar.parse(InitialTest.class);
		assertEqualsWithFile(ini, getClass(), "initialTest2_1");

		ini = parsar.parse(InitialTest.class);
		assertEqualsWithFile(ini, getClass(), "initialTest2_2");

		ini = parsar.parse(InitialTest.class);
		assertEqualsWithFile(ini, getClass(), "initialTest2_3");

		assertThat(parsar.getStatus(), is(CsvParsar.Status.DATA_END));
	}

	@Test
	public void initialTestAll() {
		InputStream in = toIn("InitialTest");
		CsvParsar parsar = new CsvParsar(in);
		List<InitialTest> list = parsar.parseAll(InitialTest.class);
		assertEqualsWithFile(list, getClass(), "initialTestAll");
	}

	@Test
	public void shortTest() {
		InputStream in = toIn("ShortTest");
		CsvParsar parsar = new CsvParsar(in);
		List<Result<InitialTest>> list = parsar
				.parseToResultAll(InitialTest.class);
		assertEqualsWithFile(list, getClass(), "shortTest");
	}

	@Test
	public void longTest() {
		InputStream in = toIn("LongTest");
		CsvParsar parsar = new CsvParsar(in);
		List<Result<InitialTest>> list = parsar
				.parseToResultAll(InitialTest.class);
		assertEqualsWithFile(list, getClass(), "longTest");
	}

	@Test
	public void quoteTest() {
		InputStream in = toIn("QuoteTest");
		CsvParsar parsar = new CsvParsar(in);
		List<Result<InitialTest>> list = parsar
				.parseToResultAll(InitialTest.class);
		assertEqualsWithFile(list, getClass(), "quoteTest");
	}

	@Test
	public void twiceTest() {
		InputStream in = toIn("TwiceTest");
		CsvParsar parsar = new CsvParsar(in);
		Result<InitialTest> result;

		// line 1
		result = parsar.parseToResult(InitialTest.class);
		assertEqualsWithFile(result, getClass(), "TwiceTest1_1");
		assertThat(parsar.getStatus(), is(CsvParsar.Status.PARSING));
		result = parsar.parseToResult(InitialTest.class);
		assertEqualsWithFile(result, getClass(), "TwiceTest1_2");
		assertThat(parsar.getStatus(), is(CsvParsar.Status.RECORD_END));

		// line 2
		result = parsar.parseToResult(InitialTest.class);
		assertEqualsWithFile(result, getClass(), "TwiceTest2_1");
		assertThat(parsar.getStatus(), is(CsvParsar.Status.PARSING));
		result = parsar.parseToResult(InitialTest.class);
		assertEqualsWithFile(result, getClass(), "TwiceTest2_2");
		assertThat(parsar.getStatus(), is(CsvParsar.Status.RECORD_END));

		// line 3
		result = parsar.parseToResult(InitialTest.class);
		assertEqualsWithFile(result, getClass(), "TwiceTest3_1");
		assertThat(parsar.getStatus(), is(CsvParsar.Status.PARSING));
		result = parsar.parseToResult(InitialTest.class);
		assertEqualsWithFile(result, getClass(), "TwiceTest3_2");
		assertThat(parsar.getStatus(), is(CsvParsar.Status.DATA_END));
	}

	@Test
	public void manyTest() {
		InputStream in = toIn("ManyTest");
		CsvParsar parsar = new CsvParsar(in);
		List<Result<InitialTest>> list = parsar
				.parseToResultAll(InitialTest.class);
		assertEqualsWithFile(list, getClass(), "manyTest");
	}

	@Test
	public void validation() {
		InputStream in = toIn("Validation");
		CsvParsar parsar = new CsvParsar(in);
		List<Result<ValidationTest>> list = parsar
				.parseToResultAll(ValidationTest.class);
		assertEqualsWithFile(list, getClass(), "validation");
	}

	@Test
	public void validation2() {
		InputStream in = toIn("Validation2");
		CsvParsar parsar = new CsvParsar(in);
		List<Result<ValidationTest2>> list = parsar
				.parseToResultAll(ValidationTest2.class);
		assertEqualsWithFile(list, getClass(), "validation2");
	}

	@Test
	public void sameOrder() {
		InputStream in = toIn("SameOrder");
		CsvParsar parsar = new CsvParsar(in);
		List<Result<SameOrderTest>> list = parsar
				.parseToResultAll(SameOrderTest.class);
		assertEqualsWithFile(list, getClass(), "sameOrder");
	}

	// Same Order Bean case is not required.
	// @Test
	// public void sameOrderBean() {
	// InputStream in = toIn("SameOrderBean");
	// CsvParsar parsar = new CsvParsar(in);
	// List<Result<SameOrderBeanTest>> list = parsar
	// .parseToResultAll(SameOrderBeanTest.class);
	// assertEqualsWithFile(list, getClass(), "sameOrderBean");
	// }

}
