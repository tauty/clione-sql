/*
 * Copyright 2011 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tetz42.clione;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.test.Util.*;

import java.math.BigInteger;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tetz42.clione.util.ParamMap;

public class ParamMapTest {

	ParamMap paramMap;

	@Before
	public void setUp() {
		this.paramMap = new ParamMap();
	}

	@After
	public void tearDown() {
		this.paramMap = null;
	}

	@Test
	public void put_normal() {
		paramMap.put("tako", "octopus");
		assertThat((String) paramMap.get("tako"), is("octopus"));
	}

	@Test(expected = NullPointerException.class)
	public void put_null() {
		paramMap.put(null, "octopus");
	}

	@Test
	public void put_sharp() {
		paramMap.put("#tako", "octopus");
		assertThat((String) paramMap.get("#tako"), is("octopus"));
		assertThat((String) paramMap.get("tako"), is("octopus"));
	}

	@Test
	public void put_doller() {
		paramMap.put("$tako", "octopus");
		assertThat((String) paramMap.get("$tako"), is("octopus"));
		assertThat((String) paramMap.get("tako"), is("octopus"));
	}

	@Test
	public void putAll() {
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("$tako", "octopus");
		map.put("#ika", "squid");
		paramMap.putAll(map);
		assertThat((String) paramMap.get("$tako"), is("octopus"));
		assertThat((String) paramMap.get("tako"), is("octopus"));
		assertThat((String) paramMap.get("#ika"), is("squid"));
		assertThat((String) paramMap.get("ika"), is("squid"));
	}

	@Test
	public void $() {
		ParamMap map = paramMap.$("$tako", "octopus");
		assertThat(map, is(paramMap));
		assertThat((String) paramMap.get("$tako"), is("octopus"));
		assertThat((String) paramMap.get("tako"), is("octopus"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void put_aster() {
		paramMap.put("*tako", "octopus");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void put_exclamation() {
		paramMap.put("!tako", "octopus");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void put_plus() {
		paramMap.put("+tako", "octopus");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void get_key_unsupported() {
		paramMap.get(new Object());
	}
	
	@Test
	public void object() {
		assertNull(paramMap.object(null));
		HashMap<Object,Object> map = new HashMap<Object, Object>();
		map.put(BigInteger.valueOf(1000000000), "BigInteger");
		map.put(100, "int");
		map.put("String", "StringValue");
		assertEqualsWithFile(paramMap.object(map), getClass(), "object_map");
		assertEqualsWithFile(paramMap.object(new Bar()), getClass(), "object_bean");
		
	}
}

class Foo{
	String foo1 = "foo1!";
	String foo2 = "foo2!";
	String foo3 = "foo3!";
}

class Bar extends Foo{
	String bar1 = "bar1!";
	String bar2 = "bar2!";
	String bar3 = "bar3!";
	String foo1 = "foobar1!";
	String foo2 = "foobar2!";
	String foo3 = "foobar3!";
}
