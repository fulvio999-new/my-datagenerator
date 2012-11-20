
package mydatagenerator.gui.custom.table.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import mydatagenerator.model.bean.AdvancedMetadataTableInfo;

/**
 * Custom tableModel to show java bean returned from a sql query on the target DB
 * It is used in the "Table Utils" Panel to show some advanced information about table (eg Create options, row count...)
 *
 */
public class AdvancedTableInfoModel  extends AbstractTableModel{	
	
	private static final long serialVersionUID = 1L;
	
	private String[] columnNames = {"Engine","Create Option","Allocated but unused bytes"};
	
	/* The list of beans to display in the table: each table row represents a bean (ie each cell is a bean field value) */
	private ArrayList<AdvancedMetadataTableInfo> tableFieldInfoBeanList = new ArrayList<AdvancedMetadataTableInfo>();
	

	/**
	 * Constructor
	 */
	public AdvancedTableInfoModel() {
		
	}

	public int getRowCount() {
		return tableFieldInfoBeanList.size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Return the value to insert in a cell, depending on the column index
	 */
	public Object getValueAt(int row, int col) {
		
		AdvancedMetadataTableInfo tb = tableFieldInfoBeanList.get(row);
		
		switch (col) {
	      case 0:
	        return tb.getEngine();
	      case 1:
	        return tb.getCreateOptions();
	      //case 2:
	        //return tb.getRow();
	      case 2:
	        return tb.getDataFree();	      
	      default:
	        return null;
	   }
	}

	public ArrayList<AdvancedMetadataTableInfo> getTableFieldInfoBeanList() {
		return tableFieldInfoBeanList;
	}

	public void setTableFieldInfoBeanList(ArrayList<AdvancedMetadataTableInfo> tableFieldInfoBeanList) {
		this.tableFieldInfoBeanList = tableFieldInfoBeanList;
	}
	
	/* Core method that return the name to place in the table header */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	
	public String[] getColumnNames() {
		return columnNames;
	}
	
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

}
