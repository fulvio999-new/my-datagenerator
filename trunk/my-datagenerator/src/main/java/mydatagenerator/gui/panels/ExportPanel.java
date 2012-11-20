
package mydatagenerator.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import mydatagenerator.core.database.operations.DatabaseExporter;
import mydatagenerator.core.database.operations.DatabaseTableUtils;
import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.gui.custom.table.model.PartialExportTableModel;
import mydatagenerator.gui.panels.common.BusyLabelPanel;
import mydatagenerator.gui.utils.filefilter.FolderFileFilter;
import mydatagenerator.init.Log4jManager;
import mydatagenerator.model.bean.TableInfoPartialExportBean;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

/**
 * Create the panel with the functionality to export the target DB content as DBunit Dataset
 *
 */
public class ExportPanel extends JPanel implements ActionListener{	

	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(ExportPanel.class);
	
	/* The frame that own this panel */
	private JFrame mainFrame;
	
	/* Various labels */
	private JLabel operationResultLabel;
	private JLabel fixHelpLabel;
	private JLabel fullExportLabel;	
	private JLabel exportTypeLabel;
	
	private JButton closeButton;
	
	private JRadioButton fullExportRadioButton;
	private JRadioButton partialExportRadioButton;
	private ButtonGroup exportTypeButtonGroup;
	
	//----- Export ------
	private JLabel exportFolderLabel;
	private JTextField exportFolderValue;
	private JButton chooseExportFolderButton;		
	private JButton confirmFullExportButton;
	
	//----- Partial Export: a panel where the user can insert the query to export only part of the DB
	private PartialExportPanel partialDatabaseExportPanel;
	
	/* A sub-panel with an animation to indicates that a processing is in action */
    private BusyLabelPanel busyLabelPanel;
  
	/**
	 * Constructor
	 */
	public ExportPanel(JFrame mainFrame) {
		
        this.mainFrame = mainFrame;
		
		this.setLayout(new MigLayout("wrap 3")); //we want 3 column
		this.setBorder(BorderFactory.createTitledBorder("Export Database as DBunit dataset"));		
		
		fixHelpLabel = new JLabel("Choose the destination folder, the export file name will be the database name");		
		
		fullExportLabel = new JLabel("<html><b>Database Export</b></html>");
		exportTypeLabel = new JLabel("Export Type:");			
				
		busyLabelPanel = new BusyLabelPanel();
		busyLabelPanel.getJxBusyLabel().setBusy(false);
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		
		fullExportRadioButton = new JRadioButton("Full export");
		fullExportRadioButton.doClick(); //by default this button is clicked: ie export all DB content
		partialExportRadioButton = new JRadioButton("Partial export");
		fullExportRadioButton.addActionListener(this);
		partialExportRadioButton.addActionListener(this);
		
		exportTypeButtonGroup = new ButtonGroup();
		exportTypeButtonGroup.add(fullExportRadioButton);
		exportTypeButtonGroup.add(partialExportRadioButton);
		
		//---- Export ----
		exportFolderLabel = new JLabel("* Destination Folder:");
		exportFolderValue = new JTextField();
		
		chooseExportFolderButton = new JButton("Choose");
		chooseExportFolderButton.addActionListener(this);			
		confirmFullExportButton = new JButton("Full Export");
		confirmFullExportButton.addActionListener(this);
		
		partialDatabaseExportPanel = new PartialExportPanel(this);
		partialDatabaseExportPanel.setVisible(false);
		
		operationResultLabel = new JLabel("");
		
		//-------- Add the components to the panel ---------
		this.add(fullExportLabel,"span 3,align center");
		this.add(fixHelpLabel,"span 3,align center");
		
		//full export
		this.add(exportFolderLabel);		
		this.add(exportFolderValue,"width 620,growx");
		this.add(chooseExportFolderButton,"width 120,gapbottom 10");				
		
		//export type chooser
		this.add(exportTypeLabel);
		this.add(fullExportRadioButton,"split 2");
		this.add(partialExportRadioButton);
		this.add(confirmFullExportButton,"width 120,gapbottom 10"); //placeholder
		
		//partial export panel
		this.add(partialDatabaseExportPanel,"span 3,growx");
		
		this.add(operationResultLabel,"span 3 1, align center,gapbottom 10");
		this.add(busyLabelPanel,"span 3 2, align center,gapbottom 20");			
		
		this.add(new JPanel(),"span 2"); //placeholder
		this.add(closeButton,"width 120");		
	}

	/**
	 * Manage the actions on the buttons
	 */
	public void actionPerformed(ActionEvent e) {
		
		final JFileChooser destinationFolderChooser = new JFileChooser(); //choose destination export folder		
		
		/* Manage the action coming from the checkbox: hide or show the partial export panel */
		if (e.getSource() instanceof JRadioButton)  
		{		     
		   if(e.getActionCommand().equals("Full export")){		    	  
              this.partialDatabaseExportPanel.setVisible(false);	
              this.confirmFullExportButton.setEnabled(true);
              operationResultLabel.setText(""); //clean old msg
              
		   }
		         
		   if(e.getActionCommand().equals("Partial export")){  //show the panel where set the table to export  	  
			  this.partialDatabaseExportPanel.setVisible(true); 
			  this.confirmFullExportButton.setEnabled(false);
			  operationResultLabel.setText("");
		   } 		      	      
		}
		
		if (e.getSource() instanceof JButton)  
	    {	
			/**
			 * Perform the Full DB export operation
			 */
			if (e.getActionCommand().equals("Full Export")) 
		    {	    	   
		       	 if (!busyLabelPanel.getJxBusyLabel().isEnabled()){
		       		 busyLabelPanel.getJxBusyLabel().setEnabled(true); 	        		
		       	 }
		       	 if (busyLabelPanel.getJxBusyLabel().isBusy()) {	        	    
		       	     busyLabelPanel.getJxBusyLabel().setBusy(false);
		       	     busyLabelPanel.getJxBusyLabel().setVisible(false);	   
		       	     
		       	 }else {	        	    
		       	       busyLabelPanel.getJxBusyLabel().setBusy(true);
		       	       busyLabelPanel.getJxBusyLabel().setVisible(true);	       	     
		       	     
		       	       final String destinationFolder = this.exportFolderValue.getText();
		       	      
		       	 	   //The exporting operation can be a long task: open a separate Thread       	   
					   Thread fullExportDatabaseThread = new Thread() {
						                    
						  public void run() {					                       
						        
						      try{						     		            
						     	   // Export the DB content as DBunit dataset 
						    	 
						     	   DatabaseExporter databaseExporter = new DatabaseExporter(); 	
						     	   databaseExporter.fullExport(destinationFolder);     				    
						     		
						     	   // stop and hide the animation
						     	   busyLabelPanel.getJxBusyLabel().setBusy(false);
						   		   busyLabelPanel.getJxBusyLabel().setVisible(false);
						   								   		
						   		   operationResultLabel.setText("Operation Executed Successfully");
						   		   operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
						   		   operationResultLabel.setForeground(Color.GREEN);
						     					    
						         }catch (Exception ex) {
						        	 
						        	if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
					 		    		 logger.fatal("Error executing the full DB export, cause: ",ex); 
						        	 
									// stop and hide the animation
									busyLabelPanel.getJxBusyLabel().setBusy(false);
							   		busyLabelPanel.getJxBusyLabel().setVisible(false);	
							   		
							   		operationResultLabel.setText("Error during the export operation: "+ex.getMessage());
							   		operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
							   		operationResultLabel.setForeground(Color.RED);							   		
						         }
			                 }
						 };
						                
					fullExportDatabaseThread.start(); 
			    } 		       	     
		    } 
			
			/**
			 * Perform a partial export of the chosen tables using the provided query to extract the data-set for each selected table
			 * 
			 * IMPORTANT: due to editable table before exoprt the partial dataset is necessary remove the focus from the table, because
			 * a change made by the user in a specific cell is applied to the table model only when the editable cell lose the focus
			 * or the user has pressed the return key
			 * 
			 */
			if (e.getActionCommand().equals("Partial Export")) 
		    {
				 if (!busyLabelPanel.getJxBusyLabel().isEnabled()){
		       		 busyLabelPanel.getJxBusyLabel().setEnabled(true); 	        		
		       	 }
		       	 if (busyLabelPanel.getJxBusyLabel().isBusy()) {	        	    
		       	     busyLabelPanel.getJxBusyLabel().setBusy(false);
		       	     busyLabelPanel.getJxBusyLabel().setVisible(false);	   
		       	     
		       	 }else {	        	    
		       	       busyLabelPanel.getJxBusyLabel().setBusy(true);
		       	       busyLabelPanel.getJxBusyLabel().setVisible(true);	       	     
		       	     
		       	       final String destinationFolder = this.exportFolderValue.getText();
		       	       
		       	       //only the table list that the user want export
		       	       final List<TableInfoPartialExportBean> filteredTableToExport = new ArrayList<TableInfoPartialExportBean>();
		       	       
		       	       //---- Get the table model to obtain the user choice: ie the table to export   
		       	       PartialExportTableModel tableModel = (PartialExportTableModel)this.getPartialDatabaseExportPanel().getTableSelectionGrid().getModel();
		       	       List<TableInfoPartialExportBean> fullTableList = tableModel.getRowList();
		       	       
		       	       for(int i=0;i<fullTableList.size();i++)
		       	       {
		       	    	   if(fullTableList.get(i).isIncludeTable())
		       	    	   {		       	    		   
			       	    		TableInfoPartialExportBean tableInfoPartialExport = new TableInfoPartialExportBean();
			       	    		tableInfoPartialExport.setTableName(fullTableList.get(i).getTableName());
			       	    		tableInfoPartialExport.setExportQuery(fullTableList.get(i).getExportQuery());
			       	    		
			       	    		filteredTableToExport.add(tableInfoPartialExport);		       	    		 
		       	    	   }
		       	       }  	      
		       	       //-------------
		       	      
		       	 	   //The exporting operation can be a long task: open a separate Thread       	   
					   Thread partialExportDatabaseThread = new Thread() {
						                    
						  public void run() {					                       
						        
						      try{						     		            
						     	   // Export the DB content as DBunit dataset 
						     	   DatabaseExporter databaseExporter = new DatabaseExporter(); 	
						     	   databaseExporter.partialExport(destinationFolder, filteredTableToExport);  
						     		
						     	   // stop and hide the animation
						     	   busyLabelPanel.getJxBusyLabel().setBusy(false);
						   		   busyLabelPanel.getJxBusyLabel().setVisible(false);
						   								   		
						   		   operationResultLabel.setText("Operation Executed Successfully");
						   		   operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
						   		   operationResultLabel.setForeground(Color.GREEN);
						     					    
						         }catch (Exception ex) {
						        	 
						        	if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
					 		    		 logger.fatal("Error executing the partial DB export, cause: ",ex);  
						        	 
									// stop and hide the animation
									busyLabelPanel.getJxBusyLabel().setBusy(false);
							   		busyLabelPanel.getJxBusyLabel().setVisible(false);	
							   		
							   		operationResultLabel.setText("Error during the export operation: "+ex.getMessage());
							   		operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
							   		operationResultLabel.setForeground(Color.RED);							   		
						         }
			                 }
						 };
						                
					partialExportDatabaseThread.start(); 
			    } 
		    }
			
			/** 
			 * Choose the destination folder for DB export
			 */
			if (e.getActionCommand().equals("Choose"))
		    {				
				destinationFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				destinationFolderChooser.setFileFilter(new FolderFileFilter());
				destinationFolderChooser.setDialogTitle("Choose a Destination Folder");	        	
	              
	            int value = destinationFolderChooser.showOpenDialog(this);
	             
	            // Return value if approved (ie yes, ok) is chosen.
	            if (value == JFileChooser.APPROVE_OPTION)
	            {
	               // The file name is equal at the DB name to export
	               File  destinationFolder = destinationFolderChooser.getSelectedFile();	              
	               String  pathDestinationFolder = destinationFolder.getAbsolutePath(); 
                   //set the file path in the textField
	               this.exportFolderValue.setText(pathDestinationFolder); 
		        }
		    }  
		    
		    /* The event intercepted by the sub-panel (ie PartialExportPanel) and forwarded at this listener */
		    if (e.getActionCommand().equals("Load Tables")) 
		    { 
		    	final PartialExportTableModel tableModel = (PartialExportTableModel)this.getPartialDatabaseExportPanel().getTableSelectionGrid().getModel();
		    	
		    	tableModel.setRowList(new ArrayList<TableInfoPartialExportBean>());
		    	//initialize the table model with an empty list of data
			    tableModel.fireTableDataChanged();
			    
			    adjustTableWidth();
			    
			    final PartialExportPanel partialExpPanel = this.getPartialDatabaseExportPanel();
			   
			    if (!busyLabelPanel.getJxBusyLabel().isEnabled())
		       	{
		       	   busyLabelPanel.getJxBusyLabel().setEnabled(true);		       	  
		       	}
		       	
		       	if (busyLabelPanel.getJxBusyLabel().isBusy()) {	   //true if task is finished   	    
		       	    busyLabelPanel.getJxBusyLabel().setBusy(false);
		       	    busyLabelPanel.getJxBusyLabel().setVisible(false);		       	   
		       	   
		       	}else{	        	    
		       	    busyLabelPanel.getJxBusyLabel().setBusy(true);
		       	    busyLabelPanel.getJxBusyLabel().setVisible(true); 
		       	    
		       	    // Loading the ordered table name list can take some time: create a dedicated Thread to perform it  		       	   
				    Thread cleanDatabaseThread = new Thread() {
					                    
					   public void run() {				                       
					        
					       try {					     		            
					    	     DatabaseTableUtils databaseTableUtils = new DatabaseTableUtils();
					    	     List<String> tableNames = databaseTableUtils.getTableNamesOrdered();					    	     
					    	     
					    	     List<TableInfoPartialExportBean> tableInfoPartialExporterList = new ArrayList<TableInfoPartialExportBean>();
					    	     
					    	     for(int i=0; i<tableNames.size(); i++)
					    	     {					    	     
					    	        TableInfoPartialExportBean tableInfo = new TableInfoPartialExportBean();	
					    	        tableInfo.setExportQuery("select * from "+tableNames.get(i)); //the default query to export full content
					    	        tableInfo.setIncludeTable(false);
					    	        tableInfo.setTableName(tableNames.get(i));
					    	       
					    	        tableInfoPartialExporterList.add(tableInfo);					    	       
					    	     }
					    	     
					    	     tableModel.setRowList(tableInfoPartialExporterList);
					    	     tableModel.fireTableDataChanged(); //reload the showed data
					    	     
					    	     adjustTableWidth();
					     		
					     		 //stop and hide the animation
					     		 busyLabelPanel.getJxBusyLabel().setBusy(false);
					   		     busyLabelPanel.getJxBusyLabel().setVisible(false);
					   		     
					   		     //show operation result
					   		     operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
					   		     operationResultLabel.setForeground(Color.GREEN);
					   		     operationResultLabel.setText("Operation executed successfully, loaded "+tableNames.size()+" tables");
					   		     
					   		     //when the tables are loaded the mark/unmark button must be enabled
							     partialExpPanel.getSelectAllTablebutton().setEnabled(true);
					     					    
					           } catch (Exception ex) {
					        	   
					        	   if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
					 		    		 logger.info("Error loading the DB table names, cause: ",ex);  
									
									//stop and hide the animation
									busyLabelPanel.getJxBusyLabel().setBusy(false);
						   		    busyLabelPanel.getJxBusyLabel().setVisible(false);
						   		    
						   		    operationResultLabel.setText("Error loading tables: "+ex.getMessage());
							   		operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
							   		operationResultLabel.setForeground(Color.RED);
					           }
		                 }					
					 };
					                
					cleanDatabaseThread.start(); 			   
		        }    	
		       	
		   }  
		   /* Select all the listed tables */ 
		   if (e.getActionCommand().equals("Check All")) 
		   {
			   PartialExportTableModel tableModel = (PartialExportTableModel)this.getPartialDatabaseExportPanel().getTableSelectionGrid().getModel();
       	       List<TableInfoPartialExportBean> fullTableList = tableModel.getRowList();
       	       
       	       for(int i=0;i<fullTableList.size();i++){
       	    	  fullTableList.get(i).setIncludeTable(true);       	    	   
       	       }
       	       tableModel.fireTableDataChanged();
		   }
		   /* De-Select all the listed tables */ 	
		   if (e.getActionCommand().equals("Uncheck All")) 
		   {
			   PartialExportTableModel tableModel = (PartialExportTableModel)this.getPartialDatabaseExportPanel().getTableSelectionGrid().getModel();
       	       List<TableInfoPartialExportBean> fullTableList = tableModel.getRowList();
       	       
       	       for(int i=0;i<fullTableList.size();i++){
       	    	  fullTableList.get(i).setIncludeTable(false);       	    	   
       	       }
       	       tableModel.fireTableDataChanged();
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
	

	/**
	 * Utility method that set the table width
	 * @param table
	 */
	private void adjustTableWidth(){
		
		//the width of the checkbox column
		TableColumn firstCol = this.getPartialDatabaseExportPanel().getTableSelectionGrid().getColumnModel().getColumn(0);		
		firstCol.setPreferredWidth(80);
				
		TableColumn secondCol = this.getPartialDatabaseExportPanel().getTableSelectionGrid().getColumnModel().getColumn(1);
		secondCol.setPreferredWidth(250);
				
		TableColumn thirdCol = this.getPartialDatabaseExportPanel().getTableSelectionGrid().getColumnModel().getColumn(2);
		thirdCol.setPreferredWidth(470);
	}
	

	public PartialExportPanel getPartialDatabaseExportPanel() {
		return partialDatabaseExportPanel;
	}
	
	public void setPartialDatabaseExportPanel(
			PartialExportPanel partialDatabaseExportPanel) {
		this.partialDatabaseExportPanel = partialDatabaseExportPanel;
	}

	public JLabel getOperationResultLabel() {
		return operationResultLabel;
	}

	public void setOperationResultLabel(JLabel operationResultLabel) {
		this.operationResultLabel = operationResultLabel;
	}

}
