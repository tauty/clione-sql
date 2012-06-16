package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortPrimitiveConv implements IConv {

	@Override
	public Object get(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getShort(columnIndex);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int columnIndex)
			throws SQLException {
		stmt.setShort(columnIndex, (Short) param);
	}
}
