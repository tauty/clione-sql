package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DateConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getObject(index) == null ? null : new Date(rs
				.getTimestamp(index).getTime());
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		if (param != null)
			stmt.setTimestamp(index, new Timestamp(((Date)param).getTime()));
		else
			stmt.setObject(index, null);
	}
}
