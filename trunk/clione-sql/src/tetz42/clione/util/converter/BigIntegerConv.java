package tetz42.clione.util.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BigIntegerConv implements IConv {

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getObject(index) == null ? null : rs.getBigDecimal(
				index).toBigInteger();
	}

	@Override
	public void set(PreparedStatement stmt, Object param, int index)
			throws SQLException {
		if (param != null)
			stmt.setBigDecimal(index, new BigDecimal((BigInteger) param));
		else
			stmt.setObject(index, null);
	}
}
