package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ByteConv implements IConv {

	@Override
	public Object get(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex) == null ? null : rs
				.getByte(columnIndex);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int columnIndex)
			throws SQLException {
		if (param != null)
			stmt.setByte(columnIndex, (Byte) param);
		else
			stmt.setObject(columnIndex, null);
	}
}
