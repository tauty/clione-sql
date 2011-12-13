package tetz42.cello.header;

import static tetz42.cello.TOUtil.*;

import java.lang.reflect.Field;

import tetz42.cello.Context;
import tetz42.cello.ICell;
import tetz42.cello.annotation.EachHeaderDef;
import tetz42.cello.annotation.HeaderDef;

public class HCell implements ICell {

	private final int depth;
	
	private final String name;
	private String title;
	private int width;
	private String style = "";
	private boolean isConvert;
	private String converSchema;

	private boolean isRemoved = false;

	private final Context context;
	
	/**
	 * For ROOT Element
	 */
	public HCell(Context context, int depth) {
		this(context, null, depth);
	}

	/**
	 * For Not Header Element
	 * 
	 * @param field
	 */
	public HCell(Context context, Field field) {
		this(context, field, UNDEFINED);
	}

	/**
	 * For Header Element
	 * 
	 * @param field
	 * @param def
	 * @param depth
	 */
	public HCell(Context context, Field field, int depth) {
		this.context = context;
		if (field == null) {
			this.name = ROOT;
		} else {
			HeaderDef def = field.getAnnotation(HeaderDef.class);
			this.name = field.getName();
			if (def == null) {
				depth = UNDEFINED;
			} else {
				this.title = def.title();
				this.width = def.width();
				this.style = def.style();
				this.isConvert = def.convert();
				this.converSchema = def.convertSchema();
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
	public HCell(Context context, EachHeaderDef def, String key, int depth) {
		this.context = context;
		this.name = key;
		if (def == null) {
			depth = UNDEFINED;
		} else {
			this.title = def.title();
			this.width = def.width();
			this.style = def.style();
			this.isConvert = def.convert();
			this.converSchema = def.convertSchema();
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

	public void setConvert(boolean isConvert) {
		this.isConvert = isConvert;
	}

	public boolean isConvert() {
		return isConvert;
	}

	public void setConverSchema(String converSchema) {
		this.converSchema = converSchema;
	}

	public String getConverSchema() {
		return converSchema;
	}
	
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSkip() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

}
