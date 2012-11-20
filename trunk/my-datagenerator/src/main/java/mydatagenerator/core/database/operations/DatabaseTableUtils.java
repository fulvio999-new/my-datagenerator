package mydatagenerator.core.database.operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.init.Log4jManager;
import mydatagenerator.model.bean.AdvancedMetadataTableInfo;
import mydatagenerator.model.bean.MetadataTableInfoBean;
import mydatagenerator.model.bean.TableFKconstraintsBean;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.filter.ITableFilter;


/**
 * Class with some utility methods to obtain information about a Database table 
 *
 */
public class DatabaseTableUtils {
	
	private final static Logger logger = Logger.getLogger(DatabaseTableUtils.class);
	
	/* Query to get informations about the fields of a table  */
	private static String GET_TABLE_COLUMN_TYPE_QUERY = "SHOW FIELDS FROM ";
	
	/* query to get some advanced table informations, eg create opions to know if a table is partitioned or not */
	private static String GET_ADVANCED_TABLE_INFO_QUERY = "show table status where Name = ";
	
	/* query to get information about FK constraints of a table: which table(s) reference it and which column
	private static String GET_TABLE_FK_CONSTRAINTS = "SELECT i.TABLE_NAME,k.COLUMN_NAME, k.REFERENCED_TABLE_NAME, k.REFERENCED_COLUMN_NAME FROM information_schema.TABLE_CONSTRAINTS i " +
													 "LEFT JOIN information_schema.KEY_COLUMN_USAGE k ON i.CONSTRAINT_NAME = k.CONSTRAINT_NAME " +
													 "WHERE i.CONSTRAINT_TYPE = 'FOREIGN KEY' AND i.TABLE_NAME = '";
	*/
	
	/* Query to get information about the table(s) that have a reference to this table */	
	private static String GET_WHO_REFERENCE_THIS_TABLE_QUERY = "select TABLE_NAME,COLUMN_NAME,REFERENCED_COLUMN_NAME,REFERENCED_TABLE_NAME from information_schema.KEY_COLUMN_USAGE where CONSTRAINT_NAME <> 'PRIMARY' and TABLE_NAME = ? and TABLE_SCHEMA = ?";
	
	/* Query to get the storage engine type of a table */
	private static String GET_TABLE_STORAGE_ENGINE = "SELECT TABLE_NAME FROM information_schema.TABLES where TABLE_SCHEMA = ? and ENGINE ='ARCHIVE'";
	
	/* Query to get the total number of table(s) that compose a Database  */
	private static String GET_TOTAL_TABLE_NUMBER = "SELECT count(table_name) as total FROM information_schema.`TABLES` T where table_schema = ?";
	
	/* Query to get the list of partitioned tables */
	private static String GET_PARTITIONED_TABLES = "SELECT distinct TABLE_NAME,PARTITION_EXPRESSION,PARTITION_METHOD FROM information_schema.PARTITIONS where TABLE_SCHEMA = ? and PARTITION_METHOD is not null and partition_expression is not null";
	
	/* The list of table(s) name that compose the Database */
	private static List<String> tableNamesList = null;


	/**
	 * Constructor
	 */
	public DatabaseTableUtils() {
		
	}
	
	
	public static void setTableNamesList(List<String> tableNamesList) {
		DatabaseTableUtils.tableNamesList = tableNamesList;
	}
	
	/**
	 * Use the DBunit features to return the Database tables names in the right filling order
	 * @throws Exception 
	 *
	 */
	public List<String> getTableNamesOrdered() throws Exception {	
		
		IDatabaseConnection conn = null;
		
		try{		
			//to prevent a double loading, because the table list can be loaded in many panels
			if (tableNamesList == null)
			{		
				 ArrayList<String> tableListOrdered = new ArrayList<String>();				 
				 
				 BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
				 conn = new DatabaseConnection(ds.getConnection());
		        
				 ITableFilter filter = new DatabaseSequenceFilter(conn);
					
				 //The dataSet: ie the list of ALL tables name
				 IDataSet dataSet = new FilteredDataSet(filter, conn.createDataSet());
				
				 ITableIterator iter = dataSet.iterator();
				
				 while(iter.next())
				 {
				   ITable tab = iter.getTable();
				   String tableName = tab.getTableMetaData().getTableName();				
				   tableListOrdered.add(tableName);			
				 }				
				
				 tableNamesList = tableListOrdered;
				
				 return tableNamesList;
			
			}else			
			   return tableNamesList;
		
		}catch (Exception e) {
			
			if(Log4jManager.IS_LOGGING_CONFIGURED)
				logger.fatal("Error loading tables names ordered, cause: ",e);
			
			throw new Exception("Set a valid target Database and test the connection");
			
		}finally{
		   if(conn != null)
			  conn.close();
		}
	}
	
    //TEST	
    public static List<String> getTableNamesOrderedStatic() throws Exception {	
		
		IDatabaseConnection conn = null;
		
		try{		
			//to prevent a double loading, because the table list can be loaded in many panels
			if (tableNamesList == null)
			{		
				 ArrayList<String> tableListOrdered = new ArrayList<String>();	
				 
				 BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
				 conn = new DatabaseConnection(ds.getConnection());
		        
				 ITableFilter filter = new DatabaseSequenceFilter(conn);
					
				 //The dataSet: ie the list of ALL tables name
				 IDataSet dataSet = new FilteredDataSet(filter, conn.createDataSet());
				
				 ITableIterator iter = dataSet.iterator();
				
				 while(iter.next())
				 {
				   ITable tab = iter.getTable();
				   String tableName = tab.getTableMetaData().getTableName();				
				   tableListOrdered.add(tableName);			
				 }				
				
				 tableNamesList = tableListOrdered;
				
				 return tableNamesList;
			
			}else			
			   return tableNamesList;
		
		}catch (Exception e) {
			throw new Exception("Set a valid target Database and test the connection");
			
		}finally{
		   if(conn != null)
			  conn.close();
		}
	}
	
	
	 /**
	  * --- CURRENTLY NOT USED ---
     * Check if the provided table name is empty
     * @param tableName the name of the table
     * @throws Exception
     */
    public boolean isTableEmpty(String tableName) throws Exception
	{    
    	IDatabaseConnection conn = null;
    	
    	try{     		
    		 BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			 conn = new DatabaseConnection(ds.getConnection());
	         
	    	 int rowCount = conn.getRowCount(tableName);
	    	 conn.close();
	    	
	    	 if(rowCount == 0)
	    	   return true;
	    	 else 
	    	   return false;  
    	
    	}catch (Exception e) {
			throw e;
			
		}finally{
		    if(conn != null)
			   conn.close();
		}
	}    
    
    
    /**
	 * Get informations about the fields of a table (eg field type,name...)
	 * NOTE: this method use the native DB connection (instead of the DBunit wrapped one) to obtain the above information
	 * because uses sql query against the MySql database.
	 * 
	 * @throws Exception 
	 */
	public ArrayList<MetadataTableInfoBean> getFieldsInfo(String tableName) throws Exception {
		
		Connection conn = null;
		Statement statement = null;
		
		ArrayList<MetadataTableInfoBean> tableFieldInfoBeanList = new ArrayList<MetadataTableInfoBean>();	
		
		try{
			BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			conn = ds.getConnection();
			/* Note: use a Statement instead of PreparedStatement because the query is not a standard sql but MySql special one  */
			statement = conn.createStatement();			
		    
			if(Log4jManager.IS_LOGGING_CONFIGURED)
			   logger.fatal("Get table field details with statement: "+GET_TABLE_COLUMN_TYPE_QUERY);
					
			ResultSet tableFieldsResultset = statement.executeQuery(GET_TABLE_COLUMN_TYPE_QUERY+tableName);
			
			while (tableFieldsResultset.next()) 
			{				
				 String fieldColumn = tableFieldsResultset.getString("Field");
				 String typeColumn = tableFieldsResultset.getString("Type"); //eg varchar(10)  bigint(10)
				 String nullColumn = tableFieldsResultset.getString("Null");
				 String keyColumn = tableFieldsResultset.getString("Key"); //PRI if primary key, MUL if there is an index on the column
				 String defaultColumn = tableFieldsResultset.getString("Default");	
				 String extraInfo = tableFieldsResultset.getString("Extra"); //eg auto_increment
				 
				 // bean that represents a table row
				 MetadataTableInfoBean tableFieldInfoBean = new MetadataTableInfoBean();
				 
				 tableFieldInfoBean.setFieldName(fieldColumn);
				 tableFieldInfoBean.setFieldType(typeColumn);
				 tableFieldInfoBean.setFieldNullable(nullColumn);
				 tableFieldInfoBean.setIsPK(keyColumn);
				 tableFieldInfoBean.setDefaultValue(defaultColumn);
				 tableFieldInfoBean.setExtraInfo(extraInfo);
				 
				 //logger.info("** -Field:"+fieldColumn+" -Type:"+typeColumn+" -Nullable: "+nullColumn+" -isPK: "+keyColumn+" -Default: "+defaultColumn+ " Extra: "+extraInfo);
				 
				 tableFieldInfoBeanList.add(tableFieldInfoBean);
			}
			
			// get if the table is partitioned looking at 
		 
		}catch (Exception e) {			
			throw e;
			
		}finally{	
			
		   if(statement != null)
			  statement.close();	//close statement and the resultSet associated
			
		   if(conn != null)
			  conn.close();	
		}
		
		return tableFieldInfoBeanList;
	}
	
	/**
	 * Get some advanced informations about a table (eg the create options to know if a table is partitioned or not)
	 * @param tableName
	 * @throws Exception 
	 */
	public ArrayList<AdvancedMetadataTableInfo> getAdvancedTableInfo(String tableName) throws Exception{
		
		Connection conn = null;
		Statement statement = null;
		
		ArrayList<AdvancedMetadataTableInfo> advancedTableInfoBeanList = new ArrayList<AdvancedMetadataTableInfo>();	
		
		try{
			BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			conn = ds.getConnection();
			/* Note: use a Statement instead of PreparedStatement because the query is not a standard sql but MySql special one  */
			statement = conn.createStatement();			
		    
			if(Log4jManager.IS_LOGGING_CONFIGURED)
			   logger.fatal("Get table advanced info: "+GET_ADVANCED_TABLE_INFO_QUERY);
					
			ResultSet tableinfoResultset = statement.executeQuery(GET_ADVANCED_TABLE_INFO_QUERY+"'"+tableName+"'");
			
			while (tableinfoResultset.next()) 
			{	
				 String engine = tableinfoResultset.getString("Engine");
				 String createOptions = tableinfoResultset.getString("Create_options"); 
				 
				 if(createOptions.equalsIgnoreCase("") || createOptions == null) // true if a table is not partitioned
				 	createOptions = "Not partitioned";
				 
				 String dataFree = tableinfoResultset.getString("Data_free"); 
				 
				 // bean that represents a table row
				 AdvancedMetadataTableInfo advancedMetadataTableInfo = new AdvancedMetadataTableInfo();
				 
				 advancedMetadataTableInfo.setEngine(engine);
				 advancedMetadataTableInfo.setCreateOptions(createOptions);				
				 advancedMetadataTableInfo.setDataFree(dataFree);	
				 
				 advancedTableInfoBeanList.add(advancedMetadataTableInfo);
			}
		 
		}catch (Exception e) {			
			throw e;
			
		}finally{	
			
		   if(statement != null)
			  statement.close();	//close statement and the resultSet associated
			
		   if(conn != null)
			  conn.close();	
		}
		
		return advancedTableInfoBeanList;
	}
	
    
	/**
	 * Get the table(s) names and column that have a references to the provide table name 
	 * (ie they have a foreign keys (FK) constraints)
	 * NOTE: this method use the native DB connection (instead of the DBunit wrapped one) to obtain the above information
	 * because uses sql query against the MySql database.
	 * 
	 * @param tableName the target table 
	 * @return TableFKconstraintsBean A dedicated bean with the required information
	 * @throws Exception 
	 */
	public ArrayList<TableFKconstraintsBean> getFkInformation(String table) throws Exception{
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		/* The list of referenced tables and the column names */
		ArrayList<TableFKconstraintsBean> tableFKinfoBeanList = new ArrayList<TableFKconstraintsBean>();
		
		try{
			BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			conn = ds.getConnection();
			
			preparedStatement = conn.prepareStatement(GET_WHO_REFERENCE_THIS_TABLE_QUERY);
		    preparedStatement.setString(1, table);
		    preparedStatement.setString(2, DatabaseConnectionFactory.getDatabaseName());
		    
		    if(Log4jManager.IS_LOGGING_CONFIGURED)
			   logger.fatal("Get FK info for table "+table+" with statement: "+GET_WHO_REFERENCE_THIS_TABLE_QUERY);
			
			ResultSet tableFKResultset = preparedStatement.executeQuery();
			
			while (tableFKResultset.next()) 
			{
				 String parentTableName = tableFKResultset.getString("TABLE_NAME"); //the table that references the input table
				 String parentColumnName = tableFKResultset.getString("COLUMN_NAME"); //the column that is FK in the input table (ie parent column)
				 
				 String childTableName =  tableFKResultset.getString("REFERENCED_TABLE_NAME"); //the referenced table
				 String childColumnName = tableFKResultset.getString("REFERENCED_COLUMN_NAME"); //the child column referenced by the FK				
			
				 TableFKconstraintsBean tableFKconstraintsBean = new TableFKconstraintsBean();	
				 
				 tableFKconstraintsBean.setParentTableName(parentTableName);  //the parent table
				 tableFKconstraintsBean.setParentColumnName(parentColumnName);//the parentColumn
				 
				 tableFKconstraintsBean.setReferencedTableName(childTableName);
				 tableFKconstraintsBean.setReferencedColumnName(childColumnName);
				 
				 tableFKinfoBeanList.add(tableFKconstraintsBean);
			}		
			
		}catch (Exception e) {
			throw e;
			
		}finally{
			
		   if(preparedStatement != null)
			  preparedStatement.close();
			
		   if(conn != null)
			  conn.close();				
		}	
		
		return tableFKinfoBeanList;
	}
	
	/**
	 * Get all the values in the provided column and table so that is possible obtain a set of allowed values that can be insert in a parent table column
	 * without break a FK constraint.
	 * Obviously this method is useful if the provided column name is a FK
	 * 
	 * @param table
	 * @param column
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<String> getColumnValue(String table,String column) throws Exception{
		
		Connection conn = null;		
		Statement statement = null;
		
		ArrayList<String> columnValue = new ArrayList<String>();
		
		try{				
			BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			conn = ds.getConnection();			  
			statement = conn.createStatement();		
		    
		    if(Log4jManager.IS_LOGGING_CONFIGURED)
			   logger.fatal("Get all column values for table "+table+"("+column+") with statement: "+"select ? from ?");
	       
			ResultSet columnResultset = statement.executeQuery("select "+ column+ " from "+ table);			
			
			while (columnResultset.next()) 
			{
			  columnValue.add(columnResultset.getString(1));
			}
			
		}catch (Exception e) {
			throw e;
			
		}finally{
			
			if(statement != null)
			   statement.close();
			
			if(conn != null)
			   conn.close();
		}	
		
		return columnValue;
	}
	
	
	/**
	 * Utility method to get the tables name with storage engine type 'ARCHIVE' 
	 * @return a list of tables name whose archive type is of type 'ARCHIVE'
	 * 
	 * @throws Exception 
	 */
	public List<String> getArchiveTables() throws Exception{
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		try{			
			 BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
			 conn = ds.getConnection();
			
			 preparedStatement = conn.prepareStatement(GET_TABLE_STORAGE_ENGINE);
		     preparedStatement.setString(1, DatabaseConnectionFactory.getDatabaseName());
			
		     if(Log4jManager.IS_LOGGING_CONFIGURED)
				logger.fatal("Get Archive tables statement: "+GET_TABLE_STORAGE_ENGINE);
		     
		     ResultSet tableResultset = preparedStatement.executeQuery();			
			 List<String> archiveTableNames = new ArrayList<String>();
			
			while (tableResultset.next()) 
			{    
			  archiveTableNames.add(tableResultset.getString(1));			
			}
						
			return archiveTableNames;
	        
		}catch (Exception e) {		
			throw e;
			
		}finally{
			
			 if(preparedStatement != null)
				preparedStatement.close();
				
			 if(conn != null)
			    conn.close();	
		 }		
	}
    
    
    /**
     * -*- Currently not used -*-
     *  
     * Using the wrapped connection offered by DBunit (ie IDatabaseConnection) check if the provided table contains the provided amount of row 
     * Used to test if ALL the data in a data-set are loaded in the table
     * @param totalRow
     * @param tableName
     * @return
     * @throws Exception
     */
    public boolean testConfigTableDataLoaded(int totalRow, String tableName) throws Exception
	{  
      IDatabaseConnection conn = null;
      
      try{ 
    	  BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
		  conn = new DatabaseConnection(ds.getConnection());
    	  
	      int tableRowCount = conn.getRowCount(tableName);
	      
	      if(tableRowCount == totalRow)
	   	     return true;
	   	  else 
	   	     return false;  
	      
      	}catch (Exception e) {
      		throw e;
		
      	}finally{
      	   if(conn != null)
			  conn.close();	 
	  }
	}
    
    /**
     * Get the total number of tables contained in the target database (with no filter on engine type)     
     * @return
     * @throws Exception
     */
    public int getTableCount() throws Exception
	{ 
       Connection conn = null;
       PreparedStatement preparedStatement = null;
       
       try{  
    	   BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
		   conn = ds.getConnection(); 	      
		   
	       preparedStatement = conn.prepareStatement(GET_TOTAL_TABLE_NUMBER);
	       preparedStatement.setString(1, DatabaseConnectionFactory.getDatabaseName());
	       
	       if(Log4jManager.IS_LOGGING_CONFIGURED)
			  logger.fatal("Get the amount of tables with statement: "+GET_TOTAL_TABLE_NUMBER);
	       
		   ResultSet tableResultset = preparedStatement.executeQuery();		  
		   tableResultset.next();
		   
		   return tableResultset.getInt(1);
	  
    	}catch (Exception e) {
			throw e;
			
		}finally{
			
			if(preparedStatement != null)
			   preparedStatement.close();
			
			if(conn !=null)
			   conn.close();
		}       
	}
    /**
     * Get the partitioned tables in the target DB and some details about them (eg the partition function)
     * 
     * @return
     */
    public List<String> getPartitionedTables() throws Exception
    {    	
    	Connection conn = null;
    	PreparedStatement preparedStatement = null;
        
        try{  
     	   BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
 		   conn = ds.getConnection();
 		   
 		   preparedStatement = conn.prepareStatement(GET_PARTITIONED_TABLES);
 		   preparedStatement.setString(1, DatabaseConnectionFactory.getDatabaseName());
 		   
 		  if(Log4jManager.IS_LOGGING_CONFIGURED)
			  logger.fatal("Get partitioned tables list with statement: "+GET_PARTITIONED_TABLES);
 		   
 		   ResultSet rs = preparedStatement.executeQuery( );
 		  
 		   while (rs.next()) {
// 		 	String userid = rs.getString("USER_ID");
// 		 	String username = rs.getString("USERNAME");	
 		   } 		   
 		   
 		  return null;
 		   
        }catch (Exception e) {
			throw e;
			
		}finally{
			
			if(preparedStatement != null)
			   preparedStatement.close();
			
			if(conn !=null)
			   conn.close();
		}     	
    }    
    
    /**
     * Change the engine type of a table to InnoDB. Used to clean the table with engine ARCHIVE
     * 
     * @param tableName The table name to change his engine type
     * @param newEngineType The engine type to set
     */
    public void changeTableEngine(String tableName, String newEngineType) throws Exception{
    	
    	String 	alterQuery = "ALTER TABLE "+tableName+" ENGINE = ?";    	
    	Connection conn = null;
    	PreparedStatement preparedStatement = null;
        
        try{        	
     	    BasicDataSource ds = DatabaseConnectionFactory.getDataSource();
 		    conn = ds.getConnection();
 		    
 		    preparedStatement = conn.prepareStatement(alterQuery); 		   
 		    preparedStatement.setString(1, newEngineType);
 		  
 		    int rowUpdated = preparedStatement.executeUpdate();
 		   
        }catch (Exception e) {
        	throw e;
        	
		}finally{
			
			if(preparedStatement != null)
				preparedStatement.close();
			
			if(conn !=null)
			   conn.close();
		}
    	
    }
 

}
