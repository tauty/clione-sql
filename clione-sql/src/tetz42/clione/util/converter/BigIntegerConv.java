package tetz42.clione.util.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BigIntegerConv implements IConv {

	@Override
	public Object get(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex) == null ? null : rs.getBigDecimal(
				columnIndex).toBigInteger();
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int columnIndex)
			throws SQLException {
		if (param != null)
			stmt.setBigDecimal(columnIndex, new BigDecimal((BigInteger) param));
		else
			stmt.setObject(columnIndex, null);
	}
}
