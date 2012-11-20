
package mydatagenerator.gui.custom.table.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mydatagenerator.model.bean.TableInfoPartialExportBean;

/**
 * Custom table-model used to draw the table where the user can select the table(s) to export and if
 * export or not the full content of the table by adding a where condition to the default sql query. 
 * 
 */
public class PartialExportTableModel extends AbstractTableModel{	
	
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Export","Table Name","Export query (double click to edit and press return to confirm)"};
	
	// The list of bean to show in the jtable
	private List<TableInfoPartialExportBean> rowList = new ArrayList<TableInfoPartialExportBean>();

	/**
	 * Constructor
	 */
	public PartialExportTableModel() {
		
	}

	/* Return the number of column which is composed the table */
	public int getColumnCount() {
	     return columnNames.length;
	}

	/* Return the name of the table column */
	public String getColumnName(int column) {
	     return columnNames[column];
	}

	/* Return the number of row to insert in the table */
	public int getRowCount() {
	    return rowList.size();
	}

	/* Return the single value to insert in the cell placed at the row-column in argument */
	public Object getValueAt(int row, int column) {
		  
	    switch (column) {
		  case 0:
		     return rowList.get(row).isIncludeTable(); //draw a checkbox
	      case 1:
	    	 return rowList.get(row).getTableName();
	      case 2:
	         return rowList.get(row).getExportQuery();	      
	      default:
		        return null; 
		  }	    
	  }

	  /* 
	   * JTable uses this method to determine the default renderer/editor for each cell
	   * Only the first column (0) have a different renderer (ie a checkbox to represents a boolean value) 
	   */
	  public Class getColumnClass(int column) {
		  
	      return (getValueAt(0, column).getClass()); 
		
	  }

	  /* 
	   * We have a cell editable, this method is called when the user edit/change a value in a column. 
	   * The new value inserted in the editable cell (ie column 0 and 2) update the table-model.
	   * NOTE: the tableModel is updated only when the editable cell lose the focus or the user has pressed the return key on the just edited cell
	   */
	  public void setValueAt(Object value, int row, int column) {		
		  
		  if(column == 0){
			  Boolean v = (Boolean)value;
			  rowList.get(row).setIncludeTable(v); //check or not the checkox in the first column depending on the field of the object model
			  
		  // the column 1 is not present because not editable  
			  
		  }else if(column == 2){
			  String v = (String)value;			  
			  rowList.get(row).setExportQuery(v);
		  }
	  }

	  /* Only the column with the selection query and checkbox are editable. Is used the default cell editor  */
	  public boolean isCellEditable(int row, int column) {
		  if(column ==1)			  
			 return false;
		  else 
			 return true;
	  }	  

	 public List<TableInfoPartialExportBean> getRowList() {
		return rowList;
	 }

	 public void setRowList(List<TableInfoPartialExportBean> rowList) {
		this.rowList = rowList;
	 }

}
