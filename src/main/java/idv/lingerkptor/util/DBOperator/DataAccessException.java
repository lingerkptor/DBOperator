package idv.lingerkptor.util.DBOperator;

public class DataAccessException extends RuntimeException{

	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataAccessException(String message) {
		super(message);
	}

}
