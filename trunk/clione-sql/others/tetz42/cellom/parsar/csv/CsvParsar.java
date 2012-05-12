package tetz42.cellom.parsar.csv;

import static tetz42.util.ReflectionUtil.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import tetz42.cellom.parsar.csv.annotation.CsvCell;
import tetz42.util.exception.IORuntimeException;
import tetz42.util.exception.UnknownFormatException;
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
		this.token = new IStreamToken(in, charsetName);
	}

	public <T> List<T> parseAll(final Class<T> clazz) {
		ArrayList<T> list = new ArrayList<T>();
		while (status != Status.DATA_END) {
			list.add(parseTask(clazz));
			if (status == Status.PARSING)
				skipTask();
		}
		return list;
	}

	public <T> List<Result<T>> parseToResultAll(final Class<T> clazz) {
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

	public <T> T parse(final Class<T> clazz) {
		return parseTask(clazz);
	}

	public <T> Result<T> parseToResult(final Class<T> clazz) {
		return new Result<T>(parse(clazz), recordNo, recordType(), validator
				.nextFailureList());
	}

	public CsvParsar skip() throws IOException {
		return skipTask();
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

	public void close() {
		token.close();
	}

	private CsvParsar skipTask() {
		do {
			token.nextCell();
		} while (status == Status.PARSING);
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

	private <T> T parseTask(Class<T> clazz) {
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
			if (isSingle(f.getType())) {
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

		IStreamToken(InputStream in, String charasetName) {
			this.isw = new IStreamWrapper(in, charasetName);
		}

		String nextCell() {
			status = Status.PARSING;
			if (isw.current() == '"') {
				isw.moveNext();
				boolean quoteEnded = false;
				while (isw.search((byte) '"') == '"'
						|| isw.status != StreamStatus.ENDED) {
					if (isw.moveNext() != '"') {
						quoteEnded = true;
						break;
					}
					isw.next(); // '""' -> '"'
				}
				if (!quoteEnded)
					throw new UnknownFormatException("double quote unmatch!");
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
				throw new UnknownFormatException("Unknown format!");
			}
			return isw.flush();
		}

		void close() {
			isw.quietClose();
		}
	}

	enum StreamStatus {
		READING, ENDED
	}

	private static class IStreamWrapper {

		private final ByteArrayOutputStream baos = new ByteArrayOutputStream(
				0xFF);
		private final BufferedInputStream in;
		private final String charsetName;
		StreamStatus status = StreamStatus.READING;

		byte current;

		IStreamWrapper(InputStream in, String charsetName) {
			this.in = new BufferedInputStream(in);
			this.charsetName = charsetName;
			read();
		}

		private byte read() {
			try {
				int i = in.read();
				if (i == -1) {
					status = StreamStatus.ENDED;
					current = 0;
					quietClose();
				} else {
					current = (byte) i;
				}
				return current;
			} catch (IOException e) {
				quietClose();
				throw new IORuntimeException(e);
			}
		}

		void quietClose() {
			try {
				in.close();
			} catch (IOException ignore) {
			}
		}

		byte current() {
			return status == StreamStatus.ENDED ? 0 : current;
		}

		byte next() {
			baos.write(current());
			return moveNext();
		}

		byte moveNext() {
			return read();
		}

		byte search(byte... bz) {
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

		String flush() {
			String res;
			try {
				res = new String(baos.toByteArray(), charsetName);
			} catch (IOException e) {
				quietClose();
				throw new IORuntimeException(e);
			}
			clear();
			return res;
		}

		void clear() {
			baos.reset();
		}

	}

}
