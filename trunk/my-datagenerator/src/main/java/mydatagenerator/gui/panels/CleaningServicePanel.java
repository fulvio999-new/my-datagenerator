
package mydatagenerator.gui.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import mydatagenerator.core.database.operations.DatabaseCleaner;
import mydatagenerator.core.database.operations.DatabaseTableUtils;
import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.gui.panels.common.BusyLabelPanel;
import mydatagenerator.init.Log4jManager;
import net.miginfocom.swing.MigLayout;

/**
 * Create a panel with the database cleaning functionality
 *
 */
public class CleaningServicePanel extends JPanel implements ActionListener{	
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(CleaningServicePanel.class);
	
	/* The frame that own this panel  */
	private JFrame mainFrame;
	
    private JButton closebutton;	
    private JButton cleanDatabaseButton;
    
    /* Sub panel where the user can enable the cleaning of the archive tables by changing temporary his engine type to MyIsam or InnoDB */
    private CleanArchiveTableSubPanel cleanArchiveTableSubPanel;
    
    /* A label to show the operation result */
	private JLabel operationResultLabel;
	
	private JLabel fixLabel;
	
	/* An output text area where show some log and stats information */
	private JTextArea logOutputTextArea;
	private JScrollPane logOutputScrollPanel;
	
	/* A sub-panel with an animation to indicates that a processing is in action */
    private BusyLabelPanel busyLabelPanel;

	/**
	 * Constructor
	 */
	public CleaningServicePanel(JFrame mainFrame) {	
		
		this.mainFrame = mainFrame;			
		this.setLayout(new MigLayout("wrap 2")); //we want 4 column
		this.setBorder(BorderFactory.createTitledBorder("Clean the Target Database"));
		
		operationResultLabel = new JLabel("");
		fixLabel = new JLabel("Operation Log Output:");			
		
		logOutputTextArea = new JTextArea();
		logOutputTextArea.setEditable(false);
		logOutputTextArea.setBorder(BorderFactory.createLineBorder(Color.black));		
		
		// Note: don't use the "add" method to add a component on the scroll panel to prevent textarea problems
		logOutputScrollPanel = new JScrollPane(logOutputTextArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		busyLabelPanel = new BusyLabelPanel();
		busyLabelPanel.getJxBusyLabel().setBusy(false);
		
		cleanDatabaseButton = new JButton("Clean Database");
		cleanDatabaseButton.addActionListener(this);
				 
		cleanArchiveTableSubPanel = new CleanArchiveTableSubPanel(this);			
		
		closebutton = new JButton("Close");
		closebutton.addActionListener(this);
		
		//------ Add the component to the panel
        this.add(operationResultLabel,"span 2,align center");
 		
        this.add(new JLabel(""),"span 2"); //placeholder
		this.add(fixLabel,"wrap");
		
		this.add(logOutputScrollPanel,"span 2,width 820,height 300,align center");
		
		this.add(cleanArchiveTableSubPanel,"span 5,growx");
		
		this.add(new JLabel(""),"span 1"); //placeholder
		this.add(cleanDatabaseButton,"align right,gapbottom 50");		
		
		this.add(busyLabelPanel,"span 2 2, align center,gapbottom 75");			
		
		this.add(new JLabel(""),"span 1"); //placeholder
		this.add(closebutton,"width 120,align right");
	}

	/**
	 * Manage the actions on the buttons
	 */
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() instanceof JButton)  
	    {			
			if (e.getActionCommand().equals("Clean Database")) 
		    {
				if(Log4jManager.IS_LOGGING_CONFIGURED)
					logger.info("--------- Cleaning Service panel --------");
				
		       	if (!busyLabelPanel.getJxBusyLabel().isEnabled())
		       	{
		       	   busyLabelPanel.getJxBusyLabel().setEnabled(true);		       	  
		       	}
		       	
		       	if (busyLabelPanel.getJxBusyLabel().isBusy()) {	   //true if task finished   	    
		       	    busyLabelPanel.getJxBusyLabel().setBusy(false);
		       	    busyLabelPanel.getJxBusyLabel().setVisible(false);		       	   
		       	   
		       	}else {	        	    
		       	      busyLabelPanel.getJxBusyLabel().setBusy(true);
		       	      busyLabelPanel.getJxBusyLabel().setVisible(true); 
		       	      
		       	      // The cleaning DB task can be long: create a dedicated Thread to perform it  		       	   
				      Thread cleanDatabaseThread = new Thread() {
					                    
					   public void run() {					                       
					        
					       try{
					    	     logOutputTextArea.setText(""); //clean old output
					    	     
					    	     // flag to indicates if the user want clean also the ARCHIVE tables
					    	     boolean cleanArchiveTable = cleanArchiveTableSubPanel.getEnableArchiveCleaningCheckBox().isSelected();
					    	   
					     		 DatabaseCleaner databaseCleaner = new DatabaseCleaner(); 					    	   
					     		 DatabaseTableUtils databaseTableUtils = new DatabaseTableUtils();
					    	     
					    	     /* the TOTAL tables to clean */
					    	     int tableToClean = databaseTableUtils.getTableCount();
					    	     
					    	     logOutputTextArea.setBorder(BorderFactory.createLineBorder(Color.black)); //reset some error
					     		 logOutputTextArea.setText("Database analisys started...\n");
					     		 logOutputTextArea.append("Total database tables: "+tableToClean+"\n");
					     		 
					     		 /* ALL the database tables */
					     		 List<String> allTables = databaseTableUtils.getTableNamesOrdered();
					    	     
					     		 /* the tables with 'ARCHIVE' engine type */
					    	     List<String> archiveTables = databaseTableUtils.getArchiveTables();	    
					    	     
					    	     //the tables without the Archive type ones
					    	     List<String> effectiveTablesToClean = new ArrayList<String>();					    	     
					    	     
					     		 long startTime = 0;
					     		 long endTime = 0;
					     		 
					     		 logOutputTextArea.append("Checking the presence of tables with unsupported store engine (ie 'ARCHIVE')...\n");
					     		 
					    	     if(!archiveTables.isEmpty()) // found Archive tables
					    	     {	
					    	    	 if(cleanArchiveTable)
					    	    	 {	
					    	    		 if(Log4jManager.IS_LOGGING_CONFIGURED)
							 				logger.info("User want clean also the ARCHIVE tables");
					    	    		 
					    	    	     logOutputTextArea.append("* ATTENTION * Found "+archiveTables.size()+" tables with 'ARCHIVE' engine: trying to clean them changing engine type... \n");
					    	    	     logOutputTextArea.append("Total tables to clean: "+tableToClean+"\n");
					    	    	     
					    	    	     // change the engine type of the ARCHIVE tables
					    	    	     for(int i=0;i<archiveTables.size();i++)
					    	    	     {					    	    	    	
					    	    	    	 databaseTableUtils.changeTableEngine(archiveTables.get(i),"InnoDB");
					    	    	    	 
					    	    	    	 if(Log4jManager.IS_LOGGING_CONFIGURED)
									 			logger.debug("Changed the engine type of table "+archiveTables.get(i)+" to InnoDB");
					    	    	     }
					    	    	     
					    	    	     // clean ALL the tables
					    	    	     startTime = System.currentTimeMillis();						    	     
					    	    	     databaseCleaner.deleteAll(allTables.toArray(new String[]{}));						    	    
					    	    	     endTime = System.currentTimeMillis();
					    	    	     
					    	    	 }else{	  // User don't want clean Archive tables 
					    	    		 
					    	    		 if(Log4jManager.IS_LOGGING_CONFIGURED)
							 				logger.info("The user DON'T want clean the ARCHIVE tables");
					    	    		 
					    	    	     logOutputTextArea.append("* ATTENTION * Found "+archiveTables.size()+" tables with 'ARCHIVE' engine: they will not be cleaned (See Mysql doc) !\n");
					    	    	     logOutputTextArea.append("Total tables cleanable: "+(tableToClean-archiveTables.size())+"\n");
						    	    	 
					    	    	     logOutputTextArea.append("Database cleaning started...\n");
					    	    	 
					    	    	     // remove tables with a storage engine that don't support the sql 'delete' operation (ie 'ARCHIVE' engine )
					    	    	     for(int i=0;i<allTables.size();i++)
					    	    	     {
					    	    	    	if(!archiveTables.contains(allTables.get(i)))
					    	    	    		effectiveTablesToClean.add(allTables.get(i));	 
					    	    	     }
						    	     
					    	    	     // clean only the NON-ARCHIVE tables
					    	    	     startTime = System.currentTimeMillis();						    	     
					    	    	     databaseCleaner.deleteAll(effectiveTablesToClean.toArray(new String[]{}));						    	    
					    	    	     endTime = System.currentTimeMillis();						    	     
					    	    	 }
						    	     
					    	     }else{		
					    	    	  logOutputTextArea.append("No tables found with 'ARCHIVE' engine \n");
					    	    	  logOutputTextArea.append("Total tables cleanable: "+allTables.size()+"\n");
					    	    	  logOutputTextArea.append("Database cleaning started...\n");
					    	    	 
					    	    	  startTime = System.currentTimeMillis();					    	    	
						     		  databaseCleaner.deleteAll(allTables.toArray(new String[]{}));						     				    
						     		  endTime = System.currentTimeMillis();					    	    	 
					    	     }	
					    	     
					    	     /*  Restore the previously modified engine type from InnoDB to ARCHIVE  */
					    	     if(cleanArchiveTable)
					    	     {					    	    	
					    	    	 logOutputTextArea.append("Restoring the engine type to ARCHIVE...\n");					    	    	 
					    	    	
				    	    	     for(int i=0;i<archiveTables.size();i++)
				    	    	     {					    	    	    	
				    	    	    	databaseTableUtils.changeTableEngine(archiveTables.get(i),"ARCHIVE");
				    	    	    	 
				    	    	    	if(Log4jManager.IS_LOGGING_CONFIGURED)
								 		   logger.debug("Restored the engine type to ARCHIVE for table "+archiveTables.get(i));
				    	    	     }
					    	     }
					    	     
					    	     if(Log4jManager.IS_LOGGING_CONFIGURED)
					    	    	logger.debug("Database cleaning finished successfully in: [" + (endTime - startTime) + "] ms");
					     					   
					     		 logOutputTextArea.append("Database cleaning finished successfully in: [" + (endTime - startTime) + "] ms \n");
					     		 logOutputTextArea.setBorder(BorderFactory.createLineBorder(Color.green));
					     		
					     		 // stop and hide the animation
					     		 busyLabelPanel.getJxBusyLabel().setBusy(false);
					   		     busyLabelPanel.getJxBusyLabel().setVisible(false);
					     					    
					           }catch (Exception ex) {
					        		
					    	    	if(Log4jManager.IS_LOGGING_CONFIGURED)
					    	    	   logger.fatal("ERROR cleaning the Database:",ex);
					        	    
					                logOutputTextArea.setBorder(BorderFactory.createLineBorder(Color.red));	
									logOutputTextArea.setText("ERROR cleaning the Database: "+ex.getMessage()+"\n"); 
									
									// stop and hide the animation
									busyLabelPanel.getJxBusyLabel().setBusy(false);
						   		    busyLabelPanel.getJxBusyLabel().setVisible(false);	
					           }
		                 }
					 };
					                
					cleanDatabaseThread.start(); 
		      	}  
		    }			
			
			// Close the connection pool before close the application
			if (e.getActionCommand().equals("Close")) 
		    {
		      if (mainFrame.isDisplayable()) { 
		    	  DatabaseConnectionFactory.closeDataSource();
	     	      mainFrame.dispose();
	          }
		   }
	    }	
		
	}

	
	public JTextArea getLogOutputTextArea() {
		return logOutputTextArea;
	}

	public void setLogOutputTextArea(JTextArea logOutputTextArea) {
		this.logOutputTextArea = logOutputTextArea;
	}

	public BusyLabelPanel getBusyLabelPanel() {
		return busyLabelPanel;
	}

	public void setBusyLabelPanel(BusyLabelPanel busyLabelPanel) {
		this.busyLabelPanel = busyLabelPanel;
	}

}
