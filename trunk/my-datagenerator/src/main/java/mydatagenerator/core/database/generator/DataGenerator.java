
package mydatagenerator.core.database.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mydatagenerator.core.database.generators.datetime.DateTimeGenerator;
import mydatagenerator.core.database.generators.numeric.RandomNumberGenerator;
import mydatagenerator.core.database.generators.string.RandomWordGenerator;
import mydatagenerator.core.database.operations.DatabaseTableUtils;
import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.init.Log4jManager;
import mydatagenerator.model.bean.MetadataTableInfoBean;
import mydatagenerator.model.bean.TableFKconstraintsBean;

import org.apache.log4j.Logger;

/**
 * Data generator core class that fill a provided tables list
 * 
 * TODO: next release, if possible optimize the algorithm and add support for further generator type
 *
 */
public class DataGenerator {
	
	private final static Logger logger = Logger.getLogger(DataGenerator.class);
	
	private String numericGeneratorType;
	private String stringGeneratorType;
	private String datetimeGeneratorType;
	private String outputSqlFile;
	
	private int recordToInsert;
	
	/* The maximun length of the text to insert in the MySQl text type */	
	private static int MAX_TEXT_LENGTH_TEXT_TYPE = 200;

	/**
	 * Constructor
	 */
	public DataGenerator(String numericGeneratorType, String stringGeneratorType, String datetimeGeneratorType, int recordToInsert, String outputFile) {
		
		 this.numericGeneratorType = numericGeneratorType;
		 this.stringGeneratorType = stringGeneratorType;
		 this.datetimeGeneratorType = datetimeGeneratorType;
		 this.recordToInsert = recordToInsert;
		 this.outputSqlFile = outputFile;
	}
	
	
	/**
	 * @param numericGeneratorType
	 * @param stringGeneratorType
	 * @param datetimeGeneratorType
	 * 
	 * @return return the number of tables filled
	 * 
	 * @throws Exception 
	 */
	public int start() throws Exception{		
		
        Connection conn = null;
        File outputFile = null;
        FileWriter fileWriter = null;
        BufferedWriter out = null;
        boolean saveToFile = false;
        
        int totalTableFilled = 0;
        
        if(numericGeneratorType == null || stringGeneratorType == null || datetimeGeneratorType == null)
           throw new Exception("Generators not initialized correctly !");
        
        /* Create and Initialize the necessary generators */        
        RandomNumberGenerator randomNumberGenerator = null;
        
        /* create random number in a small range, used for TINYINT data type */
        RandomNumberGenerator smallRandomNumberGenerator = null; 
        
        Random randomInt = new Random();
        
        RandomWordGenerator randomWordGenerator = null;        
        DateTimeGenerator dateTimeGenerator = null;
        
        // Number generator        
        if(numericGeneratorType.equalsIgnoreCase("Random-Number")){ 
           smallRandomNumberGenerator = new RandomNumberGenerator(); //used for TINYINT type
           randomNumberGenerator = new RandomNumberGenerator();  //increase the range to prevent duplicate values in case of little entry   
        }
        
        // String generator        
        if(stringGeneratorType.equalsIgnoreCase("Random-Word")){ 
           randomWordGenerator = new RandomWordGenerator(); 
        }
        
        // DateTime generator        
        if(datetimeGeneratorType.equalsIgnoreCase("Random")){ 
           dateTimeGenerator = new DateTimeGenerator();   
        }
        
        //**** Query building and database filling ****
       
        /* The current table and his current column. Field used like a cursor moved forward during the filling operation */
		String currentFieldName = null;
		String currentTable = null;
        
		try{			
			DatabaseTableUtils databaseTableUtils = new DatabaseTableUtils();
		    List<String> tableNames = databaseTableUtils.getTableNamesOrdered();		   
		    
			// 1) obtain a native jdbc connection to use for insert generated data		   
			conn = DatabaseConnectionFactory.getDataSource().getConnection();	
			
			/* 
			 * TIPS: set the mysql variable "wait_timeout" to an high value for long operation if the connection is closed automatically
			 * It is "The number of seconds the server waits for activity on a no interactive connection before closing it" 
			 * 
			 * SET GLOBAL wait_timeout = 28800;  value 1  to 2147483
             * show global variables like 'wait_timeout' ;
			 */
			
			Statement statement = conn.createStatement();
			
			// 1b) If required, prepare the file where write the sql statement(s)
			if(!this.outputSqlFile.equalsIgnoreCase("") && this.outputSqlFile !=null)
			{				
				outputFile = new File(this.outputSqlFile); 
				if(outputFile.exists())
					outputFile.delete();
				
				outputFile.createNewFile(); //create a new file
				fileWriter = new FileWriter(outputFile); 
				out = new BufferedWriter(fileWriter);
				saveToFile = true;
				
				// a fix header message
				out.write("\n\n");
				out.write("---- NOTE: the following query don't have the insert part for the auto-increment field(s) \n");	
			}
			
			// 2 for each table get informations about the fields and the FK constraints (if any)
			for(int i=0; i<tableNames.size(); i++)
			{	
				currentTable = tableNames.get(i);
				
				if(Log4jManager.IS_LOGGING_CONFIGURED)
				   logger.info("----- Starting filling of table: "+currentTable);
				
				if(saveToFile)	{
				   out.write("\n\n");	
				   out.write("---- Table: "+currentTable+" ----"+"\n");	
				   out.flush();
				}   
				
				ArrayList<MetadataTableInfoBean> tableFieldsInfoList = databaseTableUtils.getFieldsInfo(currentTable);				
				// All the FK relations of the current table (ie which table references)
				ArrayList<TableFKconstraintsBean> tableFKinfoList = databaseTableUtils.getFkInformation(currentTable);				
				
				/* Flag to indicates if the current field references another field in a child table */
				boolean fieldHasChild = false;	
				
				/* Start the query Building */
				for(int k=0;k<recordToInsert;k++)
				{					
					/* The current table column names to use in the sql insert query */
					String columnNameList = "";
					
					/* The list of values place in the sql query that we are building */
					String valuesList = "'";
				
					// 3 for each field, depending on his data type choose the right generator type to use
					for(int j=0;j<tableFieldsInfoList.size();j++)
					{					
						MetadataTableInfoBean field = tableFieldsInfoList.get(j);						
						currentFieldName = field.getFieldName();
						
						fieldHasChild = false;	
						
						// the type of the current field ( eg varchar(10) bigint(10) )
						String fieldType = field.getFieldType(); 
						String childColumn = null;
						String childTable = null;
						
						if(Log4jManager.IS_LOGGING_CONFIGURED)
						   logger.debug("Current field: "+currentFieldName +" of Type:"+fieldType);
						
						// check if the current field has child
						for(TableFKconstraintsBean fkInfoList:tableFKinfoList)
						{							
							//true if the current field point another table
							if(fkInfoList.getParentColumnName().equalsIgnoreCase(currentFieldName)) {
								
								fieldHasChild = true;
								
								//get who is the referenced column+table pair
								childColumn = fkInfoList.getReferencedColumnName();								
								childTable = fkInfoList.getReferencedTableName();
							}								
						}	
						
						/**
						 * Note the child table is already filled because our table list is in the filling order
						 */
						if(fieldHasChild && childTable !=null && childColumn !=null)
						{
							if(Log4jManager.IS_LOGGING_CONFIGURED)
							   logger.info("- Table "+currentTable+"("+currentFieldName+") references the Table:"+childTable+"("+childColumn+") ");
							
							/* the fields with "auto_increment" option don't appear in the insert query: is mysql that generate his value */
							if(!field.getExtraInfo().equalsIgnoreCase("auto_increment")) {
							    columnNameList += field.getFieldName()+",";
							
								// Get the allowed values for the current field from the child column
								ArrayList<String> allowedValues = databaseTableUtils.getColumnValue(childTable,childColumn);
								
								String chosenValue = allowedValues.get(k); //use the 'k' index because all the table have the same total row
								valuesList +=chosenValue+"','";
							}
							
						}else {	
							  fieldHasChild = false;  // current field has no child to other tables
							  
							  if(Log4jManager.IS_LOGGING_CONFIGURED)
							     logger.info("The field: "+currentFieldName+" has no references to other tables");
							  
							  //TODO: next release improve the field type above....
							  
							  /* fields with "auto_increment" option don't appear in the insert query: is mysql that generate their value */
							  if(!field.getExtraInfo().equalsIgnoreCase("auto_increment"))
							  { 
								  columnNameList += field.getFieldName()+",";
								   
								  /* PREMISE: the dimension of a some field is not mandatory, depends on the field type */
								  
								  if(fieldType.startsWith("tinyint")){  //tinyint(x) values 0-255 (or -128 to 127)	
									  
									  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case tinyint"); 
									  
									 //String dimension = fieldType.substring(8, fieldType.length() - 1);
									 //Integer.parseInt(dimension);
									 valuesList += smallRandomNumberGenerator.getNexIntValue(127)+"','";
									
								  }else if(fieldType.contains("smallint") || fieldType.contains("SMALLINT")){	//int, smallint, mediumint, bingint
										
									  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case int");
									  
									   //String dimension = fieldType.substring(4, fieldType.length() - 1);
									   //int length = Integer.parseInt(dimension);  
									   //generate a value between 0 and smallint max value, so that all the int types are covered
									   valuesList += randomNumberGenerator.getNexIntValue(32767)+"','";
									   
								  }else if(fieldType.contains("mediumint") || fieldType.contains("MEDIUMINT")){
									  
									  valuesList += randomNumberGenerator.getNexIntValue(8388607)+"','";  //TODO set value as constant field
									  
								  }else if(fieldType.contains("int") || fieldType.contains("INT")){ 
									  
									  valuesList += randomNumberGenerator.getNexIntValue(2147483647)+"','";
								
								  }else if(fieldType.contains("bigint") || fieldType.contains("BIGINT")){  
									  
									  valuesList += randomNumberGenerator.getNexLongValue()+"','"; //truncated vale
									  
								  }else if(fieldType.startsWith("char") || fieldType.startsWith("CHAR")){ 
									
									  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case char");
									  
									  // Extract the right dimension between the ( ) to prevent Data truncated by Mysql
									  String dimension = fieldType.substring(5, fieldType.length() - 1);  //eg CHAR(10)
									  int length = Integer.parseInt(dimension);
									  valuesList += randomWordGenerator.getNextString(length)+"','";
									
								  }else if(fieldType.startsWith("VARCHAR") || fieldType.startsWith("varchar")){
									  
									  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case varchar");
									  
									  String dimension = fieldType.substring(8, fieldType.length() - 1);  //eg VARCHAR(10)
									  int length = Integer.parseInt(dimension);
									  valuesList += randomWordGenerator.getNextString(length)+"','";
									  
								  // NOTE: Invalid DATE, DATETIME, or TIMESTAMP values are converted (by MySql) to the “zero” value of the appropriate type	
								  }else if(fieldType.contains("date") || fieldType.contains("DATE")){ 
									  
									  if(Log4jManager.IS_LOGGING_CONFIGURED)
										logger.trace("* Case date");
									  
									 //date: YYYY-MM-DD,  datetime: YYYY-MM-DD hh:mm:ss, time: hh:mm:ss 
									 valuesList += dateTimeGenerator.getNextDateRandomValue()+"','";
									
								  }else if(fieldType.contains("time") || fieldType.contains("TIME")){ // datetime/timestamp YYYY-MM-DD HH:MM:SS
									  
									 if(Log4jManager.IS_LOGGING_CONFIGURED)
										logger.trace("* Case time");
									  
									 valuesList += dateTimeGenerator.getNextTimeStampRandomValue()+"','";
									
								  }else if(fieldType.contains("enum") || fieldType.contains("ENUM")){ //eg ENUM('val1','val2','val3')
									 
									 if(Log4jManager.IS_LOGGING_CONFIGURED)
										logger.trace("* Case enum"); 
									  
									 // the allowed values picked-up from the ones of the set (same logic used for enum)
									 String allowedValuesList = fieldType.substring(5, fieldType.length() - 1);								
									 String[] values = allowedValuesList.split(",");
									 String value = values[randomInt.nextInt(values.length)].replaceAll("'", "");		
									
									 valuesList += value+"','";	
								  
								  }else if(fieldType.contains("set") || fieldType.contains("SET")){  //eg SET('val1','val2','val3')
									  
									 if(Log4jManager.IS_LOGGING_CONFIGURED)
										logger.trace("* Case set"); 
									  
									 // the allowed values picked-up from the ones of the enum
									 String allowedValuesList = fieldType.substring(4, fieldType.length() - 1);								
									 String[] values = allowedValuesList.split(",");
									 String value = values[randomInt.nextInt(values.length)].replaceAll("'", "");		
										
									 valuesList += value+"','";	
									 
							      }else if(fieldType.contains("text") || fieldType.contains("TEXT")){ //text, mediumtext, longtext,
							    	  
							    	  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case text");
							    	  
							    	  valuesList += randomWordGenerator.getNextString(MAX_TEXT_LENGTH_TEXT_TYPE)+"','";
							    
							       //manage the BLOB type and all his subtype (TINYBLOB, BLOB, MEDIUMBLOB, and LONGBLOB)	  
							      }else if(fieldType.contains("blob") || fieldType.contains("BLOB")){ 
							    	  
							    	  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case blob");
							    	  
							    	  // TODO next release try to insert a sample image or file
							    	  valuesList += "This is a blob data-"+randomInt.nextInt(recordToInsert)+"','";
							    	  
							      }else if(fieldType.contains("decimal") || fieldType.contains("DECIMAL")){  //eg DECIMAL(M,D) must M>D
							    	  
							    	  if(Log4jManager.IS_LOGGING_CONFIGURED)
										logger.trace("* Case decimal");
							    	  
							    	  //For Decimal value (M,D) is mandatory
							    	  String dimension = fieldType.substring(8, fieldType.length() - 1); //(M,D)
							    	  String[] md = dimension.split(",");  
							    	  
							    	  int intPart = randomInt.nextInt(Integer.parseInt(md[0]));
							    	  int decimalPart = randomInt.nextInt(Integer.parseInt(md[1]));						    	 
							    	  
							    	  valuesList += intPart+"."+decimalPart+"','";
							    	  
							      }else if(fieldType.contains("numeric") || fieldType.contains("NUMERIC")){
							    	  
							    	  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case numeric");
							    	  
							    	  valuesList += randomNumberGenerator.getNexDoubleValue(200)+"','";		
							    	  
							      }	else if(fieldType.contains("float") || fieldType.contains("FLOAT") 
							    		     || fieldType.contains("double") || fieldType.contains("DOUBLE")
							    		     || fieldType.contains("real") || fieldType.contains("REAL")) {  //floating point is optional (M,D)
							    	  
							    	  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case float,double,real");							    	  
							    	  
							    	  //calculate the start index depending on the data type 
							    	  int startIndex = 0;
							    	  
							    	  if(fieldType.contains("float") || fieldType.contains("FLOAT"))
							    		  startIndex = 6;
							    	  
							    	  if(fieldType.contains("double") || fieldType.contains("DOUBLE"))
							    		  startIndex = 7;
							    	  
							    	  if(fieldType.contains("real") || fieldType.contains("REAL"))
							    		  startIndex = 5;
							    	  
							    	  // check if (M,D) is present
							    	  if(fieldType.lastIndexOf("(") != -1) // true if (M,D) is present
							    	  {
							    		  String dimension = fieldType.substring(startIndex, fieldType.length() - 1); //(M,D)
								    	  String[] md = dimension.split(",");  
								    	  
								    	  int intPart = randomInt.nextInt(Integer.parseInt(md[0]));
								    	  int decimalPart = randomInt.nextInt(Integer.parseInt(md[1]));
								    	  
								    	  logger.info("int part:"+intPart+" decimal part:"+decimalPart);
								    	  
								    	  valuesList += intPart+"."+decimalPart+"','";
							    	  }else						    	  
							    		  valuesList += randomNumberGenerator.getNexDoubleValue(100)+"','";	
							    	  
							      } else if(fieldType.contains("bit") || fieldType.contains("BIT")){ //eg BIT(M)
							    	  
							    	  if(Log4jManager.IS_LOGGING_CONFIGURED)
										 logger.trace("* Case bit");
							    	  
							    	  String dimension = fieldType.substring(4, fieldType.length() - 1);  //eg BIT(10) means 10 bit value b'1101'
							    	  String by = Integer.toBinaryString(randomInt.nextInt(recordToInsert));
							    	  
							    	  if(by.length() > dimension.length())
							    		 by = by.substring(0, fieldType.length());
							    	  
							    	  valuesList +="b\'"+by+"\'"+"','";
							    	  
							      } else if(fieldType.contains("year") || fieldType.contains("YEAR")){
							    	 
							    	 if(Log4jManager.IS_LOGGING_CONFIGURED)
									   logger.trace("* Case year"); 
							    	  
							    	 valuesList +=randomNumberGenerator.getNexIntValue(55)+"','";
							    	  
							      }	else{
							    	  
							    	  if(Log4jManager.IS_LOGGING_CONFIGURED)
							    		 logger.fatal("!! ATTENTION !!, no suitable generator for field of type: "+fieldType);							    	  
							      }								
					        }						
				      }  					
			   }
					
			  // 4 compose the query				
			  String query = "INSERT INTO "+currentTable+" ("+columnNameList.substring(0, columnNameList.length() - 1)+") VALUES ("+valuesList.substring(0, valuesList.length() - 2)+")";
			 
			  if(Log4jManager.IS_LOGGING_CONFIGURED)
			     logger.debug("INSERT QUERY: "+query);	
					
			  if(saveToFile){
				 out.write(query+";\n");
			     out.flush();
			  } 	
			  statement.addBatch(query);							
		  }			
				
		  statement.executeBatch();
		  totalTableFilled++;
		
		  if(Log4jManager.IS_LOGGING_CONFIGURED)
		     logger.info("--------- Table: "+currentTable+" filled successfully");
	   }	
			
			if(saveToFile)
			   out.close();		
			
			// at this point (if no error) all table are filled
			
		}catch (Exception e) {	
			
			if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
		 	   logger.fatal("Error filling the database, cause: ",e);
			
			throw new Exception("\n Database can be filled partially \n Problem in Table: "+currentTable+"  Column: "+currentFieldName+" \n Cause: "+e.getMessage() +"\n * Clean the database before retry *");			
			
		}finally{			
			try {
				if(conn != null)
				   conn.close();
			} catch (SQLException e) {				
				throw e;
			}	
		}
		return totalTableFilled;			
	}

 }
