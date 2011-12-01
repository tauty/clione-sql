package tetz42.util;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import tetz42.clione.exception.UnsupportedTypeException;
import tetz42.util.exception.IORuntimeException;
import tetz42.util.exception.SQLRuntimeException;
import tetz42.util.exception.WrapException;

public abstract class Using<T> {

	private List<ResultSet> rsets;
	private List<Statement> stmts;
	private List<Connection> cons;
	private List<Closeable> ioes;

	public Using(Object... resources) {
		for (Object res : resources) {
			if (res instanceof ResultSet) {
				rsets = createIf(rsets);
				rsets.add((ResultSet) res);
			} else if (res instanceof Statement) {
				stmts = createIf(stmts);
				stmts.add((Statement) res);
			} else if (res instanceof Connection) {
				cons = createIf(cons);
				cons.add((Connection) res);
			} else if (res instanceof Closeable) {
				ioes = createIf(ioes);
				ioes.add((Closeable) res);
			} else {
				throw new UnsupportedTypeException("Using does not support "
						+ res.getClass().getName());
			}
		}
	}

	private <E> List<E> createIf(List<E> list) {
		if (list == null)
			list = new ArrayList<E>();
		return list;
	}

	public T invoke() {
		RuntimeException re = null;
		Error err = null;
		try {
			return execute();
		} catch (RuntimeException e) {
			throw re = e;
		} catch (SQLException e) {
			throw re = new SQLRuntimeException(e);
		} catch (IOException e) {
			throw re = new IORuntimeException(e);
		} catch (Exception e) {
			throw re = new WrapException(e);
		} catch (Error e) {
			throw err = e;
		} finally {
			// Exception from execute method
			Throwable t = re != null ? re : err;

			RuntimeException resourceClosingException = null;

			// SQL close process
			SQLRuntimeException sre = null;
			if (t != null) {
				for (Connection con : cons) {
					try {
						con.rollback();
					} catch (Throwable e) {
						sre = sre != null ? sre : new SQLRuntimeException(
								"Connection rollback Error.", e);
					}
				}
			}
			resourceClosingException = sre;
			sre = null;
			for (ResultSet rs : rsets) {
				try {
					rs.close();
				} catch (Throwable e) {
					sre = sre != null ? sre : new SQLRuntimeException(
							"ResultSet close Error.", e);
				}
			}
			resourceClosingException = coalsce(resourceClosingException, sre);
			sre = null;
			for (Statement stmt : stmts) {
				try {
					stmt.close();
				} catch (Throwable e) {
					sre = sre != null ? sre : new SQLRuntimeException(
							"Statement close Error.", e);
				}
			}
			resourceClosingException = coalsce(resourceClosingException, sre);
			sre = null;
			for (Connection con : cons) {
				try {
					con.close();
				} catch (Throwable e) {
					sre = sre != null ? sre : new SQLRuntimeException(
							"Connection close Error.", e);
				}
			}
			resourceClosingException = coalsce(resourceClosingException, sre);

			// IO close process
			IORuntimeException ire = null;
			for (Closeable io : ioes) {
				try {
					io.close();
				} catch (Throwable e) {
					ire = ire != null ? ire : new IORuntimeException(
							"IO close error.", e);
				}
			}
			resourceClosingException = coalsce(resourceClosingException, ire);

			if (resourceClosingException != null) {
				if (t == null) {
					// execute did not fail and close process fail case
					throw resourceClosingException;
				} else {
					// both fail case
					coalsce(t, resourceClosingException);
				}
			}
		}
	}

	private RuntimeException coalsce(RuntimeException src, RuntimeException dst) {
		if (src == null)
			return dst;
		if (dst == null)
			return src;
		coalsce((Throwable) src, dst);
		return src;
	}

	private void coalsce(Throwable src, RuntimeException dst) {
		Throwable tmp = src;
		while (tmp.getCause() != null) {
			tmp = tmp.getCause();
		}
		tmp.initCause(dst);
	}

	protected abstract T execute() throws Exception;

}
