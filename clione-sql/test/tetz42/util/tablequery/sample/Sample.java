package tetz42.util.tablequery.sample;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import tetz42.util.tablequery.tables.TableQuery;

public class Sample {

	@Test
	public void test() throws Exception {

		TableQuery<All> tq = create(All.class);

	}

	private TableQuery<All> create(Class<All> class1) {
		// TODO Auto-generated method stub
		return null;
	}

	@AAACell(style="")
	public static class All {
		@Header(title="部署", style="", width=100, convert = false)
		StrCell A = new StrCell();

		@Header(title="", convert=true, style = "", width = 0 )
		@EntryHeader(title="keyword", conversionSchema="aaa")
		CellMap<T> B;

		Map<String, String> convMap = new HashMap<String, String>();
	}

	public static class T {
	}

}
