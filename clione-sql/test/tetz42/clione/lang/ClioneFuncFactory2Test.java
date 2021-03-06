package tetz42.clione.lang;

import static org.junit.Assert.*;
import static tetz42.test.Auty.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.func.ClioneFunction;

public class ClioneFuncFactory2Test {
	
	@Before
	public void setUp() {
		ContextUtil.pushResouceInfo("FromTest");
	}
	
	@After
	public void tearDown() {
		ContextUtil.popResourceInfo();
	}

	@Test
	public void doller_params() {
		ClioneFunction clione = ClioneFuncFactory.get().parse(
				"$(TAKO, IKA, NAMAKO)");
		assertEqualsWithFile(clione, getClass(), "doller_params");
	}

	@Test
	public void doller_param_and_params() {
		try {
			ClioneFuncFactory.get().parse("$TAKO(TAKO, IKA, NAMAKO)");
			fail();
		} catch (ClioneFormatException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void doller_no_param() {
		try {
			ClioneFuncFactory.get().parse("$ TAKO IKA NAMAKO");
			fail();
		} catch (ClioneFormatException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void doller_wrong_param() {
		try {
			ClioneFuncFactory.get().parse("$:TAKO");
			fail();
		} catch (ClioneFormatException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void if_params_literal() {
		ClioneFunction clione = ClioneFuncFactory.get().parse(
				"%if(TAKO IKA NAMAKO) :SAKANA");
		assertEqualsWithFile(clione, getClass(), "if_params_literal");
	}
}
