package tetz42.cello.header;

import static tetz42.cello.TOUtil.*;

import java.lang.reflect.Field;

import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;

public class HCell {

	private final String name;
	private final int depth;
	private String title;
	private int width;
	private boolean isRemoved = false;
	private String style = "";

	/**
	 * For ROOT Element
	 */
	public HCell(int depth) {
		this(null, depth);
	}

	/**
	 * For Not Header Element
	 *
	 * @param field
	 */
	public HCell(Field field) {
		this(field, UNDEFINED);
	}

	/**
	 * For Header Element
	 *
	 * @param field
	 * @param def
	 * @param depth
	 */
	public HCell(Field field, int depth) {
		if (field == null) {
			this.name = ROOT;
		} else {
			HeaderDef def = field.getAnnotation(HeaderDef.class);
			if (def == null) {
				this.name = field.getName();
				depth = UNDEFINED;
			} else {
				this.name = isEmpty(def.name()) ? field.getName() : def.name();
				this.title = def.title();
				this.width = def.width();
				this.style = def.style();
			}
		}
		this.depth = depth;
	}

	/**
	 * For CellUnitMap Template class.
	 *
	 * @param field
	 *            - the field of CellUnitMap
	 * @param key
	 *            - the key name of cell
	 * @param depth
	 */
	public HCell(EachHeaderDef def, String key, int depth) {
		this.name = key;
		if (def == null) {
			depth = UNDEFINED;
		} else {
			this.title = def.title();
			this.width = def.width();
			this.style = def.style();
		}
		this.depth = depth;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void remove() {
		this.isRemoved = true;
	}

	public boolean isRemoved() {
		return isRemoved;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

	public void setDefaultCellStyle(String style) {
		// TODO implementation
	}

	public int getDepth() {
		return depth;
	}
}
