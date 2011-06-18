package tetz42.clione.lang;

import static tetz42.clione.SQLManager.*;
import static tetz42.test.Util.*;

import org.junit.Test;

import tetz42.clione.lang.func.ClioneFunction;

public class IfTest {

	@Test
	public void if_nega_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%if !PARAM");
		Instruction inst = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(inst, getClass(), "if_nega_true");
	}

	@Test
	public void if_nega_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%if !PARAM");
		Instruction inst = cf.perform(params());
		assertEqualsWithFile(inst, getClass(), "if_nega_false");
	}

	@Test
	public void if_nega_true_with_value() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if !PARAM 'octopus'");
		Instruction inst = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(inst, getClass(), "if_nega_true_with_value");
	}

	@Test
	public void if_nega_false_with_value() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if !PARAM 'octopus'");
		Instruction inst = cf.perform(params());
		assertEqualsWithFile(inst, getClass(), "if_nega_false_with_value");
	}

	@Test
	public void if_nega_doller_true() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%if $!PARAM");
		Instruction inst = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(inst, getClass(), "if_nega_doller_true");
	}

	@Test
	public void if_nega_doller_false() {
		ClioneFunction cf = ClioneFuncFactory.get().parse("%if $!PARAM");
		Instruction inst = cf.perform(params());
		assertEqualsWithFile(inst, getClass(), "if_nega_doller_false");
	}

	@Test
	public void if_nega_doller_true_with_value() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if $!PARAM 'octopus'");
		Instruction inst = cf.perform(paramsOn("PARAM"));
		assertEqualsWithFile(inst, getClass(), "if_nega_doller_true_with_value");
	}

	@Test
	public void if_nega_doller_false_with_value() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if $!PARAM 'octopus'");
		Instruction inst = cf.perform(params());
		assertEqualsWithFile(inst, getClass(),
				"if_nega_doller_false_with_value");
	}

	@Test
	public void if_and() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %and(param1, param2) 'octopus'");
		Instruction inst = cf.perform(paramsOn("param1", "param2"));
		assertEqualsWithFile(inst, getClass(), "if_and_true");
		inst = cf.perform(paramsOn("param2"));
		assertEqualsWithFile(inst, getClass(), "if_and_false");
		inst = cf.perform(paramsOn());
		assertEqualsWithFile(inst, getClass(), "if_and_false");
	}

	@Test
	public void if_or() {
		ClioneFunction cf = ClioneFuncFactory.get().parse(
				"%if %or(param1, param2) 'octopus'");
		Instruction inst = cf.perform(paramsOn("param1", "param2"));
		assertEqualsWithFile(inst, getClass(), "if_or_true");
		inst = cf.perform(paramsOn("param2"));
		assertEqualsWithFile(inst, getClass(), "if_or_true");
		inst = cf.perform(paramsOn());
		assertEqualsWithFile(inst, getClass(), "if_or_false");
	}

}
