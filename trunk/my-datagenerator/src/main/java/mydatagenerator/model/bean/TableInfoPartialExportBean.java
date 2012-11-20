
package mydatagenerator.model.bean;

/**
 * Bean that represents a row showed in the partial export dataSet 
 *
 */
public class TableInfoPartialExportBean {	
	
	private boolean includeTable;
	private String tableName;
	private String exportQuery;

	/**
	 * Constructor
	 */
	public TableInfoPartialExportBean() {
		
	}
	

	public boolean isIncludeTable() {
		return includeTable;
	}

	public void setIncludeTable(boolean includeTable) {
		this.includeTable = includeTable;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getExportQuery() {
		return exportQuery;
	}

	public void setExportQuery(String exportQuery) {
		this.exportQuery = exportQuery;
	}

}
