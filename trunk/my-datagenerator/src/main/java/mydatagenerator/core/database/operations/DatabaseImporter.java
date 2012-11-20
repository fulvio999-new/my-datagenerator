package mydatagenerator.core.database.operations;

import java.io.File;

import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;


/**
 * Class with utility methods to import a DBUnit dataset contained in a XML file
 *
 */
public class DatabaseImporter {

	/**
	 * Constructor
	 */
	public DatabaseImporter() {
		
	}

	/**
	  * Import a dataSet contained in the input XML file in the target database
	  * @param inputFile
	  * @throws Exception
	  */
	public void importDataSet(String inputFile) throws Exception{
		
		IDatabaseConnection conn = null;
		
		 try {
			if(inputFile == null || inputFile.equalsIgnoreCase(""))
				throw new Exception("Invalid input file !");
			 
			 BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			 conn = new DatabaseConnection(ds.getConnection());
			 
			 //conn = DatabaseConnectionFactory.getConnection();			 
			 // New feature since dbunit 2.4.7
			 FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder(); 	    
			 IDataSet dataSet = builder.build(new File(inputFile));
   
			 DatabaseOperation.INSERT.execute(conn, dataSet);
			
		} catch (Exception e) {
			throw e;
			
		} finally{
		    if(conn !=null)
		 	   conn.close();
		} 		
	}

}
