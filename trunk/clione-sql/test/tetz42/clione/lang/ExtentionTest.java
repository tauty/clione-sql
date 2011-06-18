package tetz42.clione.lang;

import static org.junit.Assert.*;
import static tetz42.clione.SQLManager.*;
import static tetz42.test.Util.*;

import java.util.Arrays;

import org.junit.Test;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.loader.LoaderUtil;
import tetz42.clione.node.SQLNode;
import tetz42.util.ObjDumper4j;

public class ExtentionTest {

	@Test
	public void if_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%if PARAM");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "if_true");
	}

	@Test
	public void if_true_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%if PARAM :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "if_true_with_sql");
	}

	@Test
	public void ifnot_true_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%!if PARAM :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "ifnot_true_with_sql");
	}

	@Test
	public void if_params_alltrue_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(PARAM1 PARAM2) :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM1", "PARAM2"));
		assertEqualsWithFile(instruction, getClass(),
				"if_params_alltrue_with_sql");
	}

	@Test
	public void if_params_onetrue_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(PARAM1 PARAM2) :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM2"));
		assertEqualsWithFile(instruction, getClass(),
				"if_params_onetrue_with_sql");
	}

	@Test
	public void if_params_allfalse_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(PARAM1 PARAM2) :ASC");
		Instruction instruction = cf.perform(paramsOn());
		assertEqualsWithFile(instruction, getClass(),
				"if_params_allfalse_with_sql");
	}

	@Test
	public void if_params_onetrue() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(PARAM1 $PARAM2) ");
		Instruction instruction = cf.perform(paramsOn("PARAM2"));
		assertEqualsWithFile(instruction, getClass(), "if_params_onetrue");
	}

	@Test
	public void if_params_allfalse() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(PARAM1 $PARAM2) ");
		Instruction instruction = cf.perform(paramsOn());
		assertEqualsWithFile(instruction, getClass(), "if_params_allfalse");
	}

	@Test
	public void if_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%if PARAM");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "if_false");
	}

	@Test
	public void if_false_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%if PARAM :ASC");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "if_false_with_sql");
	}

	@Test
	public void ifnot_false_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%!if PARAM :ASC");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "ifnot_false_with_sql");
	}

	@Test
	public void if_no_param() {
		try {
			ClioneFuncFactory.get().parse("%if ");
			fail();
		} catch (ClioneFormatException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void ifln_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%IF PARAM");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "ifln_true");
	}

	@Test
	public void ifln_true_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%IF PARAM :ASC");
		Instruction instruction = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(instruction, getClass(), "ifln_true_with_sql");
	}

	@Test
	public void ifln_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%IF PARAM");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "ifln_false");
	}

	@Test
	public void ifln_false_with_sql() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%IF PARAM :ASC");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "ifln_false_with_sql");
	}

	@Test
	public void ifln_no_param() {
		try {
			ClioneFuncFactory.get().parse("%IF ");
			fail();
		} catch (ClioneFormatException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void l_param_exsists_forwardmatch() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%L PARAM '%'");
		Instruction instruction = cf.perform(params("PARAM", "tako"));
		assertEqualsWithFile(instruction, getClass(),
				"l_param_exsists_forwardmatch");
	}

	@Test
	public void l_param_exsists_partmatch() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%L '%' PARAM '%'");
		Instruction instruction = cf.perform(params("PARAM", "tako"));
		assertEqualsWithFile(instruction, getClass(),
				"l_param_exsists_partmatch");
	}

	@Test
	public void l_param_exsists_partmatch2() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%L '%' PARAM '%'");
		Instruction instruction = cf.perform(params("PARAM", "tako_100%!"));
		assertEqualsWithFile(instruction, getClass(),
				"l_param_exsists_partmatch2");
	}

	@Test
	public void l_params_exsists_partmatch() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%L '%' PARAM1 '_' PARAM2 '%'");
		Instruction instruction = cf.perform(params("PARAM1", "#100%_").$(
				"PARAM2", "#200%_"));
		assertEqualsWithFile(instruction, getClass(),
				"l_params_exsists_partmatch");
	}

	@Test
	public void l_in_params_exsists() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%L('%' PARAM1 '_' PARAM2 '%')");
		Instruction instruction = cf.perform(params("PARAM1", "#100%_").$(
				"PARAM2", "#200%_"));
		assertEqualsWithFile(instruction, getClass(), "l_in_params_exsists");
	}

	@Test
	public void l_in_params_exsists_next() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%L('%' PARAM1 '_' PARAM2 '%') PARAM3");
		Instruction instruction = cf.perform(params("PARAM1", "#100%_").$(
				"PARAM2", "#200%_").$("PARAM3", 1000));
		assertEqualsWithFile(instruction, getClass(),
				"l_in_params_exsists_next");
	}

	@Test
	public void delnull_list() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%COMPACT PARAM");
		Instruction instruction = cf.perform(params("PARAM",
				Arrays.asList("tako", null, "ika", null, "namako")));
		assertEqualsWithFile(instruction, getClass(), "delnull_list");
	}

	@Test
	public void delnull_params() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%COMPACT PARAM1 PARAM2 PARAM3");
		Instruction inst = cf.perform(params("PARAM1",
				Arrays.asList("tako", null, "ika", null, "namako")).$("PARAM3",
				"umiushi"));
		assertEqualsWithFile(inst, getClass(), "delnull_params");
		assertEqualsWithFile(inst.merge(), getClass(), "delnull_params_merged");
	}

	// @Test
	// public void tosql() {
	// ClioneFunction cf = ClioneFuncFactory.get().parse(
	// "%TO_SQL(',' $PARAM)");
	// Instruction instruction = cf.perform(params("PARAM", "ASC"));
	// assertEqualsWithFile(instruction, getClass(), "tosql");
	// }
	//
	// @Test
	// public void tosql_null() {
	// ClioneFunction cf = ClioneFuncFactory.get().parse(
	// "%TO_SQL(',' $PARAM)");
	// Instruction instruction = cf.perform(params());
	// assertEqualsWithFile(instruction, getClass(), "tosql_null");
	// }

	@Test
	public void tostr() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%SQL(',' $PARAM)");
		Instruction instruction = cf.perform(params("PARAM", "ASC"));
		assertEqualsWithFile(instruction, getClass(), "tostr");
	}

	@Test
	public void tostr_null() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%SQL(',' $PARAM)");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "tostr_null");
	}

	@Test
	public void if_elseif_else_kakko_if() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(param1) 'tako' %elseif(param2) 'ika' %else 'namako'");
		Instruction instruction = cf.perform(paramsOn("param1", "param2"));
		assertEqualsWithFile(instruction, getClass(), "if_elseif_else_kakko_if");
	}

	@Test
	public void if_elseif_else_kakko_elseif() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(param1) 'tako' %elseif(param2) 'ika' %else 'namako'");
		Instruction instruction = cf.perform(paramsOn("param2"));
		assertEqualsWithFile(instruction, getClass(),
				"if_elseif_else_kakko_elseif");
	}

	@Test
	public void if_elseif_else_kakko_else() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(param1) 'tako' %elseif(param2) 'ika' %else 'namako'");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(),
				"if_elseif_else_kakko_else");
	}

	@Test
	public void if_elseif_kakko_valueInBack() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if(param1) 'tako' %elseif(param2) 'ika'");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(),
				"if_elseif_kakko_valueInBack");
	}

	@Test
	public void if_elseif_else_if() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if param1 'tako' %elseif param2 'ika' %else 'namako'");
		Instruction instruction = cf.perform(paramsOn("param1", "param2"));
		assertEqualsWithFile(instruction, getClass(), "if_elseif_else_if");
	}

	@Test
	public void if_elseif_else_elseif() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if param1 'tako' %elseif param2 'ika' %else 'namako'");
		Instruction instruction = cf.perform(paramsOn("param2"));
		assertEqualsWithFile(instruction, getClass(), "if_elseif_else_elseif");
	}

	@Test
	public void if_elseif_else_else() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if param1 'tako' %elseif param2 'ika' %else 'namako'");
		Instruction instruction = cf.perform(paramsOn());
		assertEqualsWithFile(instruction, getClass(), "if_elseif_else_else");
	}

	@Test
	public void if_elseif_valueInBack() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if param1 'tako' %elseif param2 'ika'");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "if_elseif_valueInBack");
	}

	@Test
	public void include() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%INCLUDE 'tetz42/clione/sql/SQLManagerTest/Select.sql'");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "include");
	}

	@Test
	public void include_rpath() {
		SQLNode sqlNode = LoaderUtil.getNodeByClass(getClass(),
				"IncludeRpath.sql");
		String sql = new SQLGenerator().genSql(params(), sqlNode);
		assertEqualsWithFile(sql, getClass(), "include_rpath");
	}

	@Test
	public void include_put() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%INCLUDE 'tetz42/clione/sql/SQLManagerTest/Select.sql'"
						+ " %PUT 'age', '120', 'name_part', 'TA'");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "include_put");
	}

	@Test
	public void include_on() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%INCLUDE 'tetz42/clione/sql/SQLManagerTest/Select.sql'"
						+ " %ON 'age', '120', 'name_part', 'TA'");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "include_on");
	}

	@Test
	public void include_kakko() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%INCLUDE('tetz42/clione/sql/SQLManagerTest/Select.sql')");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "include_kakko");
	}

	@Test
	public void include_kakko_put() {
		// '), ' bug was occurred and fail this test case. TODO fix it.
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%INCLUDE( 'tetz42/clione/sql/SQLManagerTest/Select.sql',"
						+ " %PUT('age', '120'), %PUT('name_part', 'TA') )");
		System.out.println(ObjDumper4j.dumper(cf));
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "include_kakko_put");
	}

	@Test
	public void include_kakko_on() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%INCLUDE( 'tetz42/clione/sql/SQLManagerTest/Select.sql'"
						+ " %ON('age', '120', 'name_part', 'TA') )");
		Instruction instruction = cf.perform(params());
		assertEqualsWithFile(instruction, getClass(), "include_kakko_on");
	}

}
