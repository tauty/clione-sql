package tetz42.cello;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.cello.annotation.CellDef;
import tetz42.cello.annotation.EachCellDef;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;
import tetz42.cello.header.Header;

public class Context {

	private final Header<?> header;

	private final Map<String, Map<String, String>> convertMap = new HashMap<String, Map<String, String>>();
	public int headerDepth;
	public int[] displayHeaders;

	public Context(Header<?> header) {
		this.header = header;
	}

	public void init() {
		this.headerDepth = this.header.getDepth();
		int[] displayHeaders = new int[header.getDepth()];
		for (int i = 0; i < displayHeaders.length; i++)
			displayHeaders[i] = i + 1;
		this.displayHeaders = displayHeaders;
	}
	
	public void putConversion(String schema, String convertFrom, String convertTo){
		Map<String, String> map = convertMap.get(schema);
		if(map == null)
			convertMap.put(schema, map = new HashMap<String, String>());
		map.put(convertFrom, convertTo);
	}

	public String getConversion(String schema, String convertFrom){
		Map<String, String> map = convertMap.get(schema);
		if(map == null)
			return null;
		return map.get(convertFrom);
	}
	
	public boolean isTopLevel(int level) {
		if (this.displayHeaders.length == 0)
			return false;
		return this.displayHeaders[0] == level;
	}

	public List<Field> validFields(Class<?> clazz) {
		ArrayList<Field> list = new ArrayList<Field>();
		for (Field f : clazz.getDeclaredFields()) {
			if (isValid(f))
				list.add(f);
		}
		return list;
	}

	public int classWidth(Class<?> clazz) {
		int width = 0;
		List<Field> fields = validFields(clazz);
		for (Field f : fields) {
			int fwidth = fieldWidth(f);
			if (fwidth != UNDEFINED)
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
		return UNDEFINED;
	}

	public String fieldTitle(Field f) {
		HeaderDef def = f.getAnnotation(HeaderDef.class);
		if (def != null && isEmpty(def.title()))
			return def.title();
		return f.getName();
	}

	public boolean isValid(Field f) {
		return !isStatic(f) && !isHidden(f);
	}

	public boolean isRemoved(Class<?> clazz, Field f) {
		// TODO implementation
		return false;
		// return removed.isRemoved(clazz.getName(), f.getName());
	}

	public boolean isHidden(Field f) {
		return f.getAnnotation(HeaderDef.class) == null
				&& f.getAnnotation(CellDef.class) == null
				&& f.getAnnotation(EachHeaderDef.class) == null
				&& f.getAnnotation(EachCellDef.class) == null;
	}

}
