package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class TimeConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getTime(index);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		stmt.setTime(index, (Time) param);
	}
}
