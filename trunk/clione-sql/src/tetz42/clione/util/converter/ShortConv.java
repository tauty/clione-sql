package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getObject(index) == null ? null : rs
				.getShort(index);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		if (param != null)
			stmt.setShort(index, (Short) param);
		else
			stmt.setObject(index, null);
	}
}
