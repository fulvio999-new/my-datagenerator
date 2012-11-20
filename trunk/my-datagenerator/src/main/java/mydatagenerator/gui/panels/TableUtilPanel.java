
package mydatagenerator.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import mydatagenerator.core.database.operations.DatabaseTableUtils;
import mydatagenerator.core.database.operations.utility.DatabaseConnectionFactory;
import mydatagenerator.gui.custom.table.model.AdvancedTableInfoModel;
import mydatagenerator.gui.custom.table.model.DatabaseTableInfoModel;
import mydatagenerator.gui.panels.common.BusyLabelPanel;
import mydatagenerator.gui.utils.filefilter.FolderFileFilter;
import mydatagenerator.init.Log4jManager;
import mydatagenerator.model.bean.AdvancedMetadataTableInfo;
import mydatagenerator.model.bean.MetadataTableInfoBean;
import net.miginfocom.swing.MigLayout;

/**
 * Create a jpanel that show informations about the tables of the target database.
 * ie: table list, table fields details ...
 *
 */
public class TableUtilPanel extends JPanel implements ListSelectionListener, ActionListener{	
	
    private static final long serialVersionUID = 1L;
    
    private final static Logger logger = Logger.getLogger(TableUtilPanel.class);
	
	/* The frame that own this panel */
	private JFrame mainFrame;
	
	/* The list of database table name */
	private JList databaseTableList; 
	
	private JLabel tableNameListLabel;
	private JLabel tableDetailsListLabel;
	private JLabel advancedTableInfoLabel;
	
	private JTable fieldDetailsTable;
		
	/* Advanced informations about a table */
	private JTable advancedDetailsTable;
	
	/* The scrollPanel with the DB table names */
	private JScrollPane tableListScrollPanel;	
	
	/* A label to show the operation error result */
	private JLabel operationResultLabel;
	
	/* A sub-panel with an animation to indicates that a processing is in action */
    private BusyLabelPanel busyLabelPanel;
	
	private JButton loadTableNamesButton;
	private JButton exportTableAsCsvButton;
	private JButton closebutton;
	
	/**
	 * Constructor
	 */
	public TableUtilPanel(JFrame mainFrame) {
		
		this.mainFrame = mainFrame;			
		this.setLayout(new MigLayout("wrap 2")); //we want 2 column
		this.setBorder(BorderFactory.createTitledBorder("Database Table Utils"));	
		
		tableNameListLabel = new JLabel("Table(s) sorted in filling order:");
		tableDetailsListLabel = new JLabel("Table Fields Details:");
		
		// the list of tables contained in the target DB
		databaseTableList = new JList();		
		databaseTableList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		databaseTableList.setLayoutOrientation(JList.VERTICAL);
		databaseTableList.setVisibleRowCount(-1);		
		databaseTableList.addListSelectionListener(this);	
		
		//databaseTableList.setCellRenderer(this);
		
		tableListScrollPanel = new JScrollPane(databaseTableList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		/* to show messages to the user */
		operationResultLabel = new JLabel("");
		operationResultLabel.setVisible(false);
		
		advancedTableInfoLabel = new JLabel("Advanced table options:");
		
		loadTableNamesButton = new JButton("Load Tables");
		loadTableNamesButton.addActionListener(this);
		
		exportTableAsCsvButton = new JButton("Export to file");
		exportTableAsCsvButton.addActionListener(this);
		exportTableAsCsvButton.setEnabled(false); //enable only when the table list is loaded
		
		busyLabelPanel = new BusyLabelPanel();
		busyLabelPanel.getJxBusyLabel().setBusy(false);
		
		closebutton = new JButton("Close");
		closebutton.addActionListener(this);
		
		// the table with the informations about the table field
		fieldDetailsTable = new JTable(); //set default data model
		fieldDetailsTable.setModel(new DatabaseTableInfoModel());
		JScrollPane fieldsDetailsScrollPane = new JScrollPane(fieldDetailsTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// the table with the advanced table informations
		advancedDetailsTable = new JTable();
		advancedDetailsTable.setModel(new AdvancedTableInfoModel());
		TableColumn firstCol = advancedDetailsTable.getColumnModel().getColumn(2);		
		firstCol.setPreferredWidth(200);
		JScrollPane advancedDetailsScrollPane = new JScrollPane(advancedDetailsTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		
		//----- Add the components to the panel	
		this.add(tableNameListLabel,"align center");
		this.add(tableDetailsListLabel,"align center");
		
		this.add(tableListScrollPanel,"height 530,growx");			
		this.add(fieldsDetailsScrollPane,"height 530,growx");		
		
		this.add(advancedTableInfoLabel,"align center");
		this.add(advancedDetailsScrollPane,"span 1,height 200,growx");
		
		this.add(operationResultLabel,"span 2 1, align center");
		
		this.add(busyLabelPanel,"span 2 2, align center,gapbottom 30");	
		
		this.add(loadTableNamesButton,"width 120,split 2"); //split the cell in two part
		this.add(exportTableAsCsvButton);
		this.add(closebutton,"width 120,gapleft 500");			
	}

	
	/**
	 * Manage the actions on the buttons of this panel
	 */
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser destinationFolderChooser = new JFileChooser(); //choose destination export folder
		
		if (e.getSource() instanceof JButton)  
		{		
		   if (e.getActionCommand().equals("Close")) 
		   {
		      if (mainFrame.isDisplayable()) {   
		    	  DatabaseConnectionFactory.closeDataSource();
	     	      mainFrame.dispose();
	          }
		   }
		   
		   /* Export the table names list to a txt file */
		   if (e.getActionCommand().equals("Export to file"))
		   {			   
			   destinationFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			   destinationFolderChooser.setFileFilter(new FolderFileFilter());
			   destinationFolderChooser.setDialogTitle("Choose the destination folder");	        	
	             
	           int value = destinationFolderChooser.showOpenDialog(this);
	            
	           // Return value if approved (ie yes, ok) is chosen.
	           if (value == JFileChooser.APPROVE_OPTION)
	           {	             
	              File  destinationFolder = destinationFolderChooser.getSelectedFile();	              
	              String  pathDestinationFolder = destinationFolder.getAbsolutePath(); 	             
	              
	              try{	            	  
	            	    //System.out.println("Export to: "+pathDestinationFolder+File.separator+DatabaseConnectionFactory.getDatabaseName()+"-table-list.txt");
	            	    File outputFile = new File(pathDestinationFolder+File.separator+DatabaseConnectionFactory.getDatabaseName()+"-table-list.txt"); 
						if(outputFile.exists())
						   outputFile.delete();
						
						outputFile.createNewFile(); //create a new file
						FileWriter fileWriter = new FileWriter(outputFile); 
						BufferedWriter out = new BufferedWriter(fileWriter);
	            	  
						out.write("-- Table(s) sorted in filling order of the Database: "+DatabaseConnectionFactory.getDatabaseName()+" \n\n");
						
						for(int i=0; i<databaseTableList.getModel().getSize();i++){
						   out.write(databaseTableList.getModel().getElementAt(i).toString()+"\n");
						}
						
						out.flush();
						out.close();
						
						operationResultLabel.setText("");
						operationResultLabel.setText("List exported successfully to: "+pathDestinationFolder);
			   		    operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
			   		    operationResultLabel.setForeground(Color.GREEN);
			   		    operationResultLabel.setVisible(true);
						
	              }catch (Exception ex) {
	            	  
	            	  if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
			 		     logger.fatal("Error exporting table names to file, cause: ",ex); 
	            	  
	            	  operationResultLabel.setText("");
	            	  operationResultLabel.setText("Error exporting table list: "+ex.getMessage());
				   	  operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
				   	  operationResultLabel.setForeground(Color.RED);
				   	  operationResultLabel.setVisible(true);
				  }		              
		       }
		   }
		   
		   if (e.getActionCommand().equals("Load Tables")) 
		   {	
			    //----- Clean previous values ----
			    operationResultLabel.setText("");
			    exportTableAsCsvButton.setEnabled(false);
			    databaseTableList.setListData(new Vector());
			    DatabaseTableInfoModel tableModel = (DatabaseTableInfoModel) fieldDetailsTable.getModel();
			    // initialize the table model with an empty list of data
			    tableModel.setTableFieldInfoBeanList(new ArrayList<MetadataTableInfoBean>());
			    tableModel.fireTableDataChanged();
			    
			    //clean the table with the advanced info
			    AdvancedTableInfoModel advancedTableModel = (AdvancedTableInfoModel) advancedDetailsTable.getModel();		      
			    advancedTableModel.setTableFieldInfoBeanList(new ArrayList<AdvancedMetadataTableInfo>());
			    advancedTableModel.fireTableDataChanged();
			    
			    //--------------------------------
			   
			    if (!busyLabelPanel.getJxBusyLabel().isEnabled())
		       	{
		       	   busyLabelPanel.getJxBusyLabel().setEnabled(true);		       	  
		       	}
		       	
		       	if (busyLabelPanel.getJxBusyLabel().isBusy()) {	   //true if the task is finished   	    
		       	    busyLabelPanel.getJxBusyLabel().setBusy(false);
		       	    busyLabelPanel.getJxBusyLabel().setVisible(false);		       	   
		       	   
		       	}else {	        	    
		       	    busyLabelPanel.getJxBusyLabel().setBusy(true);
		       	    busyLabelPanel.getJxBusyLabel().setVisible(true); 
		       	    
		       	    // Loading the ordered table names list can take some time: create a dedicated Thread to perform it  		       	   
				    Thread cleanDatabaseThread = new Thread() {
					                    
					   public void run() {					                       
					        
					       try {					     		            
					    	     DatabaseTableUtils databaseTableUtils = new DatabaseTableUtils();
					    	     List<String> tableNames = databaseTableUtils.getTableNamesOrdered();					    	     
					    	     databaseTableList.setListData(tableNames.toArray());  
					    	     exportTableAsCsvButton.setEnabled(true);
					     		
					     		 // stop and hide the animation
					     		 busyLabelPanel.getJxBusyLabel().setBusy(false);
					   		     busyLabelPanel.getJxBusyLabel().setVisible(false);
					   		     
					   		     //only in case of errors a message is displayed, in case of success remove old error message (if any)
					   		     operationResultLabel.setText("Loaded "+tableNames.size() +" tables. (Select a table for details)");
					   		     operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
					   		     operationResultLabel.setForeground(Color.GREEN);
					   		     operationResultLabel.setVisible(true);
					     					    
					           } catch (Exception ex) {
					        	   
					        	   if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
							 		  logger.fatal("Error loading table names, cause: ",ex); 
									
									// stop and hide the animation
									busyLabelPanel.getJxBusyLabel().setBusy(false);
						   		    busyLabelPanel.getJxBusyLabel().setVisible(false);
						   		 
						   		    operationResultLabel.setText("Error loading tables: "+ex.getMessage());
							   		operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
							   		operationResultLabel.setForeground(Color.RED);
							   		operationResultLabel.setVisible(true);
					           }
		                  }
					 };
					                
					cleanDatabaseThread.start(); 			   
		      }
		   }
		}
	}
	
	/**
	 * Manage the selection event on the items (ie a table name in the left side panel with the list of DB tables names)
	 * Note: the index start from 0
	 */
	public void valueChanged(ListSelectionEvent listSelectionEvent) 
	{
		// false when the selection event is finished (ie the selection is done)
	    boolean adjust = listSelectionEvent.getValueIsAdjusting(); 
	    String selectedTableName = null;
	        
	    if (!adjust)
	    {
	       JList list = (JList) listSelectionEvent.getSource();
	       int selections[] = list.getSelectedIndices();
	       Object selectionValues[] = list.getSelectedValues();
	       
	       DatabaseTableUtils databaseTableUtils = new DatabaseTableUtils();
	       ArrayList<MetadataTableInfoBean> tableFieldInfoBean = null; // the info about the table fields
	       ArrayList<AdvancedMetadataTableInfo> advancedTableInfoBean = null; //the advanced table info
	       
	       try{   
		       for (int i = 0, n = selections.length; i < n; i++) 
		       {
		    	  selectedTableName = (String) selectionValues[i]; // the selected value (ie a table name)	         
		          tableFieldInfoBean = databaseTableUtils.getFieldsInfo(selectedTableName);
		          advancedTableInfoBean = databaseTableUtils.getAdvancedTableInfo(selectedTableName);
		       } 
		      
		      // update the data showed in the field info table with the one coming form the DB
		      DatabaseTableInfoModel c = (DatabaseTableInfoModel) fieldDetailsTable.getModel();		      
		      c.setTableFieldInfoBeanList(tableFieldInfoBean);
		      c.fireTableDataChanged();		       
		      
		      // update the others table with the advanced table informations
		      AdvancedTableInfoModel a = (AdvancedTableInfoModel) advancedDetailsTable.getModel();		      
		      a.setTableFieldInfoBeanList(advancedTableInfoBean);
		      a.fireTableDataChanged();			      
		       
	       }catch (Exception e) {
	    	   
	    	   if(Log4jManager.IS_LOGGING_CONFIGURED) 		    	
			 	  logger.fatal("Error loading table details, cause: ",e); 
	    	   
			   // Some error happens during table info retrieving
	    	   operationResultLabel.setText("Error loading table details: check the DB connection");
		   	   operationResultLabel.setFont(new Font("Serif", Font.BOLD, 15));
		   	   operationResultLabel.setForeground(Color.RED);	    	   
		   }
	         
	   }
	}

    /**
     * Method about ListCellRenderer interface. Used to draw some list item with a different color.
     * It draw in red color the tables with storage engine of type "ARCHIVE" because this type of engine don't support the delete operation
     * (See: http://dev.mysql.com/doc/refman/5.1/en/archive-storage-engine.html)
     */
	/*
	      ---  CURRENTLY NOT USED ---
	 
	public Component getListCellRendererComponent(JList list, Object value,	int index, boolean isSelected, boolean cellHasFocus) {
		
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		    
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (index % 2 == 0)
		   	renderer.setForeground(Color.RED);  //drow wit red font the table names    
		
		return renderer;
	}
	*/
		
}

