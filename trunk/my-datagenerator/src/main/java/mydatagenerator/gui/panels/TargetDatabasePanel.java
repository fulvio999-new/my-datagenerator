
package mydatagenerator.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import mydatagenerator.core.database.operations.DatabaseTableUtils;
import mydatagenerator.core.database.operations.utility.DataPumpDatabaseConnection;
import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.core.database.operations.utility.DatabaseConnectionTester;
import mydatagenerator.gui.utils.filefilter.FolderFileFilter;
import mydatagenerator.init.Log4jManager;
import net.miginfocom.swing.MigLayout;

/**
 * Create the panel where the user set the target database and test the connection to it
 *
 */
public class TargetDatabasePanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(TargetDatabasePanel.class);
	
	/* The frame that own this panel  */
	private JFrame mainFrame;
	
	private JLabel driverLabel;
	private JTextField driverValue;
	
	private JLabel dbHostLabel;
	private JTextField dbHostValue;
	
	private JLabel dbPortLabel;
	private JTextField dbPortValue;
	
	private JLabel dbNameLabel;
	private JTextField dbNameValue;
	
	private JLabel dbUserLabel;
	private JTextField dbUserValue;
	
	private JLabel dbPasswordLabel;
	private JTextField dbPasswordValue;
	
	/* A label to show the test connection result */
	private JLabel operationResultLabel;
	
    /* Execute a dummy query versus the configured target DB */
	private JButton setDatabaseButton;
	
	private JButton closebutton;
	
	/* The dedicated object to the the DB connection*/
	private DatabaseConnectionTester databaseConnectionTester;
	
	/* Sub panel with the log configurations options to trace the operations execute for debug purpose */
	private LogConfigurationPanel logConfigurationPanel;
	

	/**
	 * Constructor
	 */
	public TargetDatabasePanel(JFrame mainFrame) {
		
		databaseConnectionTester = new DatabaseConnectionTester();
		
		this.mainFrame = mainFrame;		
		this.setLayout(new MigLayout("wrap 4")); //we want 4 column
		this.setBorder(BorderFactory.createTitledBorder("Set Target Database"));
		
		operationResultLabel = new JLabel("");
		operationResultLabel.setForeground(Color.GREEN);
		operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
		
		driverLabel = new JLabel("* Driver:");
		driverValue = new JTextField();
		driverValue.setEditable(false); 
		driverValue.setEnabled(false); 
		driverValue.setText("com.mysql.jdbc.Driver"); //only mysql databases are supported
		
		dbNameLabel = new JLabel("* Name:");
		dbNameValue = new JTextField(""); 
		
		dbHostLabel = new JLabel("* Host:");
		dbHostValue = new JTextField("localhost");
		
		dbPortLabel = new JLabel("* Port:");
		dbPortValue = new JTextField("3306"); 
		
		dbUserLabel = new JLabel("* User:");
		dbUserValue = new JTextField("root");
		
		dbPasswordLabel = new JLabel("Password:");
		dbPasswordValue = new JTextField();
				
		setDatabaseButton = new JButton("Set Database");
		setDatabaseButton.addActionListener(this);
		
		logConfigurationPanel = new LogConfigurationPanel(this);
		logConfigurationPanel.setVisible(true);
		
		closebutton = new JButton("Close");
		closebutton.addActionListener(this);
		
		//----- Add the components to the Panel
		this.add(driverLabel,"gapleft 20");
		this.add(driverValue,"width 100:200:500,wrap,gapbottom 10");
		
		this.add(dbNameLabel,"gapleft 20");
		this.add(dbNameValue,"width 100:200:500,wrap,gapbottom 10");
		
		this.add(dbHostLabel,"gapleft 20");
		this.add(dbHostValue,"width 300:300:500");
		
		this.add(dbPortLabel,"gapleft 20,align right");
		this.add(dbPortValue,"width 60,gapbottom 10");
		
		this.add(dbUserLabel,"gapleft 20");
		this.add(dbUserValue,"width 300:300:500");
		
		this.add(dbPasswordLabel,"gapleft 70");
		this.add(dbPasswordValue,"width 500,growx");	//min:preferred:max
		
		this.add(new JLabel(""),"span 4,gapbottom 30"); //placeholder			
		
		this.add(new JLabel("* Required field"),"span 3,gapleft 20");
		this.add(setDatabaseButton,"align right,width 120");
		
		this.add(operationResultLabel,"span 4,align center,gapbottom 10");
		
		this.add(logConfigurationPanel,"span 4,growx"); //
		
		this.add(new JLabel(""),"span 3"); //placeholder	
		this.add(closebutton,"align right,width 120");
	}


	/**
	 * Manage the actions on the buttons placed on this panel and in the sub-panel LogConfigurationPanel
	 */
	public void actionPerformed(ActionEvent e) {		

		final JFileChooser destinationLogFileChooser = new JFileChooser(); //choose out folder for the log file
		
	    if (e.getSource() instanceof JButton)  
	    {	 
	    	 DatabaseConnectionFactory dsf = new DatabaseConnectionFactory();
	    	
	    	 try{     	
	    	
	    		/* Set the target database and initialize the logging system (if the user has enabled it) */	
	            if (e.getActionCommand().equals("Set Database"))
	            {		    	   
		    	  String driver = this.driverValue.getText().trim();
		    	  String host = this.dbHostValue.getText().trim();
		    	  String port = this.dbPortValue.getText().trim();	
		    	  String dbname = this.dbNameValue.getText().trim();
		    	  String user = this.dbUserValue.getText().trim();
		    	  String password = this.dbPasswordValue.getText().trim();
		    		  
		    	  if(host.equalsIgnoreCase("") || port.equalsIgnoreCase("") || dbname.equalsIgnoreCase("") || user.equalsIgnoreCase(""))
		    		 throw new Exception("Invalid input !");
		    	   
		    	  DataPumpDatabaseConnection dataPumpDatabaseConnection = new DataPumpDatabaseConnection(driver,host,port,dbname,user,password);	    	      	  
		    	  dsf.initializeFactory(dataPumpDatabaseConnection);		  
		    	   
	    		  operationResultLabel.setText(""); //clean old messages    		   
    		     			   
    			  databaseConnectionTester = new DatabaseConnectionTester();
    	    	  databaseConnectionTester.testConnection();
    	    	  
    	    	  operationResultLabel.setForeground(Color.GREEN);	      		  
	    		  operationResultLabel.setText("Test Connection executed: Target DB set successfully !"); 
	    		  
	    		  /* Clean some old table names list (ie the list is a static field) about a previous target DB  */
	    		  DatabaseTableUtils.setTableNamesList(null);    		    
	            }
	       
	           if (e.getActionCommand().equals("Close")) 
	           {
	    	      if (mainFrame.isDisplayable()) {   
	    		     DatabaseConnectionFactory.closeDataSource();
     		         mainFrame.dispose();
                  }
	           }
	       
	         /* ----- Manage the action coming from the sub-panel LogConfigurationPanel ----- */	
	       
			 /* Choose the destination log file */		
			 if (e.getActionCommand().equals("Browse"))
		     {
		        destinationLogFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        destinationLogFileChooser.setFileFilter(new FolderFileFilter());
		        destinationLogFileChooser.setDialogTitle("Choose the output folder the log file");	        	
	             
	            int value = destinationLogFileChooser.showOpenDialog(this);
	            
	            // Return value if approved (ie yes, ok) is chosen.
	            if (value == JFileChooser.APPROVE_OPTION)
	            {	             
	               File  destinationFolder = destinationLogFileChooser.getSelectedFile();	              
	               String  pathDestinationFolder = destinationFolder.getAbsolutePath(); 	             
	               this.logConfigurationPanel.getOutLogFileField().setText(pathDestinationFolder); 
		         }           
		      }
			
		     /* Initialize and Enable the logging system */	
		     if(e.getActionCommand().equals("Enable"))
		     {			 
			     if(this.logConfigurationPanel.getEnableLogCheckBox().isSelected() && !this.logConfigurationPanel.getOutLogFileField().getText().equalsIgnoreCase(""))
			     {
			        Log4jManager log4jManager = new Log4jManager();	 
			    		 
			    	//set some logging system properties: the chosen path to the output log file and the log level
			    	String logLevel = (String) this.logConfigurationPanel.getLogLevelCombo().getSelectedItem();
			    	log4jManager.initializeLogging(this.logConfigurationPanel.getOutLogFileField().getText()+File.separator+Log4jManager.OUTPUT_FILENAME,logLevel);
			    	
			    	this.logConfigurationPanel.getMessageLabel().setForeground(Color.GREEN);
			    	this.logConfigurationPanel.getMessageLabel().setFont(new Font("Serif", Font.BOLD, 15));
			    	this.logConfigurationPanel.getMessageLabel().setText("Logging enabled successfully !");
			    	
			      }else{
			    	 this.logConfigurationPanel.getMessageLabel().setForeground(Color.RED);
				     this.logConfigurationPanel.getMessageLabel().setFont(new Font("Serif", Font.BOLD, 15));
				     this.logConfigurationPanel.getMessageLabel().setText("Invalid output folder"); 
			      }
		      }
		     
	       }catch (Exception ex) {	
  			   
   			  	if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
		           logger.info("Error connecting with DB or configuring logging, cause: ",ex);
  			   
   			    operationResultLabel.setForeground(Color.RED);
	    		operationResultLabel.setText(ex.getMessage());
	    		   
	    		/* Clean some old table names list (ie the list is a static field) about the previous target DB  */
	    		DatabaseTableUtils.setTableNamesList(null);
	    		dsf.initializeFactory(null);
			 } 
	    }	    
	    
	    if(e.getSource() instanceof JCheckBox)
	    {  
	    	if(e.getActionCommand().equalsIgnoreCase("Enable Disable Log"))
			{
			    if(this.logConfigurationPanel.getEnableLogCheckBox().isSelected())
				{
			       this.logConfigurationPanel.getOutLogFileField().setEnabled(true);
			       this.logConfigurationPanel.getChooseOutLogFileButton().setEnabled(true);
			       this.logConfigurationPanel.getLogLevelCombo().setEnabled(true);	
			       this.logConfigurationPanel.getConfirmEnableLogButton().setEnabled(true);	
			       this.logConfigurationPanel.getMessageLabel().setText("");
			       
				}else{
					
			       this.logConfigurationPanel.getOutLogFileField().setEnabled(false);
			       this.logConfigurationPanel.getChooseOutLogFileButton().setEnabled(false);
			       this.logConfigurationPanel.getLogLevelCombo().setEnabled(false);
			       this.logConfigurationPanel.getConfirmEnableLogButton().setEnabled(false);
			       this.logConfigurationPanel.getMessageLabel().setText("");
				}
			}
	    }
	}

}
