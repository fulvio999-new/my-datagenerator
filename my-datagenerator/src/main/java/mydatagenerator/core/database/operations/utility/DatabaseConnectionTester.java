
package mydatagenerator.core.database.operations.utility;

import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;


/**
 * Utility class called from the Database settings panel that try to connect with the DB whose connection parameters are provided by the user.
 *
 */
public class DatabaseConnectionTester {

	/**
	 * Constructor
	 */
	public DatabaseConnectionTester() {
		
	}
	
	
	/**
	 * Test the DB connection. Used in the "target database panel"
	 * @return true if the DB is working
	 * @throws Exception 
	 */
	public boolean testConnection() throws Exception{
				
		Connection conn = null;
		
		try {
			BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			conn = ds.getConnection();
						
			return true;

		}catch (Exception e) {	
			throw new Exception("Can't connect with the database !");			
		}
		
	}

}
