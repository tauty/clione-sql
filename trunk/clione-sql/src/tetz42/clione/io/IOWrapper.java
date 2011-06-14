package tetz42.clione.io;

import java.io.Closeable;
import java.io.IOException;

import tetz42.clione.exception.IORuntimeException;
import tetz42.clione.exception.ResourceClosingException;
import tetz42.clione.exception.WrapException;

public abstract class IOWrapper<T> {

	private Closeable[] ioes;

	public IOWrapper(Closeable... ioes) {
		this.ioes = ioes;
	}

	public T kick() {
		RuntimeException re = null;
		Error err = null;
		try {
			return execute();
		} catch (RuntimeException e) {
			throw re = e;
		} catch (IOException e) {
			throw re = new IORuntimeException(e);
		} catch (Exception e) {
			throw re = new WrapException(e);
		} catch (Error e) {
			throw err = e;
		} finally {
			// Exception from run method
			Throwable t = re != null ? re : err;

			// close process
			ResourceClosingException rce = null;
			for (Closeable io : ioes) {
				try {
					io.close();
				} catch (Throwable e) {
					rce = rce != null ? rce : new ResourceClosingException(e);
				}
			}

			// execute did not fail and close process fail case
			if (t == null && rce != null)
				throw rce;

			// both fail case
			if (t != null && rce != null) {
				while (t.getCause() != null) {
					t = t.getCause();
				}
				t.initCause(rce);
			}

			// safe case or execute fail case
		}
	}

	protected abstract T execute() throws IOException;
}
