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
package tetz42.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dumps or inspects any objects and converts to readable format string. This
 * library consists of one file only.
 * <p>
 * 
 * [sample]<br>
 * &emsp;&emsp; import static tetz42.util.ObjDumper4j.*;<br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; :<br>
 * &emsp;&emsp;&emsp;&emsp; Logger.debug( dumper("The result object:\n",
 * theResult) );<br>
 * &emsp;&emsp;&emsp;&emsp; Logger.debug( inspecter("foo") );<br>
 * 
 * <p>
 * You can choose five algorithms against reference cycles.<br>
 * - superRapid<br>
 * &emsp;&emsp;&emsp;&emsp; // Very fast. Error will occur if the parameter is a
 * bean that has a reference to itself.<br>
 * &emsp;&emsp;&emsp;&emsp; Logger.debug( dumper(dto).superRapid() );<br>
 * <br>
 * - rapid<br>
 * &emsp;&emsp;&emsp;&emsp; // Fast. Depends on the parameter's 'hashCode' or
 * 'equals' method, it might not act properly.<br>
 * &emsp;&emsp;&emsp;&emsp; Logger.debug( dumper(dto).rapid() );<br>
 * <br>
 * - normal<br>
 * &emsp;&emsp;&emsp;&emsp; // Normal. Depends on the parameter's 'hashCode'
 * method, it might not act properly.<br>
 * &emsp;&emsp;&emsp;&emsp; Logger.debug( dumper(dto).normal() );<br>
 * <br>
 * - safe<br>
 * &emsp;&emsp;&emsp;&emsp; // Safe.<br>
 * &emsp;&emsp;&emsp;&emsp; Logger.debug( dumper(dto) );<br>
 * <br>
 * - superSafe<br>
 * &emsp;&emsp;&emsp;&emsp; // Very safe. Error won't occur even if the
 * parameter is a List, Set or Map that has a reference to itself.<br>
 * &emsp;&emsp;&emsp;&emsp; Logger.debug( dumper(list).superSafe() );<br>
 * <br>
 * 
 * @version 1.2.0
 * @author tetz
 */
public class ObjDumper4j {

	/**
	 * Defines the types considered as primitive.<br>
	 * Primitive types is not dumped.
	 * <p>
	 * You can change primitive types by modifying the code as follows.<br>
	 * - add type<br>
	 * map.put( HashMap.class.getName(), null );<br>
	 * - change the method performed<br>
	 * map.put( Integer.class.getName(), "floatValue" );<br>
	 * // note: By default, 'toString' method is performed.<br>
	 * 
	 * <p>
	 * Furthermore, you can also change primitive types by putting a property
	 * file. You must name the file 'ObjDumper4j.properties', and the contents
	 * must be as follows:<br/>
	 * java.util.HashMap=<br/>
	 * java.lang.Integer=floatValue<br/>
	 * 
	 */
	protected static final Map<String, String> primitiveMap;
	static {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Object.class.getName(), null);
		map.put(Class.class.getName(), null);
		map.put(Boolean.class.getName(), null);
		map.put(Character.class.getName(), null);
		map.put(Number.class.getName(), null);
		map.put(Byte.class.getName(), null);
		map.put(Short.class.getName(), null);
		map.put(Integer.class.getName(), null);
		map.put(Long.class.getName(), null);
		map.put(Float.class.getName(), null);
		map.put(Double.class.getName(), null);
		map.put(BigInteger.class.getName(), null);
		map.put(BigDecimal.class.getName(), null);
		map.put(AtomicInteger.class.getName(), null);
		map.put(AtomicLong.class.getName(), null);
		try {
			Properties prop = new Properties();
			prop.load(ObjDumper4j.class
					.getResourceAsStream("ObjDumper4j.properties"));
			for (Map.Entry<Object, Object> entry : prop.entrySet())
				map.put(String.valueOf(entry.getKey()),
						String.valueOf(entry.getValue()).trim());
		} catch (Throwable e) {
		}
		primitiveMap = Collections.unmodifiableMap(map);
	}

	private static final String CRLF = System.getProperty("line.separator");
	private static final String CRLFx2 = CRLF + CRLF;

	/**
	 * dump method.
	 * 
	 * @see ObjDumper4j#dumper
	 * @param objs
	 *            - objects to dump
	 * @return the string converted from objs.
	 */
	public static String dump(Object... objs) {
		return dumper(objs).delimiter(CRLFx2).toString();
	}

	/**
	 * Creates an object to dump the parameters.
	 * 
	 * <p>
	 * When 'toString' method of the object is called, the parameters will dump
	 * as follows:<br>
	 * - Array, List<br>
	 * &emsp;&emsp;String[]@13b8f864[<br>
	 * &emsp;&emsp;&emsp;&emsp;"foo"<br>
	 * &emsp;&emsp;&emsp;&emsp;"bar"<br>
	 * &emsp;&emsp;&emsp;&emsp;"baz"<br>
	 * &emsp;&emsp;]<br>
	 * <br>
	 * - Map<br>
	 * &emsp;&emsp;HashMap@54bb7759{<br>
	 * &emsp;&emsp;&emsp;&emsp;"key1": "value1"<br>
	 * &emsp;&emsp;&emsp;&emsp;"key2": "value2"<br>
	 * &emsp;&emsp;}<br>
	 * <br>
	 * - Bean<br>
	 * &emsp;&emsp; Bean@7cf1bb78{<br>
	 * &emsp;&emsp;&emsp;&emsp; intField = 10<br>
	 * &emsp;&emsp;&emsp;&emsp; floatField = 10.0<br>
	 * &emsp;&emsp;&emsp;&emsp; strField = "10"<br>
	 * &emsp;&emsp; }<br>
	 * <br>
	 * - mix<br>
	 * &emsp;&emsp; HashMap@54bb7759{<br>
	 * &emsp;&emsp;&emsp;&emsp; "key1": Bean@7cf1bb78{<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; intField = 10<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; floatField = 10.0<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; mapField = HashMap@7f2a3793{<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "key1": 100<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "key2": 10000<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; }<br>
	 * &emsp;&emsp;&emsp;&emsp; }<br>
	 * &emsp;&emsp;&emsp;&emsp; "key2": ArrayList@50d5db23[<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "foo"<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "bar"<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "baz"<br>
	 * &emsp;&emsp;&emsp;&emsp; ]<br>
	 * &emsp;&emsp; }<br>
	 * 
	 * <p>
	 * You can use this method as follows:<br>
	 * &emsp;&emsp;Logger.debug( dumper("Contents of DTO = ", dto) );<br>
	 * - result<br>
	 * &emsp;&emsp; [DEBUG] Contents of DTO = FooDTO@13b8f864{<br>
	 * &emsp;&emsp;&emsp;&emsp; id = 42<br>
	 * &emsp;&emsp;&emsp;&emsp; name = "Tetz"<br>
	 * &emsp;&emsp;&emsp;&emsp; sex = "male"<br>
	 * &emsp;&emsp;&emsp;&emsp; jobs = ArrayList@37bd2664[<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "IT engineer"<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "Farmer"<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "Husband"<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "Farther"<br>
	 * &emsp;&emsp;&emsp;&emsp; ]<br>
	 * &emsp;&emsp; }<br>
	 * 
	 * <p>
	 * The object returned does not do anything unless its 'toString' method is
	 * performed.<br>
	 * Therefore, if you use wise logging product like log4j, you need not care
	 * about performance problem when you write the code like below:<br>
	 * &emsp;&emsp; log.debug( dumper("Result:\n", theResult) );<br>
	 * instead of:<br>
	 * &emsp;&emsp; if( log.isDebugEnabled() ) {<br>
	 * &emsp;&emsp;&emsp;&emsp; log.debug( dumper("Result:\n", theResult) );<br>
	 * &emsp;&emsp; }<br>
	 * Because log4j does not perform 'toString' method unless the log is
	 * output.<br>
	 * 
	 * @param objs
	 *            - objects to dump
	 * @return the object to dump parameters
	 */
	public static ObjDumper4j dumper(Object... objs) {
		return new ObjDumper4j(objs);
	}

	/**
	 * inspect method.
	 * 
	 * @see ObjDumper4j#inspecter
	 * @param objs
	 *            - objects to inspect
	 * @return the string converted from objs.
	 */
	public static String inspect(Object... objs) {
		return inspecter(objs).delimiter(CRLFx2).toString();
	}

	/**
	 * Returns the object to inspect parameters.
	 * 
	 * <p>
	 * [sample]<br>
	 * &emsp;&emsp;System.out.println( inspecter("foo") );<br>
	 * - result<br>
	 * &emsp;&emsp; String@18cc6{<br>
	 * &emsp;&emsp;&emsp;&emsp; value = char[]@7a187814[<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; f<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; o<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; o<br>
	 * &emsp;&emsp;&emsp;&emsp; ]<br>
	 * &emsp;&emsp;&emsp;&emsp; offset = 0<br>
	 * &emsp;&emsp;&emsp;&emsp; count = 3<br>
	 * &emsp;&emsp;&emsp;&emsp; hash = 101574<br>
	 * &emsp;&emsp;&emsp;&emsp; serialVersionUID = -6849794470754667710<br>
	 * &emsp;&emsp;&emsp;&emsp; serialPersistentFields =
	 * ObjectStreamField[]@314c194d[ ]<br>
	 * &emsp;&emsp;&emsp;&emsp; CASE_INSENSITIVE_ORDER =
	 * CaseInsensitiveComparator@23394894{<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; serialVersionUID =
	 * 8575799808933029326<br>
	 * &emsp;&emsp;&emsp;&emsp; }<br>
	 * &emsp;&emsp; }<br>
	 * 
	 * @param objs
	 *            - objects to inspect
	 * @return the object to inspect parameters
	 */
	public static ObjDumper4j inspecter(Object... objs) {
		return new ObjDumper4j(objs) {
			@Override
			protected void dumpObj(Object obj) {
				sb.append(initIndent);
				dumpBean(obj, initIndent);
			}
		};
	}

	private final Object objs[];
	protected StringBuilder sb;
	private String delimiter;
	private Markable m;
	private String indent = "\t";
	protected String initIndent = "";
	private boolean isStaticShow = false;
	private boolean doSort = false;
	private boolean isPrimitiveFirst = false;
	private boolean isClassFlatten = false;

	/**
	 * Changes the algorithm to most safe type.
	 * <p>
	 * [sample]<br>
	 * &emsp;&emsp; Logger.debug( dumper(dto).superSafe() );<br>
	 * 
	 * @return a reference to this object
	 */
	public ObjDumper4j superSafe() {
		this.m = new SafeMarkable() {

			@Override
			protected String genId(Object obj) {
				return obj.getClass().getSimpleName();
			}
		};
		return this;
	}

	/**
	 * Changes the algorithm to safe type.
	 * <p>
	 * [sample]<br>
	 * &emsp;&emsp; Logger.debug( dumper(dto).safe() );<br>
	 * <br>
	 * By default, this type is selected.<br>
	 * 
	 * @return a reference to this object
	 */
	public ObjDumper4j safe() {
		this.m = new SafeMarkable();
		return this;
	}

	/**
	 * Changes the algorithm to normal type.
	 * <p>
	 * [sample]<br>
	 * &emsp;&emsp; Logger.debug( dumper(dto).normal() );<br>
	 * 
	 * @return a reference to this object
	 */
	public ObjDumper4j normal() {
		this.m = new Markable() {
			private final Set<Object> markedSet = new HashSet<Object>();

			@Override
			public boolean mark(Object obj) {
				return markedSet.add(new Wrapper(obj));
			}
		};
		return this;
	}

	/**
	 * Changes the algorithm to rapid type.
	 * <p>
	 * [sample]<br>
	 * &emsp;&emsp; Logger.debug( dumper(dto).rapid() );<br>
	 * 
	 * @return a reference to this object
	 */
	public ObjDumper4j rapid() {
		this.m = new Markable() {
			private final Set<Object> markedSet = new HashSet<Object>();

			@Override
			public boolean mark(Object obj) {
				return markedSet.add(obj);
			}
		};
		return this;
	}

	/**
	 * Changes the algorithm to most rapid type.
	 * <p>
	 * [sample]<br>
	 * &emsp;&emsp; Logger.debug( dumper(dto).superRapid() );<br>
	 * 
	 * @return a reference to this object
	 */
	public ObjDumper4j superRapid() {
		this.m = new Markable() {

			@Override
			public boolean mark(Object obj) {
				return true;
			}
		};
		return this;
	}

	/**
	 * Specifies delimiter.
	 * <p>
	 * [sample]<br>
	 * &emsp;&emsp; System.out.println( dumper(dto1,
	 * dto2).delimiter("\n#======#\n") );<br>
	 * -result<br>
	 * &emsp;&emsp; FooDTO@13b8f864{<br>
	 * &emsp;&emsp;&emsp;&emsp; id = 42<br>
	 * &emsp;&emsp;&emsp;&emsp; name = "Foo"<br>
	 * &emsp;&emsp;&emsp;&emsp; jobs = ArrayList@37bd2664[<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "IT engineer"<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "Farmer"<br>
	 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp; "Project Manager"<br>
	 * &emsp;&emsp;&emsp;&emsp; ]<br>
	 * &emsp;&emsp; }<br>
	 * &emsp;&emsp; #======#<br>
	 * &emsp;&emsp; BarDTO@13b8ff64{<br>
	 * &emsp;&emsp;&emsp;&emsp; id = 79<br>
	 * &emsp;&emsp;&emsp;&emsp; name = "Bar"<br>
	 * &emsp;&emsp; }<br>
	 * 
	 * @return a reference to this object
	 */
	public ObjDumper4j delimiter(String delimiter) {
		this.delimiter = delimiter;
		return this;
	}

	/**
	 * Show static fields of dumped object.<br>
	 * 
	 * @return a reference to this object
	 */
	public ObjDumper4j showStatic() {
		this.isStaticShow = true;
		return this;
	}

	/**
	 * Change indent literal of dumped string.<br>
	 * The default value is <TAB>.
	 * 
	 * @param indent
	 *            user specific indent.
	 * @return a reference to this object
	 */
	public ObjDumper4j indent(String indent) {
		this.indent = indent;
		return this;
	}

	/**
	 * Set initial indent of dumped string.<br>
	 * 
	 * @param indent
	 *            user specific indent.
	 * @return a reference to this object
	 */
	public ObjDumper4j initIndent(String initIndent) {
		this.initIndent = initIndent;
		return this;
	}

	public ObjDumper4j doSort() {
		this.doSort = true;
		return this;
	}

	public ObjDumper4j primitiveFirst() {
		this.isPrimitiveFirst = true;
		return this;
	}

	public ObjDumper4j classFlatten() {
		this.isClassFlatten = true;
		return this;
	}

	protected ObjDumper4j(Object... objs) {
		this.objs = objs;
	}

	/**
	 * Generates dumped string.
	 */
	@Override
	public String toString() {
		sb = new StringBuilder();
		if (m == null)
			this.safe();
		String result;
		try {
			for (int i = 0; i < objs.length; i++) {
				if (i != 0 && delimiter != null)
					sb.append(delimiter);
				dumpObj(objs[i]);
			}
		} catch (Throwable t) {
			dumpThrowable(t);
		} finally {
			result = sb.toString();
			sb = null;
			m = null;
		}
		return result;
	}

	protected void dumpObj(Object obj) {
		if (obj instanceof String)
			sb.append(this.initIndent + obj);
		else {
			sb.append(this.initIndent);
			dumpObj(obj, this.initIndent);
		}
	}

	protected void dumpObj(Object obj, String indent) {
		if (obj == null)
			sb.append("null");
		else if (obj instanceof CharSequence)
			sb.append("\"").append((CharSequence) obj).append("\"");
		else if (primitiveMap.containsKey(obj.getClass().getName()))
			dumpPrimitive(obj);
		else if (obj instanceof Date)
			dumpDate((Date) obj);
		else if (obj.getClass().isEnum())
			sb.append("" + obj);
		else if (obj.getClass().isArray())
			dumpAry(obj, indent);
		else if (Iterable.class.isInstance(obj))
			dumpIterable((Iterable<?>) obj, indent);
		else if (Map.class.isInstance(obj))
			dumpMap((Map<?, ?>) obj, indent);
		else if (obj instanceof Throwable)
			dumpThrowable((Throwable) obj);
		else
			dumpBean(obj, indent);
	}

	private SimpleDateFormat sdformat;

	private void dumpDate(Date date) {
		if (sdformat == null)
			sdformat = new SimpleDateFormat(
					"EEE, MMM. d, yyyy 'at' HH:mm:ss.SSS", Locale.US);
		sb.append(sdformat.format(date));
	}

	protected void dumpPrimitive(Object obj) {
		String methodName = primitiveMap.get(obj.getClass().getName());
		if (methodName != null && methodName.length() != 0) {
			try {
				Method m = obj.getClass().getDeclaredMethod(methodName,
						(Class<?>[]) null);
				if (readyForAccess(m, m.getModifiers())) {
					sb.append(m.invoke(obj, (Object[]) null));
					return;
				}
			} catch (Throwable t) {
				dumpThrowable(t);
			}
		}
		sb.append(obj);
	}

	protected void dumpAry(Object obj, String indent) {
		sb.append(m.genId(obj)).append("[");

		if (!m.mark(obj)) {
			sb.append("...]");
			return;
		}
		int length = Array.getLength(obj);
		if (length == 0) {
			sb.append(" ]");
			return;
		}
		String subIndent = indent + this.indent;
		for (int i = 0; i < length; i++) {
			sb.append(CRLF).append(subIndent);
			dumpObj(Array.get(obj, i), subIndent);
		}
		sb.append(CRLF).append(indent).append("]");
	}

	protected void dumpIterable(Iterable<?> col, String indent) {
		sb.append(m.genId(col)).append("[");
		if (!m.mark(col)) {
			sb.append("...]");
			return;
		}
		String subIndent = indent + this.indent;
		boolean isZero = true;
		for (Object e : col) {
			isZero = false;
			sb.append(CRLF).append(subIndent);
			dumpObj(e, subIndent);
		}
		(isZero ? sb.append(" ") : sb.append(CRLF).append(indent)).append("]");
	}

	protected void dumpMap(Map<?, ?> map, String indent) {
		sb.append(m.genId(map)).append("{");
		if (!m.mark(map)) {
			sb.append("...}");
			return;
		}
		if (map.size() == 0) {
			sb.append(" }");
			return;
		}
		String subIndent = indent + this.indent;
		if (doSort || isPrimitiveFirst)
			map = convToSortedMap(map);
		for (Map.Entry<?, ?> e : map.entrySet()) {
			sb.append(CRLF).append(subIndent);
			dumpObj(e.getKey(), subIndent);
			sb.append(": ");
			dumpObj(e.getValue(), subIndent);
		}
		sb.append(CRLF).append(indent).append("}");
	}

	@SuppressWarnings("unchecked")
	private SortedMap<?, ?> convToSortedMap(final Map<?, ?> map) {
		SortedMap<Object, Object> sortedMap = new TreeMap<Object, Object>(
				new Comparator<Object>() {

					@Override
					public int compare(Object src, Object dst) {
						if (isPrimitiveFirst) {
							int i = forPrimitiveFirst(src, dst);
							if (i != 0)
								return i;
						}
						return forDoSort(src, dst);
					}

					private int forPrimitiveFirst(Object src, Object dst) {
						int i = compareForPrimitiveFirst(src, dst);
						if (i == 0)
							i = compareForPrimitiveFirst(map.get(src),
									map.get(dst));
						return i;
					}

					@SuppressWarnings("rawtypes")
					private int forDoSort(Object src, Object dst) {
						if (src == null || dst == null) {
							if(src != null)
								return -1;
							if(dst != null)
								return 1;
							return 0;
						}
						if (src instanceof Number && dst instanceof Number)
							return ((Comparable) src).compareTo(dst);
						int res;
						if (0 != (res = src.getClass().getName()
								.compareTo(dst.getClass().getName())))
							return res;
						if (src instanceof Comparable
								&& src.getClass() == dst.getClass())
							return ((Comparable) src).compareTo(dst);
						return String.valueOf(src).compareTo(
								String.valueOf(dst));
					}
				});
		sortedMap.putAll(map);
		return sortedMap;
	}

	protected void dumpBean(Object obj, String indent) {
		sb.append(m.genId(obj)).append("{");
		if (!m.mark(obj)) {
			sb.append("...}");
			return;
		}
		try {
			int startTimeLength = sb.length();
			boolean isFieldAdded = false;
			String subIndent = indent + this.indent;
			FieldTokenizer tokenizer = new FieldTokenizer(obj.getClass());
			while (true) {
				for (Field f : tokenizer.getFields()) {
					sb.append(CRLF).append(subIndent).append(f.getName())
							.append(" = ");
					if (readyForAccess(f, f.getModifiers()))
						dumpObj(f.get(obj), subIndent);
					isFieldAdded = true;
				}
				String nextClassName = tokenizer.getClassName();
				if (nextClassName != null)
					sb.append(CRLF).append(subIndent).append("[")
							.append(nextClassName).append("]");
				else
					break;
			}

			if (isFieldAdded)
				sb.append(CRLF).append(indent).append("}");
			else
				sb.delete(startTimeLength, sb.length()).append(" }");
		} catch (Throwable t) {
			dumpThrowable(t);
		}
	}

	protected void dumpThrowable(Throwable obj) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		obj.printStackTrace(pw);
		pw.flush();
		sb.append(sw.toString());
	}

	private boolean readyForAccess(AccessibleObject ao, int mod) {
		if (Modifier.isPublic(mod) || ao.isAccessible())
			return true;
		try {
			ao.setAccessible(true);
			return true;
		} catch (SecurityException e) {
			dumpThrowable(e);
			return false;
		}
	}

	private int compareForPrimitiveFirst(Object obj1, Object obj2) {
		Class<?> clazz1, clazz2;
		if (obj1 == null)
			clazz1 = int.class;
		else
			clazz1 = obj1.getClass();
		if (obj2 == null)
			clazz2 = int.class;
		else
			clazz2 = obj2.getClass();
		return compareForPrimitiveFirst(clazz1, clazz2);
	}

	private int compareForPrimitiveFirst(Class<?> clazz1, Class<?> clazz2) {
		int i1 = isPrimitive(clazz1) ? 0 : 1;
		int i2 = isPrimitive(clazz2) ? 0 : 1;
		return i1 - i2;
	}

	private boolean isPrimitive(Class<?> clazz) {
		return clazz.isPrimitive() || primitiveMap.containsKey(clazz.getName())
				|| CharSequence.class.isAssignableFrom(clazz)
				|| Date.class.isAssignableFrom(clazz) || clazz.isEnum();
	}

	private abstract class Markable {
		protected abstract boolean mark(Object obj);

		protected String genId(Object obj) {
			return obj.getClass().getSimpleName() + "@"
					+ Integer.toHexString(obj.hashCode());
		}
	}

	private class SafeMarkable extends Markable {
		private Map<String, List<Object>> markedListMap = new HashMap<String, List<Object>>();

		private List<Object> getList(Object obj) {
			String key = obj.getClass().getName();
			List<Object> list = markedListMap.get(key);
			if (list == null)
				markedListMap.put(key, list = new ArrayList<Object>());
			return list;
		}

		@Override
		public boolean mark(Object obj) {
			List<Object> list = getList(obj);
			for (Object other : list) {
				if (obj == other)
					return false;
			}
			return list.add(obj);
		}
	}

	private class Wrapper {
		private int hash;
		private Object obj;

		Wrapper(Object obj) {
			this.obj = obj;
			this.hash = obj.hashCode();
		}

		@Override
		public boolean equals(Object another) {
			if (another.getClass() == Wrapper.class)
				return this.obj == ((Wrapper) another).obj;
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	private class FieldTokenizer {

		private final Class<?> initClazz;
		private Class<?> clazz;

		FieldTokenizer(Class<?> clazz) {
			this.initClazz = clazz;
			this.clazz = this.initClazz;
		}

		String getClassName() {
			if (clazz == null || clazz == Object.class)
				return null;
			return clazz.getName();
		}

		List<Field> getFields() {
			List<Field> list = getFields(new ArrayList<Field>());
			if (isPrimitiveFirst || doSort) {
				Collections.sort(list, new Comparator<Field>() {

					@Override
					public int compare(Field f1, Field f2) {
						if (isPrimitiveFirst) {
							int i = compareForPrimitiveFirst(f1.getType(),
									f2.getType());
							if (i != 0)
								return i;
						}
						if (doSort) {
							return f1.getName().compareTo(f2.getName());
						}
						return 0;
					}
				});
			}
			return list;
		}

		private List<Field> getFields(List<Field> list) {
			if (clazz == null || clazz == Object.class)
				return list;
			for (Field f : clazz.getDeclaredFields()) {
				if (!isStaticShow && Modifier.isStatic(f.getModifiers()))
					continue;
				list.add(f);
			}
			clazz = clazz.getSuperclass();
			return !isClassFlatten ? list : getFields(list);
		}
	}

}
