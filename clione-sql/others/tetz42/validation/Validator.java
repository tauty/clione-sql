package tetz42.validation;

import static tetz42.util.ReflectionUtil.*;
import static tetz42.util.Util.*;
import static tetz42.validation.FailureType.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tetz42.validation.annotation.Valid;

public class Validator {

	private List<FailureInfo> failureInfoList = null;

	private Map<String, FailureInfo> map = null;

	public List<FailureInfo> nextFailureList() {

		if (map != null) {
			for (FailureInfo info : map.values()) {
				if (info.type != OK)
					failList().add(info);
			}
		}
		map = null;

		List<FailureInfo> list = failureInfoList;
		this.failureInfoList = null;

		return list;
	}

	public boolean isValueOK(Field field, String value) {
		Valid v = getAnnotation(field, Valid.class);
		if (v == null)
			return true;

		String name = evl(v.name(), field.getName());

		if (!isEmpty(v.atLeastOne())) {
			if (isEmpty(value)) {
				setAtLeastOne(v.atLeastOne(), name, REQUIRED_AT_LEAST_ONE);
				return true;
			} else {
				setAtLeastOne(v.atLeastOne(), name, OK);
			}
		}

		switch (v.required()) {
		case NOT_EMPTY:
			if (isEmpty(value))
				return newFailureInfo(name, REQUIRED);
			break;
		case NOT_NULL:
			if (value == null)
				return newFailureInfo(name, REQUIRED);
			break;
		case FALSE:
			if (isEmpty(value))
				return true;
			break;
		}

		if (v.isIn().length != 0) {
			boolean isFail = true;
			for (String s : v.isIn()) {
				if (s.equals(value)) {
					isFail = false;
				}
			}
			if (isFail)
				return newFailureInfo(name, NOT_IN_VALUES, v.isIn());
		}

		if (isSpecified(v.length())) {
			if (value.length() < v.length())
				return newFailureInfo(name, LENGTH_TOO_SHORT, v.length());
			else if (value.length() > v.length())
				return newFailureInfo(name, LENGTH_TOO_LONG, v.length());
		}

		if (isSpecified(v.maxLength()) && value.length() > v.maxLength()) {
			return newFailureInfo(name, LENGTH_TOO_LONG, v.maxLength());
		}

		if (isSpecified(v.minLength()) && value.length() < v.minLength()) {
			return newFailureInfo(name, LENGTH_TOO_SHORT, v.minLength());
		}

		// byte length must be checked before the String is generated.
		// if (v.byteLength() != -1) {
		// if (value.getBytes().length < v.byteLength())
		// return newFailureInfo(name, BYTELENGTH_TOO_SHORT);
		// else if (value.length() > v.byteLength())
		// return newFailureInfo(name, BYTELENGTH_TOO_LONG);
		// }
		//
		// if (v.maxByteLength() != -1 && value.length() > v.maxByteLength()) {
		// return newFailureInfo(name, BYTELENGTH_TOO_LONG);
		// }
		//
		// if (v.minByteLength() != -1 && value.length() < v.minByteLength()) {
		// return newFailureInfo(name, BYTELENGTH_TOO_SHORT);
		// }

		FailureType type;
		Format format = v.format();
		if (OK != (type = format.is(value)))
			return newFailureInfo(name, type);

		return true;
	}

	private List<FailureInfo> failList(){
		if (failureInfoList == null)
			failureInfoList = new ArrayList<FailureInfo>();
		return failureInfoList;
	}

	private boolean newFailureInfo(String name, FailureType type) {
		return newFailureInfo(name, type, null);
	}

	private boolean newFailureInfo(String name, FailureType type,
			Object addtionalInfo) {
		failList().add(new FailureInfo(name, type, addtionalInfo));
		return false;
	}

	private void setAtLeastOne(String target, String name, FailureType type) {
		if (map == null)
			this.map = new HashMap<String, FailureInfo>();
		FailureInfo info = map.get(target);
		if (info == null) {
			map.put(target, info = new FailureInfo(name, type));
		} else {
			info.addName(name);
			info.type = info.type == OK ? OK : type;
		}
	}

	private boolean isSpecified(long setting) {
		return setting != Long.MIN_VALUE;
	}

}
