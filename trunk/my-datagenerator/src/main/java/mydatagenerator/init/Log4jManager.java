
package mydatagenerator.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Class that initialize the log4j system used by the application and customize the configuration with some choice of the user (ie output log file)
 *
 */
public class Log4jManager {
	
	private final static Logger logger = Logger.getLogger(Log4jManager.class);

	private  Properties configProperties;
	
	/*
	 * Flag to indicates to others class if the logging is configured and enabled (ie the user want use it)
	 * the other classes check this flag before logging messages to prevent out message if the user don't want to the logging 
	 * */
	public static boolean IS_LOGGING_CONFIGURED = false; 
	
	/* The output log file name */
	public final static String OUTPUT_FILENAME = "mydatagenerator.log";

	/**
	 * Constructor
	 */
	public Log4jManager() {		
		this.configProperties = new Properties();
	}
	
	/**
	 * Initialize the logging system
	 * @param outFile The full path to the chosen output log file 
	 * @param logLevel The chosen level of the logger named "USER" already configured in the properties file (ie DEBUG, FATAL...) 
	 * @throws IOException If some error happen 
	 */
	public void initializeLogging(String outFile, String logLevel) throws IOException{
		
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("log4j.properties"); 			
		configProperties.load(is);
		// customize the basic properties file with the choice of the user
		configProperties.setProperty("log4j.appender.USER.File",outFile);
		configProperties.setProperty("log4j.logger.mydatagenerator",logLevel+",USER");
		
		PropertyConfigurator.configure(configProperties);
				
		logger.info("Logging System initialized successfully !");
		
		IS_LOGGING_CONFIGURED = true;
	}
	

	public Properties getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(Properties configProperties) {
		this.configProperties = configProperties;
	}

}
