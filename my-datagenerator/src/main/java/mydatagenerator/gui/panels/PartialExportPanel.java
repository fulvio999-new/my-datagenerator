
package mydatagenerator.gui.panels;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import mydatagenerator.gui.custom.table.model.PartialExportTableModel;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

/**
 * Create a sub-panel of the panel "ImportExportService" with the necessary options to perform a  
 * partial export of the DB. The dataSet to export is obtained with a given query 
 *
 */
public class PartialExportPanel extends JPanel implements ActionListener{	
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(PartialExportPanel.class);
	
	private JLabel partialExportLabel;
	
	private JButton loadTableListButton; //load the table list 
	private JButton confirmPartialExportButton;
	private JButton selectAllTablebutton; 

	/* The table with the list of DB table where the user can choose which one export and for each table can 
	 * decide if export all the table content (default) or only part of it adding a where condition at the default query
	 * */
	private JTable tableSelectionGrid;	
	
	/* The parent jpanel that contains this sub-panel */
	private ExportPanel parentPanel;

	/**
	 * Constructor
	 */
	public PartialExportPanel(ExportPanel importExportServicePanel) {
		
		this.parentPanel = importExportServicePanel;		
		this.setLayout(new MigLayout("wrap 3")); //we want 3 column
		
		partialExportLabel = new JLabel("<html><b>Partial Database Export: select the table(s) to export</b></html>");
		
		loadTableListButton = new JButton("Load Tables");
		loadTableListButton.addActionListener(this);
		
		selectAllTablebutton = new JButton("Check All");
		selectAllTablebutton.addActionListener(this);
		selectAllTablebutton.setEnabled(false);		
		
		confirmPartialExportButton = new JButton("Partial Export");
		confirmPartialExportButton.addActionListener(this);		
		
		/* An EDITABLE table where the user can choose the table(s) to include in the export and if it want all the table content or only part of it */
		tableSelectionGrid = new JTable(); 		
		tableSelectionGrid.setModel(new PartialExportTableModel());
		 
		//necessary to use user defined column width
		tableSelectionGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		tableSelectionGrid.setCellSelectionEnabled(true);
		
		adjustTableWidth();
		
		JScrollPane scrollPane = new JScrollPane(tableSelectionGrid,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//--- Add the components to the panel
		this.add(partialExportLabel,"span 3,align center"); //title	
		
		this.add(scrollPane,"span 3,width 850,growx,growy");		
		
		this.add(loadTableListButton,"width 120"); 		
		this.add(selectAllTablebutton,"width 120,gapleft 40");
		this.add(confirmPartialExportButton,"width 120,align right");
	}

	/**
	 * Handle the actions on the buttons and pass it at parent panel to manage them
	 */
	public void actionPerformed(ActionEvent e) { 
		
		if (e.getSource() instanceof JButton)  
	    {			
			if (e.getActionCommand().equals("Load Tables")) 
		    {
			   this.parentPanel.getOperationResultLabel().setText(""); // clean old message
			   this.parentPanel.actionPerformed(e);
		    }	
			
			if (e.getActionCommand().equals("Partial Export")) 
		    {
			   this.parentPanel.actionPerformed(e);
		    }
			
			/* change the label showed on the button */
			if (e.getActionCommand().equals("Check All")) 
		    {
			   this.selectAllTablebutton.setText("Uncheck All");
			   this.parentPanel.actionPerformed(e);
		    }
			
			if (e.getActionCommand().equals("Uncheck All")) 
		    {
			   this.selectAllTablebutton.setText("Check All");
			   this.parentPanel.actionPerformed(e);
		    }
	    }
	}
	
	/**
	 * Utility method that set the table width
	 * @param table
	 */
	private void adjustTableWidth(){
		
		//the width of the checkbox column
		TableColumn firstCol = tableSelectionGrid.getColumnModel().getColumn(0);		
		firstCol.setPreferredWidth(80);
		firstCol.setResizable(false);
				
		TableColumn secondCol = tableSelectionGrid.getColumnModel().getColumn(1);
		secondCol.setPreferredWidth(250);
				
		TableColumn thirdCol = tableSelectionGrid.getColumnModel().getColumn(2);
		thirdCol.setPreferredWidth(470);
	}
	
	

	public JTable getTableSelectionGrid() {
		return tableSelectionGrid;
	}

	public void setTableSelectionGrid(JTable tableSelectionGrid) {
		this.tableSelectionGrid = tableSelectionGrid;
	}
	
	public JButton getSelectAllTablebutton() {
		return selectAllTablebutton;
	}

	public JButton getConfirmPartialExportButton() {
		return confirmPartialExportButton;
	}

	public void setConfirmPartialExportButton(JButton confirmPartialExportButton) {
		this.confirmPartialExportButton = confirmPartialExportButton;
	}


}
