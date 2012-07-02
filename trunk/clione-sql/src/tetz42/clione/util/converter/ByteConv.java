package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ByteConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getObject(index) == null ? null : rs
				.getByte(index);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		if (param != null)
			stmt.setByte(index, (Byte) param);
		else
			stmt.setObject(index, null);
	}
}
