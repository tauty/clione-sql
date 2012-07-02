package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getObject(index) == null ? null : rs
				.getDouble(index);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		if (param != null)
			stmt.setDouble(index, (Double) param);
		else
			stmt.setObject(index, null);
	}
}
