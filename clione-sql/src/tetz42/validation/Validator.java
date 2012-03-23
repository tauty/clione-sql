package tetz42.validation;

import static tetz42.util.ReflectionUtil.*;
import static tetz42.util.Util.*;
import static tetz42.validation.FailureType.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import tetz42.validation.annotation.Valid;

public class Validator {

	private List<FailureInfo> failureInfoList = null;

	public boolean isOK() {
		return failureInfoList == null || failureInfoList.size() == 0;
	}

	public List<FailureInfo> nextFailureList() {
		List<FailureInfo> list = failureInfoList;
		this.failureInfoList = null;
		return list;
	}

	public boolean isValueOK(Field field, String value) {
		Valid v = getAnnotation(field, Valid.class);
		if (v == null)
			return true;

		FailureType type;

		String name = evl(v.name(), field.getName());

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

		if (isSpecified(v.length())) {
			if (value.length() < v.length())
				return newFailureInfo(name, LENGTH_TOO_SHORT);
			else if (value.length() > v.length())
				return newFailureInfo(name, LENGTH_TOO_LONG);
		}

		if (isSpecified(v.maxLength()) && value.length() > v.maxLength()) {
			return newFailureInfo(name, LENGTH_TOO_LONG);
		}

		if (isSpecified(v.minLength()) && value.length() < v.minLength()) {
			return newFailureInfo(name, LENGTH_TOO_SHORT);
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

		Format format = v.format();
		if (OK != (type = format.is(value)))
			return newFailureInfo(name, type);

		return true;
	}

	private boolean newFailureInfo(String name, FailureType type) {
		if (failureInfoList == null)
			this.failureInfoList = new ArrayList<FailureInfo>();
		failureInfoList.add(new FailureInfo(name, type));
		return false;
	}

	private boolean isSpecified(long setting) {
		return setting != Long.MIN_VALUE;
	}

}
