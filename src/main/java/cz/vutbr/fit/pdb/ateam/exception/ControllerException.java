package cz.vutbr.fit.pdb.ateam.exception;

/**
 * Exception used by Models
 * @author Tomas Mlynaric
 */
public class ControllerException extends Exception {
	private String message;

	/**
	 * Basic constructor which sets local message variable
	 *
	 * @param message message which will be thrown
	 */
	public ControllerException(String message) {
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

