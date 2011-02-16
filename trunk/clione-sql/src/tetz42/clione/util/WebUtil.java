package tetz42.clione.util;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebUtil {
	// Utility methods are follows:
	
	public static ParamMap convRequest(HttpServletRequest req) {
		ParamMap map = new ParamMap();
		map.putAll(convReqParams(req));
		map.putAll(convSessions(req));
		map.putAll(convReqAttrs(req));
		return map;
	}

	public static ParamMap convReqParams(ServletRequest req) {
		ParamMap map = new ParamMap();

		Enumeration<?> names = req.getParameterNames();

		while (names.hasMoreElements()) {
			String name = String.valueOf(names.nextElement());
			String[] values = req.getParameterValues(name);
			if (values.length == 1)
				map.put(name, values[0]);
			else
				map.put(name, values);
		}
		return map;
	}

	public static ParamMap convReqAttrs(ServletRequest req) {
		ParamMap map = new ParamMap();

		Enumeration<?> names = req.getAttributeNames();

		while (names.hasMoreElements()) {
			String name = String.valueOf(names.nextElement());
			map.put(name, req.getAttribute(name));
		}
		return map;
	}

	public static ParamMap convSessions(HttpServletRequest req) {
		ParamMap map = null;
		HttpSession session = req.getSession(false);
		if (session != null)
			map = convSessions(session);
		return map != null ? map : new ParamMap();
	}

	public static ParamMap convSessions(HttpSession session) {
		ParamMap map = new ParamMap();
		
		Enumeration<?> names = session.getAttributeNames();
		
		while (names.hasMoreElements()) {
			String name = String.valueOf(names.nextElement());
			map.put(name, session.getAttribute(name));
		}
		return map;
	}
}
