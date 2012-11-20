
package mydatagenerator.core.database.operations.utility;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;


/**
 * Utility class that work as a Factory to provide a connection with the target database (the caller that obtained a connection must close it)
 *
 */
public class DatabaseConnectionFactory {
	
	/* The real DB connection used by DBUnit to perform the operations */
	private static BasicDataSource ds;
	
	/* The target DB name */
	private static String databaseName;
	
	/* custom object that contains the inserted parameters about the target DB */
	private static DataPumpDatabaseConnection dataPumpDatabaseConnection;
	
	/*
	 * Constants used to configure the connection pool
	 */
	private static int POOL_INITIAL_SIZE = 6;
	private static String POOL_VALIDATION_QUERY = "select 1";
	private static int POOL_MAX_IDLE = 4;
	private static int POOL_MAX_ACTIVE = 4;	
	private static boolean POOL_TEST_ON_BORROW = true;
	private static boolean POOL_TEST_WHILE_IDLE = true;
	

	/**
	 * Constructor
	 */
	public DatabaseConnectionFactory() {
				
	}		

	
	/**
	 * Return the connection object used by DBunit (ie a wrapped one from the native jdbc connection) to perform the operations
	 * @return
	 * @throws Exception If a valid connection can't be established with the database
	 */
	public static BasicDataSource getDataSource() throws Exception {
		
		try{		
			if (ds == null) 
			{			
				databaseName = dataPumpDatabaseConnection.getDbname();
				
				String connectURI = "jdbc:mysql://"+dataPumpDatabaseConnection.getHost()+":"+dataPumpDatabaseConnection.getPort()+"/"+dataPumpDatabaseConnection.getDbname()+"?zeroDateTimeBehavior=convertToNull&user="+dataPumpDatabaseConnection.getUser()+"&password="+ dataPumpDatabaseConnection.getPassword();
				
				// Execute: SHOW STATUS WHERE variable_name = 'Threads_connected' to get active connections
				ds = new BasicDataSource();
				ds.setDriverClassName(dataPumpDatabaseConnection.getDriver());
				ds.setUrl(connectURI);
				ds.setUsername(dataPumpDatabaseConnection.getUser());
				ds.setPassword(dataPumpDatabaseConnection.getPassword());
				ds.setInitialSize(POOL_INITIAL_SIZE);
				ds.setTestOnBorrow(POOL_TEST_ON_BORROW);
				ds.setTestWhileIdle(POOL_TEST_WHILE_IDLE);
				ds.setValidationQuery(POOL_VALIDATION_QUERY);		
				ds.setMaxIdle(POOL_MAX_IDLE);
				ds.setMaxActive(POOL_MAX_ACTIVE);
				
				return ds;
				
			}else {			
				return ds;				
			}	
		}catch (Exception e) {
			throw new Exception("Set a valid target Database and test the connection"); //ie the factory wasn't initialized
		}
    }
	
	
	/**
	 * Set the connection created using the values inserted in the target database form
	 * @param dataPumpDatabaseConnection
	 */
	public void initializeFactory(DataPumpDatabaseConnection dataPumpConnection) {
		dataPumpDatabaseConnection = dataPumpConnection;
	}	
	

	/*
	 * Closes and releases all idle connections that are currently stored in the connection pool associated with this data source.
	 * Closing an already closed BasicDataSource has no effect and does not generate exceptions
	 * It is called before closing the JFrame
	 */
	public static void closeDataSource(){
		
		if (ds != null){
		   try {
			 ds.close();
		   }catch (SQLException e) {	}
		}
	}

	public static String getDatabaseName() {
	   return databaseName;
	}

}
