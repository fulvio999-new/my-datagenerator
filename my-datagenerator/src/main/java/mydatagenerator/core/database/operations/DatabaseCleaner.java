package mydatagenerator.core.database.operations;

import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.operation.DatabaseOperation;


/**
 * Core class with the business methods to clean the table(s) of the database
 *
 */
public class DatabaseCleaner {

	/**
	 * Constructor
	 */
	public DatabaseCleaner() {
		
	}  
  
	/**
	 * Delete the contents of the tables names in argument.
	 * Note: the tables with engine type 'ARCHIVE' will no be cleaned, because this type of store engine doesn't support
	 * the sql 'delete' operation (See MySql documentation for more info).
	 * 
	 * @param tables The list of tables names to clean. The list mustn't contains table with 'ARCHIVE' engine type
	 * @throws Exception
	 */
	public void deleteAll(String[] tables) throws Exception {      
        
		 try{	
			 BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
	        
			 //ITableFilter filter = new DatabaseSequenceFilter(conn, tables);
			 IDatabaseConnection conn = new DatabaseConnection(ds.getConnection()); 
			 ITableFilter filter = new DatabaseSequenceFilter(conn, tables);
			
			 //The target dataSet: ie all the tables in the input list
			 IDataSet dataSet = new FilteredDataSet(filter, conn.createDataSet());			
			
			 DatabaseOperation.DELETE_ALL.execute(conn, dataSet);	
		 
			 conn.close();			
			
		}catch (Exception e) {			
			throw e;					
		}
   }

}
