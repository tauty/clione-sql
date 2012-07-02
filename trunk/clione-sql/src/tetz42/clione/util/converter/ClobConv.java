package tetz42.clione.util.converter;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ClobConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getClob(index);
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		stmt.setClob(index, (Clob) param);
	}
}
