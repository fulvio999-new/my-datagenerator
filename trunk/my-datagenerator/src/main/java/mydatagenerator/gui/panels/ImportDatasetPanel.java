
package mydatagenerator.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import mydatagenerator.core.database.operations.DatabaseImporter;
import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.gui.panels.common.BusyLabelPanel;
import mydatagenerator.gui.utils.filefilter.XmlFileFilter;
import mydatagenerator.init.Log4jManager;
import net.miginfocom.swing.MigLayout;

/**
 * Panel that offer the functionality to import a DBunit data-set in the target Database
 *
 */
public class ImportDatasetPanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(ImportDatasetPanel.class);
	
	/* The frame that own this panel */
	private JFrame mainFrame;
	
	/* Various labels */
	private JLabel operationResultLabel;
	private JLabel importTitleLabel;
	
	private JButton closeButton;
	
	//----- Import -----
	private JLabel importFileLabel;
	private JTextField importFileValue;			
	private JButton confirmImportButton;
	private JButton chooseImportFileButton;
		
	/* A sub-panel with an animation to indicates that a processing is in action */
	private BusyLabelPanel busyLabelPanel;

	/**
	 * Constructor
	 */
	public ImportDatasetPanel(JFrame mainFrame) {
		
		this.mainFrame = mainFrame;
			
		this.setLayout(new MigLayout("wrap 3")); //we want 3 column
		this.setBorder(BorderFactory.createTitledBorder("Import a DBunit dataset"));
		
		busyLabelPanel = new BusyLabelPanel();
		busyLabelPanel.getJxBusyLabel().setBusy(false);
		
		//---- Import ----
		operationResultLabel = new JLabel(""); //show the operation result
		
		importFileLabel = new JLabel("* Import File:"); ;
		importFileValue = new JTextField();		
		chooseImportFileButton = new JButton("Choose");
		chooseImportFileButton.setActionCommand("Choose Import File");
		chooseImportFileButton.addActionListener(this);
		confirmImportButton = new JButton("Import");		
		confirmImportButton.addActionListener(this);
		importTitleLabel = new JLabel("<html><b>Import a valid xml DBunit dataset</b></html>");
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		
		//---- Add the component to the panel
		this.add(importTitleLabel,"span 3,align center");		
		
		this.add(importFileLabel);		
		this.add(importFileValue,"width 600,growx");
		this.add(chooseImportFileButton,"width 120,gapbottom 10");
		
		this.add(new JLabel(""),"span 2"); //placeholder
		this.add(confirmImportButton,"width 120");
		
		this.add(operationResultLabel,"span 3, align center");	
		this.add(busyLabelPanel,"span 3 2, align center,gapbottom 80");
		
		this.add(new JPanel(),"span 4 4,height 300"); //placeholder
		
		this.add(new JLabel(""),"span 2"); //placeholder
		this.add(closeButton,"width 120");
		
	}
		
		/**
		 * Manage the actions on the buttons
		 */
		public void actionPerformed(ActionEvent e) {			
			
			final JFileChooser inputFileChooser = new JFileChooser(); //choose input data-set xml file
			
			/* Manage the action coming from the checkbox: hide show the partial export panel */
			if (e.getSource() instanceof JButton)  
			{				
				/**
				 * Perform the xml Dataset import operation
				 */
				if (e.getActionCommand().equals("Import")) 
			    {			
					final String inputFile = this.importFileValue.getText();
					
					if(inputFile == null || inputFile.equals("")){
						
			       	   operationResultLabel.setText("Invalid input file !");
					   operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
					   operationResultLabel.setForeground(Color.RED);
						
			       	 } else {					
			       		
						 if(!busyLabelPanel.getJxBusyLabel().isEnabled())
						 {
				       		 busyLabelPanel.getJxBusyLabel().setEnabled(true); 	        		
				       	 }
				       	 if(busyLabelPanel.getJxBusyLabel().isBusy()) {	        	    
				       	     busyLabelPanel.getJxBusyLabel().setBusy(false);
				       	     busyLabelPanel.getJxBusyLabel().setVisible(false);	 
				       	     
				       	 }else {	        	    
				       	      busyLabelPanel.getJxBusyLabel().setBusy(true);
				       	      busyLabelPanel.getJxBusyLabel().setVisible(true);	       	     
				       	      
				       	 	  // The exporting operation can be a long task: open a separate Thread       	   
							  Thread exportDatabaseThread = new Thread() {
								                    
								  public void run() {					                       
								        
								      try {						     		            
								    	   // Import a DBunit dataset in the target DB
								     	   DatabaseImporter databaseImporter = new DatabaseImporter(); 	
								     	   databaseImporter.importDataSet(inputFile);     				    
								     		
								     	   // stop and hide the animation
								     	   busyLabelPanel.getJxBusyLabel().setBusy(false);
								   		   busyLabelPanel.getJxBusyLabel().setVisible(false);
								   		  
								   		   operationResultLabel.setText("Operation Executed Successfully");
								   		   operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
								   		   operationResultLabel.setForeground(Color.GREEN);
								     					    
								          } catch (Exception ex) {
								        	 
								        	if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
								 		       logger.fatal("Error importing the dbunit dataset, cause: ",ex);   
								        	  
											// stop and hide the animation
											busyLabelPanel.getJxBusyLabel().setBusy(false);
									   		busyLabelPanel.getJxBusyLabel().setVisible(false);	
									   		
									   		operationResultLabel.setText("Error during the import operation: "+ex.getMessage());
									   		operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
									   		operationResultLabel.setForeground(Color.RED);
								         }
					                 }
								 };
								                
							 exportDatabaseThread.start(); 
					      } 
			       	 
			       	 }//else
			    }
				
				/* 
				 * Choose an import xml as DBunit dataset
				 */
			    if (e.getActionCommand().equals("Choose Import File"))
			    {
			    	// only xml file are accepted
			    	inputFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
			    	inputFileChooser.setFileFilter(new XmlFileFilter());
			    	inputFileChooser.setDialogTitle("Choose an XML input file");	        	
		              
		            int value = inputFileChooser.showOpenDialog(this);
		             
		            //Return value if approved (ie yes, ok) is chosen.
		            if (value == JFileChooser.APPROVE_OPTION)
		            {	              
		               File inputFile = inputFileChooser.getSelectedFile();	               
		               String  pathDestinationFolder = inputFile.getAbsolutePath(); 
	                   //set the file path in the textField
		               this.importFileValue.setText(pathDestinationFolder); 
			        }
			    }
			    
			    /* Close the application */
			    if (e.getActionCommand().equals("Close")) 
			    {
			      if (mainFrame.isDisplayable()) {  
			    	 DatabaseConnectionFactory.closeDataSource();
		     	     mainFrame.dispose();
		          }
			   }				
				
			}		
	}

}
