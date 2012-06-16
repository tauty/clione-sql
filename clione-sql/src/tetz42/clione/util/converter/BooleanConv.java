package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BooleanConv implements IConv {

	@Override
	public Object get(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex) == null ? null : rs
				.getBoolean(columnIndex);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int columnIndex)
			throws SQLException {
		if (param != null)
			stmt.setBoolean(columnIndex, (Boolean) param);
		else
			stmt.setObject(columnIndex, null);
	}
}
