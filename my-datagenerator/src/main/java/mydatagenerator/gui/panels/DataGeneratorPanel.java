
package mydatagenerator.gui.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import mydatagenerator.core.database.generator.DataGenerator;
import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.gui.panels.common.BusyLabelPanel;
import mydatagenerator.gui.utils.filefilter.CustomOutputFileFilter;
import mydatagenerator.init.Log4jManager;
import net.miginfocom.swing.MigLayout;

/**
 * Create a panel where the user can set the pump options: 
 * - how many entry insert
 * - if perform a clean insert o append at the existing data
 * 
 * - Use a global setting for all the tables
 *
 */
public class DataGeneratorPanel extends JPanel implements ActionListener{	
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(DataGeneratorPanel.class);
	
	/* A fix title */
	private JLabel titleMessage;
	
	/* An output text area where show some log and stats information */
	private JTextArea logOutputTextArea;
	private JScrollPane logOutputScrollPanel;
	
	/* A sub-panel with an animation to indicates that a processing is in action */
    private BusyLabelPanel busyLabelPanel;

	private JButton closeButton;
	private JButton startGenerationButton;
	
	private JFrame mainFrame;
	
	/* The spinner where the user can set how many row insert in the ALL the tables */
	private JSpinner recordToInsertSpinner;
	
	private JLabel recordSpinnerLabel;
	
	/* Numeric Type */
	private JLabel numericTypeLabel;
	private static String[] numericTypeGeneratorList = {"Random-Number"};
	private JLabel numericTypeGeneratorLabel;
	private JComboBox numericTypeGeneratorListCombo;
	
	/* String Type */	
	private JLabel stringTypeLabel;
	private static String[] stringTypeGeneratorList = {"Random-Word"};
	private JLabel stringTypeGeneratorLabel;
	private JComboBox stringTypeGeneratorListCombo;
	
	/* Date & Time */
	private JLabel datetimeTypeLabel;
	private static String[] datetimeTypeGeneratorList = {"Random"};
	private JLabel datetimeTypeGeneratorLabel;
	private JComboBox datetimeTypeGeneratorListCombo;
	
	/* create a sql file containing the insert instructions with the random data generated */
	private JCheckBox generateSqlfileCheckBox;
	private JTextField outputSqlFile;
	private JButton browseButton;
	
	/* Value placed in a configuration properties file to have custom builds */
	private int spinnerMaxValue;
	
	/* constant value not configurable */
	private static int SPINNER_MIN_VALUE = 1;	
	private static int SPINNER_INITIAL_VALUE = 1;
	private static int SPINNER_STEP_SIZE = 1;

	/**
	 * Constructor
	 */
	public DataGeneratorPanel(JFrame mainFrame) {
		
		/* Load the configuration properties */
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("MyDatagenerator-conf.properties"); 	
		Properties config = new Properties();
		
		try {
			config.load(is);
			spinnerMaxValue = Integer.parseInt(config.getProperty("max_record_for_table"));
			
		} catch (IOException e) {
			
			//apply the default configuration values
			if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
		 	   logger.fatal("Error loading the configuration file, apply the default values, cause: ",e);
		}
		
		this.setLayout(new MigLayout("wrap 5")); //we want 5 column
		this.setBorder(BorderFactory.createTitledBorder("Data Generator Configuration"));		
		this.mainFrame = mainFrame;
		
		this.titleMessage = new JLabel("<html><b>NOTE:</b> <ul> <li>Database should be empty before start the generator (use the cleaning service) to prevent duplication error</li> " +
										"<li>If a partitioned table has no partition for the random value the current version of the generator will fail!</li> " +
										"<li>Restart the application before retry a filling activity, to reset the random generators.</li>" +
										"</ul></html>");
		
		SpinnerModel model = new SpinnerNumberModel(SPINNER_INITIAL_VALUE,SPINNER_MIN_VALUE,spinnerMaxValue,SPINNER_STEP_SIZE);		
		
		recordToInsertSpinner = new JSpinner(model);
		// get the default editor of the spinner and disable it to prevent editing from keyboard
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) recordToInsertSpinner.getEditor();
		editor.getTextField().setEnabled(true);
		editor.getTextField().setEditable(false);		
		recordSpinnerLabel = new JLabel("Number of row to insert (max "+spinnerMaxValue+"):");
		
		// Numeric Type fields
		numericTypeGeneratorLabel = new JLabel("Generator Pattern:");		
		numericTypeLabel = new JLabel("<html><b>Numeric configuration</b></html>");
		
		// String Type fields 
		stringTypeLabel = new JLabel("<html><b>String configuration</b></html>");
		stringTypeGeneratorLabel = new JLabel("Generator Pattern:");	
		
		// Date & Time fields
		datetimeTypeLabel = new JLabel("<html><b>Date & Time configuration</b></html>");
		datetimeTypeGeneratorLabel = new JLabel("Generator Pattern:");	
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		
		startGenerationButton = new JButton("Start Generation");
		startGenerationButton.addActionListener(this);
		
		generateSqlfileCheckBox = new JCheckBox("Generate SQL file");
		generateSqlfileCheckBox.setActionCommand("saveToFile");		
		generateSqlfileCheckBox.addActionListener(this);
		
		outputSqlFile = new JTextField();
		outputSqlFile.setEnabled(false);
		
		browseButton = new JButton("Browse");
		browseButton.setEnabled(false);
        browseButton.addActionListener(this);
		
		busyLabelPanel = new BusyLabelPanel();
		busyLabelPanel.getJxBusyLabel().setBusy(false);		
		
		logOutputTextArea = new JTextArea();
		logOutputTextArea.setEditable(false);
		logOutputTextArea.setBorder(BorderFactory.createLineBorder(Color.black));		
		
		// Note: don't use the "add" method to add a component on the scroll panel to prevent textarea problem
		logOutputScrollPanel = new JScrollPane(logOutputTextArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//----- Add the components to the panel -----
		this.add(titleMessage,"span 5, align left");
		
		this.add(recordSpinnerLabel);
		this.add(recordToInsertSpinner,"wrap");
		
		this.add(new JSeparator(SwingConstants.HORIZONTAL),"span 5, growx");
		
		// Numeric Type
		numericTypeGeneratorListCombo = new JComboBox(numericTypeGeneratorList);		
		numericTypeGeneratorListCombo.setActionCommand("numericType"); //set the action command to include in the Event
		numericTypeGeneratorListCombo.addActionListener(this);
		
		this.add(numericTypeLabel,"wrap");
		this.add(numericTypeGeneratorLabel);
		this.add(numericTypeGeneratorListCombo,"wrap");		
		
		this.add(new JSeparator(SwingConstants.HORIZONTAL),"span 5, growx");
		
		// String value
		stringTypeGeneratorListCombo = new JComboBox(stringTypeGeneratorList);		
		stringTypeGeneratorListCombo.setActionCommand("stringType"); //set the action command to include in the Event
		stringTypeGeneratorListCombo.addActionListener(this);
		
		this.add(stringTypeLabel,"wrap");
		this.add(stringTypeGeneratorLabel);
		this.add(stringTypeGeneratorListCombo,"wrap");
		
		this.add(new JSeparator(SwingConstants.HORIZONTAL),"span 5, growx");
		
		// Date & Time
		datetimeTypeGeneratorListCombo = new JComboBox(datetimeTypeGeneratorList);		
		datetimeTypeGeneratorListCombo.setActionCommand("datetimeType"); //set the action command to include in the Event handler
		datetimeTypeGeneratorListCombo.addActionListener(this);
		
		this.add(datetimeTypeLabel,"wrap");
		this.add(datetimeTypeGeneratorLabel);
		this.add(datetimeTypeGeneratorListCombo,"wrap");		
		
		this.add(generateSqlfileCheckBox);
		this.add(outputSqlFile,"span 3,width 450,growx");
		this.add(browseButton,"width 130, wrap");
		
		this.add(busyLabelPanel,"span 5 2, align center,gapbottom 30");	
		
		this.add(logOutputScrollPanel,"span 5,width 820,height 600");
		
		this.add(new JLabel(),"span 4");
		this.add(startGenerationButton,"split 2,align right");
		this.add(closeButton,"width 120,align right");
	}

	/**
	 * Manage the event on the button
	 */
	public void actionPerformed(ActionEvent e) {
		
		final JFileChooser destinationSqlFileChooser;  
		
		/* set the minimum row to insert (at least one) */
		int recordToInsert = 1;
		String outputFile = null;
		
		/* The generators type chosen by the user */
		String numericGeneratorType = null;
		String stringGeneratorType = null;
		String datetimeGeneratorType = null;		
		
		/* Detect the changed value on the combo box to get the chosen generator type
		   and display some configuration options for the chosen generator
		  !!!!! CURRENTLY NOT USED !!!!!
		   
			if (e.getSource() instanceof JComboBox)  
		    {
				// get the action command to understand which is the combo-box
				String actionCommand = e.getActionCommand();
				//System.out.println("COMMAND: "+actionCommand);
				
				if(actionCommand.equalsIgnoreCase("numericType")){
					numericGeneratorType = (String) this.numericTypeGeneratorListCombo.getSelectedItem();				
					
					//set visible an option panel for the chosen generator
									
				}else if(actionCommand.equalsIgnoreCase("stringType")){
					stringGeneratorType = (String) this.stringTypeGeneratorListCombo.getSelectedItem();				
					
				}else if(actionCommand.equalsIgnoreCase("datetimeType")){
					datetimeGeneratorType = (String) this.datetimeTypeGeneratorListCombo.getSelectedItem();				
				}		   
		    }	
       */ 
       
        /* Manage the actions on the JCheckBox: enable/disable the sql output file */
		if (e.getSource() instanceof JCheckBox)  
	    {
			if(e.getActionCommand().equals("saveToFile"))
			{			
				if(generateSqlfileCheckBox.isSelected())
				{
				   outputSqlFile.setEnabled(true);
				   browseButton.setEnabled(true);
				}else{
					outputSqlFile.setEnabled(false);
				    browseButton.setEnabled(false);
				}    
			}				
	    }
		
		/* Manage the actions on the buttons */
		if (e.getSource() instanceof JButton)  
	    {
			/* Close the application */
			if (e.getActionCommand().equals("Close")){
				
				if (mainFrame.isDisplayable()) { 
					DatabaseConnectionFactory.closeDataSource();
		     	    mainFrame.dispose();
		        }
			}	
			
			/* Choose the folder/file where save the sql file */
			if (e.getActionCommand().equals("Browse"))
			{				
				destinationSqlFileChooser = new JFileChooser();
				destinationSqlFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				destinationSqlFileChooser.setDialogTitle("Specify file and path to store the sql statements");
				destinationSqlFileChooser.setAcceptAllFileFilterUsed(false); //disable the default fileChooserFilter
				destinationSqlFileChooser.setFileFilter(new CustomOutputFileFilter()); //only sql or txt files are allowed
	              
	            int value = destinationSqlFileChooser.showOpenDialog(this);
	            
	            // Return value if approve (ie yes, ok) is chosen.
	            if (value==JFileChooser.APPROVE_OPTION)
	            {
	               File f = destinationSqlFileChooser.getSelectedFile();
	               outputSqlFile.setText(f.getAbsolutePath()); 	              
	            }     
			}
			
			/* Start data Generation */
			if (e.getActionCommand().equals("Start Generation"))
			{	
				if(generateSqlfileCheckBox.isSelected() && outputSqlFile.getText().trim().equalsIgnoreCase(""))
				{					 
					logOutputTextArea.setBorder(BorderFactory.createLineBorder(Color.red));	
					logOutputTextArea.setText("Please, set a valid sql output file or unmark the checkbox. \n");
				}else{
				
					recordToInsert = (Integer) recordToInsertSpinner.getValue();
					
					// pick the choices of the user
					numericGeneratorType = (String) this.numericTypeGeneratorListCombo.getSelectedItem();
					stringGeneratorType = (String) this.stringTypeGeneratorListCombo.getSelectedItem();
					datetimeGeneratorType = (String) this.datetimeTypeGeneratorListCombo.getSelectedItem();
					
					/* Debug
					System.out.println("Chosen Numeric Generator Type: "+numericGeneratorType);
					System.out.println("Chosen String Generator Type: "+stringGeneratorType);
					System.out.println("Chosen Date time Generator Type: "+datetimeGeneratorType);
					*/
					outputFile = outputSqlFile.getText().trim();				
					
					final DataGenerator dataGenerator = new DataGenerator(numericGeneratorType, stringGeneratorType, datetimeGeneratorType, recordToInsert, outputFile);
					
					if (!busyLabelPanel.getJxBusyLabel().isEnabled())
			       	{
			       	   busyLabelPanel.getJxBusyLabel().setEnabled(true);		       	  
			       	}
			       	
			       	if (busyLabelPanel.getJxBusyLabel().isBusy())  //true if the task is finished  
			       	{	 	    
			       	    busyLabelPanel.getJxBusyLabel().setBusy(false);
			       	    busyLabelPanel.getJxBusyLabel().setVisible(false);		       	   
			       	   
			       	}else {	        	    
			       	    busyLabelPanel.getJxBusyLabel().setBusy(true);
			       	    busyLabelPanel.getJxBusyLabel().setVisible(true); 
						
						Thread fillDatabaseThread = new Thread() {
		                    
							public void run() {					                       
							        
							    try {							    	
							    	 logOutputTextArea.setBorder(BorderFactory.createLineBorder(Color.green));	
							    	 logOutputTextArea.setText("Database Filling started...\n");
							    	 
							    	 long startTime = System.currentTimeMillis();					    	 
							    	 int totalTableFilled = dataGenerator.start();	 /* CORE METHOD */					    	 
						     		 long endTime = System.currentTimeMillis();
							    	 
									 //TO TEST Animation
							    	 //Thread.currentThread().sleep(10000);
							    	 
							    	 // stop and hide the animation
							     	 busyLabelPanel.getJxBusyLabel().setBusy(false);
							   		 busyLabelPanel.getJxBusyLabel().setVisible(false);						   							   		
							   		
									 logOutputTextArea.append("Database Filled successfully in: [" + (endTime - startTime) + "] ms (filled: "+totalTableFilled+" tables) ! \n");
							   		 
							   		 // stop and hide the animation
						     		 busyLabelPanel.getJxBusyLabel().setBusy(false);
						   		     busyLabelPanel.getJxBusyLabel().setVisible(false);
							    	   
							    }catch (Exception ex){		
							    	
							    	if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
							 		   logger.fatal("Error filling the database, cause: ",ex);
							    	
							    	busyLabelPanel.getJxBusyLabel().setBusy(false);
						   		    busyLabelPanel.getJxBusyLabel().setVisible(false);
									
									logOutputTextArea.setBorder(BorderFactory.createLineBorder(Color.red));	
									logOutputTextArea.setText(" ERROR filling the Database: "+ex.getMessage()+"\n");								
							    }
							}
						};
						
						fillDatabaseThread.start(); 
					
			       	} //else
		       	
			   } 	
			} //start
	    }		
	}


}
