
package mydatagenerator.gui.custom.table.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import mydatagenerator.model.bean.MetadataTableInfoBean;


/**
 * Custom tableModel to show java bean returned from a sql query on the target DB
 * It is used in the "Table Utils" Panel
 *
 */
public class DatabaseTableInfoModel extends AbstractTableModel{
	
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Field Name","Type","Nullable","Key","Default","Extra"};
	
	/* The list of beans to display in the table: each table row represents a bean (ie each cell is a bean field value) */
	private ArrayList<MetadataTableInfoBean> tableFieldInfoBeanList = new ArrayList<MetadataTableInfoBean>();
	
	/* constructor */
	public DatabaseTableInfoModel(){
		
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}
	
	public int getRowCount() {
		return tableFieldInfoBeanList.size();
	}
	
	/* Core method that return the name to place in the table header */
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	/**
	 * Return the value to insert in a cell, depending on the column index
	 */
	public Object getValueAt(int row, int col) {
		
		MetadataTableInfoBean tb = tableFieldInfoBeanList.get(row);
		
		switch (col) {
	      case 0:
	        return tb.getFieldName();
	      case 1:
	        return tb.getFieldType();
	      case 2:
	        return tb.getFieldNullable();
	      case 3:
	        return tb.getIsPK();
	      case 4:
	    	return tb.getDefaultValue(); 
	      case 5:
	    	  return tb.getExtraInfo(); 
	      default:
	        return null;
	   }		
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public ArrayList<MetadataTableInfoBean> getTableFieldInfoBeanList() {
		return tableFieldInfoBeanList;
	}

	public void setTableFieldInfoBeanList(ArrayList<MetadataTableInfoBean> tableFieldInfoBeanList) {
		this.tableFieldInfoBeanList = tableFieldInfoBeanList;
	}

	

}
