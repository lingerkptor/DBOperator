package idv.lingerkptor.util.DBOperator;

import idv.lingerkptor.util.DBOperator.ConnectPool.STATE;

/**
 * 
 * @author lingerkptor
 *
 *
 */
public class DBOperatorException extends RuntimeException {
	/**
	 * 錯誤碼
	 */
	private STATE code;
	private static final long serialVersionUID = -8923279912278751216L;

	@SuppressWarnings("unused")
	private DBOperatorException() {

	}

	@SuppressWarnings("unused")
	private DBOperatorException(String message) {

	}

	/**
	 * 給定錯誤碼，讓使用者方便處理（使用switch case）
	 * 
	 * @param message 訊息
	 * @param code    錯誤碼
	 */

	public DBOperatorException(String message, STATE code) {
		super("Code " + code + " : " + message);
	}

	public STATE getState() {
		return code;
	}
}
