package cz.vutbr.fit.pdb.ateam.exception;

/**
 * Exception used by DataManager class.
 *
 * Created by Jakub Tutko on 11.10.2015.
 */
public class DataManagerException extends Exception {
	public static final int ERROR_CODE_MAX_SESSION = 2391;
	public static final int ERROR_CODE_INVALID_LOGIN = 1017;

	private String message;

	private int errorCode;

	/**
	 * Basic constructor which sets local message variable
	 *
	 * @param message message which will be thrown
	 */
	public DataManagerException(String message) {
		this.message = message;
	}

	public DataManagerException(String message, int errorCode){
		this.errorCode = errorCode;
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

	/**
	 * Can pass error code from SQLException
	 * @return
	 */
	public int getErrorCode(){ return this.errorCode; }
}
