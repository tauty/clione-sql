package tetz42.util.tablequery.tables;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tetz42.util.tablequery.RemovedMap;
import tetz42.util.tablequery.RowHolder;
import tetz42.util.tablequery.annotation.ColumnDef;
import tetz42.util.tablequery.annotation.Hidden;
import tetz42.util.tablequery.tables.TableObject1.HeaderInfo;

public class TableQuery<T> {

	private final Class<T> clazz;
	private final ContextValues context;
	private final RowHolder<T> rowHolder;

	public TableQuery(Class<T> clazz) {
		this.clazz = clazz;
		this.context = new ContextValues();
		this.rowHolder = new RowHolder<T>(clazz, context);
	}

	public static class ContextValues {
		public int headerDepth = 0;
		public final RemovedMap removed = new RemovedMap();
		public final Map<String, String> aliasMap = new HashMap<String, String>();
		public final LinkedHashMap<String, HeaderInfo> headerClsMap = new LinkedHashMap<String, HeaderInfo>();
		public int[] displayHeaders = null;

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
			ColumnDef def = f.getAnnotation(ColumnDef.class);
			if (def != null)
				return def.width();
			return HeaderInfo.UNDEFINED;
		}

		public String fieldTitle(Field f) {
			ColumnDef def = f.getAnnotation(ColumnDef.class);
			if (def != null && def.title() != null)
				return def.title();
			return f.getName();
		}

		public boolean isRemoved(Class<?> clazz, Field f) {
			return removed.isRemoved(clazz.getName(), f.getName());
		}

		public boolean isHidden(Field f) {
			return f.getAnnotation(Hidden.class) != null;
		}
	}
}
