package tetz42.cellom.header;

import static tetz42.cellom.CelloUtil.*;

import java.lang.reflect.Field;

import tetz42.cellom.Context;
import tetz42.cellom.ICell;
import tetz42.cellom.annotation.EachHeader;
import tetz42.cellom.annotation.Header;

public class HeaderCell implements ICell {

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
	private boolean isWindowFrozen = false;

	/**
	 * For Header Element
	 *
	 * @param field
	 * @param def
	 * @param depth
	 */
	HeaderCell(Context<?> context, Field field, int depth) {
		this.context = context;
		this.realDepth = depth;
		if (field == null) {
			this.name = ROOT;
			this.width = UNDEFINED;
		} else {
			Header def = field.getAnnotation(Header.class);
			this.name = field.getName();
			if (def == null) {
				depth = UNDEFINED;
				this.realDepth--;
				this.width = UNDEFINED;
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
	HeaderCell(Context<?> context, EachHeader def, String key, int depth) {
		this.context = context;
		this.name = key;
		this.realDepth = depth;
		if (def == null) {
			depth = UNDEFINED;
			this.width = UNDEFINED;
			this.realDepth--;
			this.title = this.name;
			this.style = ICell.HEADER_STYLE;
			this.isConverted = false;
			this.convertSchema = "";
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

	public void freezeWindow() {
		this.isWindowFrozen  = true;
	}

	@Override
	public boolean isWindowFrozen() {
		return isWindowFrozen;
	}
}
