package tetz42.validation;

import static tetz42.validation.FailureType.*;
import java.util.regex.Pattern;

public enum Format {
	NUMERIC {
		@Override
		public FailureType is(String s) {
			return numPtn.matcher(s).matches() ? OK : FORMAT_NOT_NUMERIC;
		}
	},
	ALPHABETIC {
		@Override
		public FailureType is(String s) {
			return alphaPtn.matcher(s).matches() ? OK : FORMAT_NOT_ALPHABETIC;
		}
	},
	ALPHANUEMERIC {
		@Override
		public FailureType is(String s) {
			return alphaNumPtn.matcher(s).matches() ? OK
					: FORMAT_NOT_ALPHANUEMERIC;
		}
	},
	ANY {
		@Override
		public FailureType is(String s) {
			return OK;
		}
	};

	Pattern numPtn = Pattern.compile("\\A[0-9]+\\z");
	Pattern alphaPtn = Pattern.compile("\\A[a-zA-Z]+\\z");
	Pattern alphaNumPtn = Pattern.compile("\\A[0-9a-zA-Z]+\\z");

	public abstract FailureType is(String s);
}
