/**
 *
 */
package tetz42.validation;

import static tetz42.util.Util.*;

import java.util.List;

public class FailureInfo {
	public String name;
	public FailureType type;
	public String additionalInfo;

	FailureInfo(String name, FailureType type) {
		this(name, type, null);
	}

	FailureInfo(String name, FailureType type, Object additionalInfo) {
		this.name = name;
		this.type = type;
		setAdditionalInfo(additionalInfo);
	}

	void addName(String name) {
		this.name += ", " + name;
	}

	void setAdditionalInfo(Object obj) {
		if(obj != null) {
			if(obj.getClass().isArray())
				this.additionalInfo = mkStringByComma((Object[])obj);
			else if(obj instanceof List<?>)
				this.additionalInfo = mkStringByComma((List<?>) obj);
			else
				this.additionalInfo = "" + obj;
		}
	}
}