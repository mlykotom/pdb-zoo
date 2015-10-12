package adapter;

/**
 * Exception used by DataManager class.
 *
 * Created by Jakub Tutko on 11.10.2015.
 */
public class DataManagerException extends Exception {
	private String message;

	/**
	 * Basic constructor which sets local message variable
	 *
	 * @param message message which will be thrown
	 */
	public DataManagerException(String message) {
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
