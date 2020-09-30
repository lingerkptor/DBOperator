package idv.lingerkptor.util.DBOperator;

public class DataAccessException extends RuntimeException{

	/**
	 */
	private static final long serialVersionUID = -7511882287819368246L;

	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataAccessException(String message) {
		super(message);
	}

}
