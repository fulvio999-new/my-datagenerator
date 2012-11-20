package mydatagenerator.core.database.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.model.bean.TableInfoPartialExportBean;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;


/**
 * Class with utility methods to export the DB content as DBUnit dataset
 * - full db content
 * - partial db content with partial table content
 *
 */
public class DatabaseExporter {
	 
	 /**
	  * Full DB export FK ordered as XML file in DBunit format 
	  * @param outputFolder
	  * @throws Exception
	  */
	public void fullExport(String outputFolder) throws Exception{		

		 IDatabaseConnection conn = null;
		
		 try {
			 if(outputFolder == null || outputFolder.equalsIgnoreCase(""))
			     throw new Exception("Invalid destination folder !");
			
			 BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			 conn = new DatabaseConnection(ds.getConnection());
			 
			 //conn = DatabaseConnectionFactory.getConnection();				
			 String targetDbName = DatabaseConnectionFactory.getDatabaseName();  
			 
			 ITableFilter tablefilter = new DatabaseSequenceFilter(conn); 	     
			 IDataSet databaseDataSet = new FilteredDataSet(tablefilter, conn.createDataSet());   
			 
			 //the name of the created file is equal at the DB name
			 FlatXmlDataSet.write(databaseDataSet, new FileOutputStream(outputFolder+File.separator+targetDbName+".xml")); 			    
			
		} catch (Exception e) {
			throw e;
			
		} finally{
			if(conn !=null)
			   conn.close();
		}
	 }
	
	 
	 /**
	  * Export only a part of the target DB: the one obtained from a provided subset of tables and their associated sql extraction queries.
	  * The user can customize that queries by adding a sql condition
	  * 
	  * See: http://www.dbunit.org/faq.html#extract
	  * 
	  * @param outputFileName The folder where write the exported data-set
	  * @param filteredTableToExport The list of table(s) to export and their associated query  
	  * @param query The query to extract the data-set to export
	  * @throws Exception
	  */
	public void partialExport(String outputFolder, List<TableInfoPartialExportBean> filteredTableToExport) throws Exception{
		
		IDatabaseConnection conn = null;
		
		try {
			if(outputFolder == null || outputFolder.equalsIgnoreCase(""))
				throw new Exception("Invalid destination folder !");
						
			BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			conn = new DatabaseConnection(ds.getConnection());
			 
			if(filteredTableToExport.size() >0)
			{			
				String targetDbName = DatabaseConnectionFactory.getDatabaseName();				
				QueryDataSet partialDataSet = new QueryDataSet(conn);	
				
				// build the data-set. NOTE: the new inserted value will be inserted in the model only when the editable cell lose the focuc 
				for(int i=0;i<filteredTableToExport.size();i++){	    	  
			       //System.out.println(filteredTableToExport.get(i).isIncludeTable());
			       //System.out.println("ADD: "+filteredTableToExport.get(i).getTableName()+","+filteredTableToExport.get(i).getExportQuery());
			       partialDataSet.addTable(filteredTableToExport.get(i).getTableName(),filteredTableToExport.get(i).getExportQuery());
			    }		
			          
				FlatXmlDataSet.write(partialDataSet, new FileOutputStream(outputFolder+File.separator+targetDbName+".xml"));   
				
			}else
				throw new Exception("Select at least one table !");
		} catch (Exception e) {
			throw e;
			
		}finally{
			if(conn !=null)
			   conn.close();
			}		
	 }
	 
	 /**
	  * Export only dependent tables database export to the provided file name
	  * ie: export table X in argument and all tables that have a PK which is a FK on X, in the right order for insertion.
	  * 
	  * @param outputFileName
	  * @param tableName
	  * @throws Exception
	  */
	public void dependencyExport(String outputFileName, String tableName) throws Exception{
		
		IDatabaseConnection conn = null;
		
		 try{		
			 BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			 conn = new DatabaseConnection(ds.getConnection());
			 
			 String[] depTableNames = TablesDependencyHelper.getAllDependentTables(conn, tableName );
			 IDataSet depDataSet = conn.createDataSet( depTableNames );
			 FlatXmlDataSet.write(depDataSet, new FileOutputStream(outputFileName)); 
			
		} catch (Exception e) {
			throw e;
			
		}finally{
		   if(conn !=null)
			  conn.close();
		}
	 }
	
}
