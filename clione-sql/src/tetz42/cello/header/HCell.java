package tetz42.cello.header;

import static tetz42.cello.CelloUtil.*;

import java.lang.reflect.Field;

import tetz42.cello.Context;
import tetz42.cello.ICell;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;

public class HCell implements ICell {

	private final Context<?> context;
	private final String name;
	private final int depth;

	private String title;
	private int width;
	private String style = "";
	private boolean isConverted;
	private String convertSchema;

	private int size;
	private boolean isRemoved = false;
	private boolean isSkipped = false;
	private int x = 1;
	private int y = 1;
	private int realDepth;

	/**
	 * For Header Element
	 *
	 * @param field
	 * @param def
	 * @param depth
	 */
	HCell(Context<?> context, Field field, int depth) {
		this.context = context;
		this.realDepth = depth;
		if (field == null) {
			this.name = ROOT;
		} else {
			HeaderDef def = field.getAnnotation(HeaderDef.class);
			this.name = field.getName();
			if (def == null) {
				depth = UNDEFINED;
				realDepth--;
			} else {
				this.title = def.title();
				this.width = def.width();
				this.style = def.style();
				this.isConverted = def.convert();
				this.convertSchema = def.convertSchema();
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
	HCell(Context<?> context, EachHeaderDef def, String key, int depth) {
		this.context = context;
		this.name = key;
		this.realDepth = depth;
		if (def == null) {
			depth = UNDEFINED;
			realDepth--;
		} else {
			this.title = this.name;
			this.width = def.width();
			this.style = def.style();
			this.isConverted = def.convert();
			this.convertSchema = def.convertSchema();
		}
		this.depth = depth;
	}

	public String getName() {
		return name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
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

	@Override
	public String getStyle() {
		return style;
	}

	public void setDefaultCellStyle(String style) {
		// TODO implementation
		System.out.println(style);
	}

	public int getDepth() {
		return depth;
	}

	public void setConvert(boolean isConvert) {
		this.isConverted = isConvert;
	}

	public boolean isConverted() {
		return isConverted;
	}

	public void setConvertSchema(String convertSchema) {
		this.convertSchema = convertSchema;
	}

	public String getConvertSchema() {
		return convertSchema == null ? "" : convertSchema;
	}

	@Override
	public String getValue() {
		String title = this.getTitle();
		if (isEmpty(title))
			title = this.getName();
		if (this.isConverted()) {
			String s = this.context.getConversion(this.getConvertSchema(),
					title);
			if (s != null)
				title = s;
		}
		return title;
	}

	public void skip() {
		this.isSkipped = true;
	}

	@Override
	public boolean isSkipped() {
		return this.isSkipped;
	}

	void setX(int x) {
		this.x = x;
	}

	@Override
	public int getX() {
		return this.x;
	}

	void setY(int y) {
		this.y = y;
	}

	@Override
	public int getY() {
		return this.y;
	}

	void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public int getRealDepth() {
		return realDepth;
	}

}
