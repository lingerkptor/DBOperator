package idv.lingerkptor.util.DBOperator;

public interface DatabaseConfig {
	public String getDriver();
	public String getDriverUrl();
	public String getUrl();
	public String getAccount();
	public String getPassword();
	public int getMaxConnection();
	
}
