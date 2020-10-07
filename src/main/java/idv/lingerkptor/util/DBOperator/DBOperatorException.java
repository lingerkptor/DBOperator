package idv.lingerkptor.util.DBOperator;

/**
 * 
 * @author lingerkptor
 *
 *
 */
public class DBOperatorException extends RuntimeException {
	/**
	 * 錯誤碼<br/>
	 * CLOSING ： 關閉中<br/>
	 * UNREADY ： 資料庫尚未給定 <br/>
	 * CLOSED ： 已關閉<br/>
	 * CONNECTFULL ： Connection 已滿<br/>
	 * CONFIGISNULL // 資料庫沒有設定檔
	 */
	public enum CODE {
		CLOSING // 關閉中
		, UNREADY // 資料庫尚未給定
		, CLOSED // 已關閉
		, CONNECTFULL // Connection 已滿
		, CONFIGISNULL // 資料庫沒有設定檔
	};

	/**
	 * 錯誤碼
	 */
	private CODE code;
	private static final long serialVersionUID = -8923279912278751216L;

	/**
	 * 給定錯誤碼，讓使用者方便處理（使用switch case）
	 * 
	 * @param message 訊息
	 * @param code    錯誤碼
	 */

	public DBOperatorException(String message, CODE code) {
		super("Code"+code+" "+message);
		this.code = code;
	}

	public CODE getState() {
		return code;
	}
}
