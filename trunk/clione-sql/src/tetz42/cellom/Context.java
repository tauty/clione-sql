package tetz42.cellom;

import static tetz42.cellom.CelloUtil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.cellom.annotation.Body;
import tetz42.cellom.annotation.EachBody;
import tetz42.cellom.annotation.EachHeader;
import tetz42.cellom.annotation.Header;
import tetz42.cellom.body.Row;
import tetz42.cellom.header.HeaderManager;

public class Context<T> {

	private static final ICell emptyCell = new EmptyCell();

	private final HeaderManager<T> header;
	private final Row<T> rowDef;
	private final Map<String, Map<String, String>> convertMap = new HashMap<String, Map<String, String>>();

	public int depth;
	public int[] displayHeaders;

	public Context(Class<T> clazz, HeaderManager<T> header) {
		this.header = header;
		this.rowDef = new Row<T>(clazz, this);
	}

	public void init() {
		int[] displayHeaders = new int[header.getDepth()];
		for (int i = 0; i < displayHeaders.length; i++)
			displayHeaders[i] = i + 1;
		setDisplayHeaders(displayHeaders);
	}

	public HeaderManager<?> getHeader() {
		return this.header;
	}

	public Row<T> getRowDef() {
		return this.rowDef;
	}

	public void setDisplayHeaders(int... indexes) {
		this.displayHeaders = indexes;
		this.depth = indexes.length;
	}

	public void putConversion(String schema, String convertFrom,
			String convertTo) {
		Map<String, String> map = convertMap.get(schema);
		if (map == null)
			convertMap.put(schema, map = new HashMap<String, String>());
		map.put(convertFrom, convertTo);
	}

	public String getConversion(String schema, String convertFrom) {
		Map<String, String> map = convertMap.get(schema);
		if (map == null)
			return null;
		return map.get(convertFrom);
	}

	public boolean isTop(int level) {
		if (this.displayHeaders.length == 0)
			return false;
		return this.displayHeaders[0] == level;
	}

	public List<Field> validFields(Class<?> clazz) {
		ArrayList<Field> list = new ArrayList<Field>();
		for (Field f : getFields(clazz)) {
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
		Header h = f.getAnnotation(Header.class);
		if (h != null)
			return h.width();
		return UNDEFINED;
	}

	public String fieldTitle(Field f) {
		Header def = f.getAnnotation(Header.class);
		if (def != null && isEmpty(def.title()))
			return def.title();
		return f.getName();
	}

	public boolean isValid(Field f) {
		return !isStatic(f);
	}

	public boolean isHidden(Field f) {
		return f.getAnnotation(Header.class) == null
				&& f.getAnnotation(Body.class) == null
				&& f.getAnnotation(EachHeader.class) == null
				&& f.getAnnotation(EachBody.class) == null;
	}

	public ICell getEmptyCell() {
		return emptyCell;
	}

	private static class EmptyCell implements ICell {

		@Override
		public boolean isSkipped() {
			return true;
		}

		@Override
		public String getStyle() {
			return null;
		}

		@Override
		public String getValue() {
			return null;
		}

		@Override
		public int getX() {
			return 0;
		}

		@Override
		public int getY() {
			return 0;
		}

		@Override
		public int getWidth() {
			return 0;
		}

		@Override
		public boolean isWindowFrozen() {
			return false;
		}
	}

}
