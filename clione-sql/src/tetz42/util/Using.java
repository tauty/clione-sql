/*
 * Copyright 2012 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import tetz42.util.exception.ResourceClosingException;
import tetz42.util.exception.SQLRuntimeException;
import tetz42.util.exception.WrapException;

public abstract class Using<T> {

	private List<Closeable> ioList = new ArrayList<Closeable>();
	private List<Closeable> rsList = new ArrayList<Closeable>();
	private List<Closeable> stmtList = new ArrayList<Closeable>();
	private List<Connection> cons = new ArrayList<Connection>();

	public Using(Object... resources) {
		addResources(resources);
	}

	protected final void addResources(Object... resources) {
		for (Object res : resources) {
			addResource(res);
		}
	}

	protected final void addResources(Iterable<?> resources) {
		for (Object res : resources) {
			addResource(res);
		}
	}

	protected void addResource(Object resource) {
		if (resource == null) {
			return;
		} else if (resource instanceof Closeable) {
			this.ioList.add((Closeable) resource);
		} else if (resource instanceof ResultSet) {
			final ResultSet rs = (ResultSet) resource;
			this.rsList.add(new Closeable() {
				@Override
				public void close() {
					try {
						rs.close();
					} catch (SQLException e) {
						throw new ResourceClosingException(e);
					}
				}
			});
		} else if (resource instanceof Statement) {
			final Statement stmt = (Statement) resource;
			this.stmtList.add(new Closeable() {
				@Override
				public void close() {
					try {
						stmt.close();
					} catch (SQLException e) {
						throw new ResourceClosingException(e);
					}
				}
			});
		} else if (resource instanceof Connection) {
			this.cons.add((Connection) resource);
		} else if (resource instanceof Iterable<?>) {
			this.addResources((Iterable<?>) resource);
		} else if (resource.getClass().isArray()) {
			this.addResources((Object[]) resource);
		} else {
			throw new UnsupportedTypeException("Using does not support "
					+ resource.getClass().getName());
		}
	}

	public final T invoke() {
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
			try {
				// Exception from execute method
				Throwable t = re != null ? re : err;

				ResourceClosingException rce = null;

				// close
				rce = close(ioList, rce);
				rce = close(rsList, rce);
				rce = close(stmtList, rce);

				for (Connection con : cons) {
					if (t != null || rce != null) {
						// Connection should be rolled back if the exception has
						// occurred.
						try {
							con.rollback();
						} catch (Throwable e) {
							rce = coalsceRce(rce, new ResourceClosingException(
									e));
						}
					}
					try {
						con.close();
					} catch (Throwable e) {
						rce = coalsceRce(rce, new ResourceClosingException(e));
					}
				}

				if (rce != null) {
					if (t == null) {
						// execute did not fail and close process fail case
						throw rce;
					} else {
						// both fail case
						coalsce(t, rce);
					}
				}

				// normal end or the exception thrown by execute method

			} finally {
				try {
					finallyCallback();
				} catch (Throwable ignore) {
				}
			}
		}
	}

	private ResourceClosingException close(List<Closeable> resources,
			ResourceClosingException rce) {
		for (Closeable res : resources) {
			try {
				res.close();
			} catch (Throwable t) {
				ResourceClosingException newRce;
				if (t instanceof ResourceClosingException)
					newRce = (ResourceClosingException) t;
				else
					newRce = new ResourceClosingException(t);
				rce = coalsceRce(rce, newRce);
			}
		}
		return rce;
	}

	private ResourceClosingException coalsceRce(ResourceClosingException src,
			ResourceClosingException dst) {
		if (src == null)
			return dst;
		if (dst == null)
			return src;
		coalsce(src, dst);
		return src;
	}

	private void coalsce(Throwable src, RuntimeException dst) {
		Throwable t = src;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		t.initCause(dst);
	}

	protected abstract T execute() throws Exception;

	protected void finallyCallback() {
	}
}
