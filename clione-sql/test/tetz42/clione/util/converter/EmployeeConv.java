package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return new Employee().fromString(rs.getString(index));
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		stmt.setString(index, param.toString());
	}

}
