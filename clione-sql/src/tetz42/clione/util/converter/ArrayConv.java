package tetz42.clione.util.converter;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ArrayConv implements IConv {

	@Override
	public Object get(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getArray(columnIndex);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int columnIndex)
			throws SQLException {
		stmt.setArray(columnIndex, (Array)param);
	}
}
