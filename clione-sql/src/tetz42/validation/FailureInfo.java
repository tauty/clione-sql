/**
 *
 */
package tetz42.validation;

public class FailureInfo {
	public final String name;
	public final FailureType type;

	FailureInfo(String name, FailureType type) {
		this.name = name;
		this.type = type;
	}
}