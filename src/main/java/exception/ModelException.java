package exception;

/**
 * Exception used by Models
 * Created by Tomas Mlynaric on 21.10.2015.
 */
public class ModelException extends Exception {
	private String message;

	/**
	 * Basic constructor which sets local message variable
	 *
	 * @param message message which will be thrown
	 */
	public ModelException(String message) {
		this.message = message;
	}

	/**
	 * Getter to message variable
	 *
	 * @return message variable
	 */
	@Override
	public String getMessage() {
		return this.message;
	}
}

