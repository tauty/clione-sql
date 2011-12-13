package tetz42.util.tablequery;

import static tetz42.util.tablequery.TOUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tetz42.util.tableobject.annotation.Hidden;
import tetz42.util.tablequery.annotation.CellDef;
import tetz42.util.tablequery.annotation.HeaderDef;
import tetz42.util.tablequery.tables.TableObject1.HeaderInfo;

public class Context<T> {

	private final Class<T> clazz;

	private final RecursiveMap<ClassDef> defMap = new RecursiveMap<ClassDef>();
	public final RemovedMap removed = new RemovedMap();
	public final Map<String, String> aliasMap = new HashMap<String, String>();
	public final LinkedHashMap<String, HeaderInfo> headerClsMap = new LinkedHashMap<String, HeaderInfo>();
	public int headerDepth = 0;
	public int[] displayHeaders = null;

	public Context(Class<T> clazz) {
		this.clazz = clazz;
		//		classDef(clazz, defMap);
	}

	public List<Field> validFields(Class<?> clazz) {
		ArrayList<Field> list = new ArrayList<Field>();
		for (Field f : clazz.getDeclaredFields()) {
			if (!isRemoved(clazz, f) && !isHidden(f))
				list.add(f);
		}
		return list;
	}

	public int classWidth(Class<?> clazz) {
		int width = 0;
		List<Field> fields = validFields(clazz);
		for (Field f : fields) {
			int fwidth = fieldWidth(f);
			if (fwidth != HeaderInfo.UNDEFINED)
				width += fwidth;
		}
		return width;
	}

	public int fieldWidth(Field f) {
		HeaderDef h = f.getAnnotation(HeaderDef.class);
		if (h != null)
			return h.width();
		CellDef c = f.getAnnotation(CellDef.class);
		if (c != null)
			return c.width();
		return HeaderInfo.UNDEFINED;
	}

	public String fieldTitle(Field f) {
		HeaderDef def = f.getAnnotation(HeaderDef.class);
		if (def != null && isEmpty(def.title()))
			return def.title();
		return f.getName();
	}

	public boolean isRemoved(Class<?> clazz, Field f) {
		return removed.isRemoved(clazz.getName(), f.getName());
	}

	public boolean isHidden(Field f) {
		return f.getAnnotation(HeaderDef.class) == null
				&& f.getAnnotation(CellDef.class) == null;
	}

	/**
	 * create Define map.
	 * 
	 * @param clazz
	 * @param defMap
	 * @return
	 */
	private int classDef(Class<?> clazz, RecursiveMap<ClassDef> defMap) {
		ClassDef def = new ClassDef();
		defMap.setValue(def);
		def.clazz = clazz;

		if (isPrimitive(clazz)) {
			return def.size = 1;
		}

		int result = 0;
		List<Field> fields = cellFields(clazz);
		for (Field f : fields) {
			defMap = defMap.get(f.getName());
			setAnnotationInfo(f, defMap);
			result += classDef(f.getType(), defMap);
		}

		return def.size = result;
	}

	private void setAnnotationInfo(Field f, RecursiveMap<ClassDef> defMap) {
		System.out.println(f);
		System.out.println(defMap);
	}

	private List<Field> cellFields(Class<?> clazz) {
		ArrayList<Field> list = new ArrayList<Field>();
		for (Field f : clazz.getDeclaredFields()) {
			if (!isCell(f))
				list.add(f);
		}
		return list;
	}

	private boolean isCell(Field f) {
		return f.getAnnotation(Hidden.class) != null;
	}

	public static class ClassDef {
		// The class
		public Class<?> clazz;
		// The size of the fields this class manages.
		public int size;
	}

}
