package epfl.lia.logist.exception;

public class NoConfigurationException extends LogistException {
	public NoConfigurationException() {
		super("No configuration is available");
	}
}
