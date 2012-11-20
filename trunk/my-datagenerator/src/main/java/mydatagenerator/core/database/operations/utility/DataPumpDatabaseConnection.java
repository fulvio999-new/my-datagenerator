
package mydatagenerator.core.database.operations.utility;

/**
 * Bean that represents a Connection with the target DB
 * It is a singleton object, initialized when the user test the connection successfully
 */
public class DataPumpDatabaseConnection {
	
	private String driver;
	private String host;
	private String port;	
	private String dbname;
	private String user;
	private String password;

	/**
	 * Constructor
	 */
	public DataPumpDatabaseConnection(String driver,String host,String port,String dbname,String user,String password) {		
		
		this.driver = driver;
		this.host = host;
		this.port = port;	
		this.dbname = dbname;
		this.user = user;
		this.password = password;		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

}
