package tetz42.cellom.parsar.csv;

import static tetz42.util.ReflectionUtil.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;

import tetz42.cellom.parsar.csv.annotation.CsvCell;
import tetz42.util.Using;
import tetz42.validation.FailureInfo;
import tetz42.validation.Validator;

public class CsvParsar {

	private final IStreamToken token;
	private final InputStream in;
	private final Validator validator = new Validator();

	public enum Status {
		PARSING, RECORD_END, DATA_END
	}

	private Status status = Status.PARSING;

	public enum RecordType {
		NORMAL, SHORT_RECORD, LONG_RECORD
	}

	public static class Result<T> {
		private Result(T data, int recordNo, RecordType type,
				List<FailureInfo> failureInfoList) {
			this.data = data;
			this.recordNo = recordNo;
			this.type = type;
			this.failureInfoList = failureInfoList;
		}

		public final T data;
		public final int recordNo;
		public final RecordType type;
		public final List<FailureInfo> failureInfoList;
	}

	private boolean isShortRecord;

	private int recordNo = 0;

	public CsvParsar(InputStream istream) {
		this(istream, "UTF-8");
	}

	public CsvParsar(InputStream istream, final String charsetName) {
		this.in = istream;
		this.token = new Using<IStreamToken>(in) {
			@Override
			protected IStreamToken execute() throws Exception {
				return new IStreamToken(in, charsetName);
			}
		}.invoke();
	}

	public <T> List<T> parseAll(final Class<T> clazz) {
		return new Using<List<T>>(in) {
			@Override
			protected List<T> execute() throws Exception {
				ArrayList<T> list = new ArrayList<T>();
				while (status != Status.DATA_END) {
					list.add(parseTask(clazz));
					if (status == Status.PARSING)
						skipTask();
				}
				return list;
			}
		}.invoke();
	}

	public <T> List<Result<T>> parseToResultAll(final Class<T> clazz) {
		return new Using<List<Result<T>>>(in) {
			@Override
			protected List<Result<T>> execute() throws Exception {
				ArrayList<Result<T>> list = new ArrayList<Result<T>>();
				while (status != Status.DATA_END) {
					T t = parseTask(clazz);
					RecordType type = recordType();
					if (status == Status.PARSING)
						skipTask();
					list.add(new Result<T>(t, recordNo, type, validator
							.nextFailureList()));
				}
				return list;
			}
		}.invoke();
	}

	public <T> T parse(final Class<T> clazz) {
		return new Using<T>(in) {
			@Override
			protected T execute() throws Exception {
				return parseTask(clazz);
			}
		}.invoke();
	}

	public <T> Result<T> parseToResult(final Class<T> clazz) {
		return new Result<T>(parse(clazz), recordNo, recordType(), validator
				.nextFailureList());
	}

	public CsvParsar skip() {
		return new Using<CsvParsar>(in) {
			@Override
			protected CsvParsar execute() throws Exception {
				return skipTask();
			}
		}.invoke();
	}

	public Status getStatus() {
		return status;
	}

	public boolean isShortRecord() {
		return isShortRecord;
	}

	public int getRecordNo() {
		return recordNo;
	}

	private CsvParsar skipTask() throws IOException {
		do {
			token.nextCell();
		} while (status != Status.RECORD_END && status != Status.DATA_END);
		return this;
	}

	private RecordType recordType() {
		if (status == Status.PARSING)
			return RecordType.LONG_RECORD;
		else if (isShortRecord())
			return RecordType.SHORT_RECORD;
		else
			return RecordType.NORMAL;
	}

	private static List<Field> filter(List<Field> fList) {
		ArrayList<Field> list = new ArrayList<Field>();
		for (Field f : fList) {
			if (getAnnotation(f, CsvCell.class) != null)
				list.add(f);
		}
		return list;
	}

	private <T> T parseTask(Class<T> clazz) throws IOException {
		isShortRecord = false;
		List<Field> fList = filter(avoidDuplication(getAllFields(clazz)));
		Collections.sort(fList, new Comparator<Field>() {
			@Override
			public int compare(Field f1, Field f2) {
				CsvCell c1 = getAnnotation(f1, CsvCell.class);
				CsvCell c2 = getAnnotation(f2, CsvCell.class);
				return c1.order() - c2.order();
			}
		});
		T res = newInstance(clazz);
		int preOrder = -1;
		String value = null;
		Iterator<Field> ite = fList.iterator();
		while (ite.hasNext()) {
			Field f = ite.next();
			CsvCell c = getAnnotation(f, CsvCell.class);
			if (isShortRecord) {
				if (c.order() == preOrder) {
					isShortRecord = false;
				} else {
					validator.isValueOK(f, null);
					continue;
				}
			}
			if (isPrimitive(f.getType())) {
				// TODO Same orderNo specification should be implemented for
				// 'else' case below too.
				if (c.order() != preOrder)
					value = token.nextCell();
				if (validator.isValueOK(f, value))
					setStringValue(res, f, value);
				preOrder = c.order();
			} else {
				setValue(res, f, parseTask(f.getType()));
			}
			if ((status == Status.RECORD_END || status == Status.DATA_END)
					&& ite.hasNext()) {
				isShortRecord = true;
			}
		}
		return res;
	}

	private class IStreamToken {

		private final IStreamWrapper isw;

		IStreamToken(InputStream in, String charasetName) throws IOException {
			this.isw = new IStreamWrapper(in, charasetName);
		}

		String nextCell() throws IOException {
			status = Status.PARSING;
			if (isw.current() == '"') {
				isw.moveNext();
				boolean quoteEnded = false;
				while (isw.search((byte) '"') == '"' || isw.status != StreamStatus.ENDED) {
					if (isw.moveNext() != '"') {
						quoteEnded = true;
						break;
					}
					isw.next(); // '""' -> '"'
				}
				if (!quoteEnded)
					throw new UnsupportedDataTypeException(
							"double quote unmatch!");
			} else {
				isw.search((byte) ',', (byte) '\r');
			}
			if (isw.current() == ',') {
				isw.moveNext();
			} else if (isw.current() == '\r') {
				// skip CRLF
				isw.moveNext();
				isw.moveNext();
				status = isw.status == StreamStatus.ENDED ? Status.DATA_END
						: Status.RECORD_END;
				recordNo++;
			} else if (isw.status == StreamStatus.ENDED) {
				status = Status.DATA_END;
				recordNo++;
			} else {
				throw new UnsupportedDataTypeException("Unknown format!");
			}
			return isw.flush();
		}
	}

	enum StreamStatus {
		READING, ENDED
	}

	private static class IStreamWrapper {

		private int pos = 0;
		private int length;
		private byte[] buf = new byte[0xFF];

		private final ByteArrayOutputStream baos = new ByteArrayOutputStream(
				0xFF);
		private final BufferedInputStream in;
		private final String charsetName;
		StreamStatus status = StreamStatus.READING;

		IStreamWrapper(InputStream in, String charsetName) throws IOException {
			this.in = new BufferedInputStream(in);
			this.charsetName = charsetName;
			read();
		}

//		byte tameshiNext() throws IOException {
//			int i = in.read();
//			if(i == -1)
//				status = StreamStatus.ENDED;
//		}

		byte current() {
			return status == StreamStatus.ENDED ? 0 : buf[pos];
		}

		byte next() throws IOException {
			baos.write(current());
			return moveNext();
		}

		byte moveNext() throws IOException {
			if ((++pos) < length)
				return buf[pos];
			read();
			return current();
		}

		byte search(byte... bz) throws IOException {
			byte res = 0;
			while (this.status != StreamStatus.ENDED) {
				res = current();
				for (byte b : bz) {
					if (res == b)
						return res;
				}
				next();
			}
			return res;
		}

		String flush() throws UnsupportedEncodingException {
			String res = new String(baos.toByteArray(), charsetName);
			clear();
			return res;
		}

		void clear() {
			baos.reset();
		}

		private void read() throws IOException {
			length = in.read(buf);
			if (length == -1)
				this.status = StreamStatus.ENDED;
			pos = 0;
		}

	}

}
