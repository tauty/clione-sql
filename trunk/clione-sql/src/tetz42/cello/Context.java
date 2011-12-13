package tetz42.cello;

import static tetz42.cello.TOUtil.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

	private static final int UNDEFINED = -1;

	private final Header<?> header;

	public final Map<String, String> aliasMap = new HashMap<String, String>();
	public int headerDepth = 0;
	public int[] displayHeaders = null;

	public Context(Header<?> header) {
		this.header = header;
		this.headerDepth = this.header.getDepth();
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
