package tetz42.util.tablequery;

import static tetz42.util.tablequery.TOUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import tetz42.util.tableobject.annotation.Hidden;

public class Context<T> {

	private final Class<T> clazz;

	private final RecursiveMap<ClassDef> defMap = new RecursiveMap<ClassDef>();

	public Context(Class<T> clazz) {
		this.clazz = clazz;
		classDef(clazz, defMap);
	}

	/**
	 * create Define map.
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
