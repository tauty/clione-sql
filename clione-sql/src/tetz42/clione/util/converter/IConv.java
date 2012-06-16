package tetz42.clione.util.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IConv {
	Object get(ResultSet rs, int columnIndex) throws SQLException;
	void set(PreparedStatement stmt, Object param, int columnIndex) throws SQLException;
}
