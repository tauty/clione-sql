package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongPrimitiveConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getLong(index);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		stmt.setLong(index, (Long) param);
	}
}
