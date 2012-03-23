package tetz42.cellom.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import tetz42.cellom.CelloUtil;
import tetz42.cellom.ICell;
import tetz42.cellom.IRow;
import tetz42.cellom.ITableManager;

public class PoiGenerator {

	private final Map<String, HSSFCellStyle> styleMap = new HashMap<String, HSSFCellStyle>();

	public static interface fontName {
		String MS_P_GOTHIC = "ＭＳ Ｐゴシック";
	}

	// private final Locale locale;

	private final HSSFWorkbook book;
	private HSSFSheet sheet;
	private final HSSFDataFormat format;

	private final HSSFCellStyle headerStyle;
	private final HSSFCellStyle defaultStyle;

	private int x;
	private int y;
	private int offsetX;
	private int offsetY;

	public PoiGenerator() {
		this(new HSSFWorkbook());
	}

	public PoiGenerator(HSSFWorkbook book) {
		this(book, "sheet");
	}

	public PoiGenerator(HSSFWorkbook book, String sheetName) {
		this.book = book;
		HSSFSheet sheet;
		this.sheet = (sheet = book.getSheet(sheetName)) != null ? sheet : book
				.createSheet(sheetName);
		format = book.createDataFormat();

		headerStyle = createHeaderStyle();
		defaultStyle = createBodyLeft();

		setupPrint();

		// file name
		HSSFHeader head = sheet.getHeader();
		head.setRight(HSSFHeader.file());

		// page no
		HSSFFooter footer = sheet.getFooter();
		footer.setCenter(HSSFFooter.page() + " / " + HSSFFooter.numPages());
	}

	public PoiGenerator sheet(String sheetName) {
		HSSFSheet sheet;
		this.sheet = (sheet = book.getSheet(sheetName)) != null ? sheet : book
				.createSheet(sheetName);
		return this;
	}

	private static final Pattern ptn = Pattern
			.compile("(BOLD_)?NOLINE_([TBLR]+)");

	public PoiGenerator generate(ITableManager tm) {
		for (Iterable<ICell> row : tm.header().each()) {
			for (ICell cell : row) {
				if (!cell.isSkipped()) {
					if (cell.getX() != 1 || cell.getY() != 1) {
						joinCell(cell.getX() - 1, cell.getY() - 1);
					}
					writeHeader(cell.getString());
					if (cell.getWidth() != ICell.UNDEFINED)
						setWidth(cell.getWidth() * 256);
				}
				next();
			}
			nextRow();
		}
		for (IRow row : tm.eachRow()) {
			for (ICell cell : row.each()) {
				HSSFCellStyle style = styleMap.get(cell.getStyle());
				if (style == null) {
					// style generate and cache
					if (cell.getStyle().indexOf("_RIGHT") != -1) {
						style = createBodyRight();
					} else {
						style = createBodyLeft();
					}
					Matcher m = ptn.matcher(cell.getStyle());
					if (m.matches()) {
						if (!CelloUtil.isEmpty(m.group(1)))
							style.setFont(boldFont(10));
						String tblf = m.group(2);
						if (tblf.contains("T"))
							style.setBorderTop(HSSFCellStyle.BORDER_NONE);
						if (tblf.contains("B"))
							style.setBorderBottom(HSSFCellStyle.BORDER_NONE);
						if (tblf.contains("L"))
							style.setBorderLeft(HSSFCellStyle.BORDER_NONE);
						if (tblf.contains("R"))
							style.setBorderRight(HSSFCellStyle.BORDER_NONE);
					}
					styleMap.put(cell.getStyle(), style);
				}
				if (cell.isWindowFrozen()) {
					createFreezePane();
				}
				write(cell.getValue(), style).next();
			}
			nextRow();
		}
		return this;
	}

	public void writeBook(OutputStream stream) throws IOException {
		book.write(stream);
	}

	public PoiGenerator setXY(int x, int y) {
		this.x = x;
		this.y = y;
		offsetX = offsetY = 0;
		return this;
	}

	public PoiGenerator setX(int x) {
		this.x = x;
		offsetX = 0;
		return this;
	}

	public PoiGenerator setY(int y) {
		this.y = y;
		offsetX = offsetY = 0;
		return this;
	}

	public PoiGenerator writeHeader(String value) {
		return write(value, headerStyle);
	}

	public PoiGenerator writeData(Object value) {
		return write(value, defaultStyle);
	}

	public PoiGenerator write(Object value, HSSFCellStyle style) {
		setCellValue(x + offsetX, y + offsetY, value, style);
		return this;
	}

	public void setCellValue(int x, int y, Object value, HSSFCellStyle style) {
		if (value instanceof String)
			HSSFCellUtil.createCell(HSSFCellUtil.getRow(y, sheet), x,
					(String) value, style);
		else {
			HSSFCell cell = HSSFCellUtil.createCell(HSSFCellUtil.getRow(y,
					sheet), x, null, style);
			cell.setCellValue((String) null);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			if (value instanceof Integer)
				cell.setCellValue((Integer) value);
			else if (value instanceof Double)
				cell.setCellValue((Double) value);
		}
	}

	public void setWidth(int width) {
		if (width != ICell.UNDEFINED) {
			this.sheet.setColumnWidth(this.x + this.offsetX, width);
		}
	}

	public void joinCellRight(int x) {
		joinCell(x, 0);
	}

	public void joinCellRight(Cell cell, int x) {
		joinCell(cell, x, 0);
	}

	public void joinCellDown(int y) {
		joinCell(0, y);
	}

	public void joinCellDown(Cell cell, int y) {
		joinCell(cell, 0, y);
	}

	public void joinCell(int x, int y) {
		joinCell(getCell(this.x + offsetX, this.y + offsetY), x, y);
	}

	public void joinCell(Cell cell, int x, int y) {
		for (int ix = 0; ix <= x; ix++) {
			for (int iy = 0; iy <= y; iy++) {
				if (ix == 0 && iy == 0)
					continue;
				Cell joinedCell = getCell(cell.getColumnIndex() + ix, cell
						.getRowIndex()
						+ iy);
				joinedCell.setCellStyle(headerStyle);
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(cell.getRowIndex(), cell
				.getRowIndex()
				+ y, cell.getColumnIndex(), cell.getColumnIndex() + x));
	}

	public void createFreezePane() {
		sheet.createFreezePane(this.x + offsetX, this.y + offsetY);
	}

	public PoiGenerator next() {
		offsetX++;
		return this;
	}

	public PoiGenerator back() {
		offsetX--;
		return this;
	}

	public PoiGenerator nextRow() {
		offsetX = 0;
		offsetY++;
		return this;
	}

	public Cell getCell(int x, int y) {
		Row row = sheet.getRow(y);
		if (row == null)
			row = sheet.createRow(y);
		Cell cell = row.getCell(x);
		if (cell == null)
			cell = row.createCell(x);
		return cell;
	}

	public short getFormat(String formatStr) {
		return format.getFormat(formatStr);
	}

	public int getLastRowNum() {
		return sheet.getLastRowNum();
	}

	public PoiGenerator setGridlineOff() {
		sheet.setDisplayGridlines(false);
		return this;
	}

	private Font boldFont(int point) {
		return creteFont(point, Font.BOLDWEIGHT_BOLD);
	}

	private Font normalFont(int point) {
		return creteFont(point, Font.BOLDWEIGHT_NORMAL);
	}

	private Font creteFont(int point, short fontType) {
		Font font = book.createFont();
		font.setFontName(fontName.MS_P_GOTHIC);
		font.setFontHeightInPoints((short) point);
		font.setBoldweight(fontType);
		return font;
	}

	private HSSFCellStyle createHeaderStyle() {
		HSSFCellStyle style = book.createCellStyle();
		style.setFont(boldFont(10));

		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFillForegroundColor(IndexedColors.GOLD.getIndex());

		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}

	private HSSFCellStyle createBodyLeft() {
		return createBodyStyle(CellStyle.ALIGN_LEFT);
	}

	private HSSFCellStyle createBodyRight() {
		return createBodyStyle(CellStyle.ALIGN_RIGHT);
	}

	private HSSFCellStyle createBodyStyle(short LorR) {
		HSSFCellStyle style = book.createCellStyle();
		style.setFont(normalFont(10));

		style.setAlignment(LorR);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setWrapText(true);

		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}

	private void setupPrint() {
		PrintSetup print = sheet.getPrintSetup();
		print.setLandscape(true);
		print.setPaperSize(PrintSetup.A4_PAPERSIZE);
		print.setScale((short) 80);
	}
}
