package idv.lingerkptor.util.DBOperator;
/**
 * 資料庫設定
 * @author lingerkptor
 *
 */
public interface DatabaseConfig {
	public String getDriver();
	public String getDriverUrl();
	public String getUrl();
	public String getAccount();
	public String getPassword();
	public int getMaxConnection();
	
}
