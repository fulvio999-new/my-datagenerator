
package mydatagenerator.model.bean;

/**
 * Bean that contains informations about the FK constraints of a table.
 * The informations are obtained from the MySql information_schema database
 *
 */
public class TableFKconstraintsBean {

	/* the table that has a FK to another table */
	private String parentTableName;
	/* the FK column name of 'tableName' (ie the parent Column) */
	private String parentColumnName;
	
	
	/* the table name referenced by 'tableName' */
	private String referencedTableName;
	
	/* the column name of referenced by another table FK field (ie the child column) */
	private String referencedColumnName;
	
	
	/**
	 * Constructor
	 */
	public TableFKconstraintsBean() {
		
	}
	
	

	public String getParentColumnName() {
		return parentColumnName;
	}



	public void setParentColumnName(String parentColumnName) {
		this.parentColumnName = parentColumnName;
	}



	public String getParentTableName() {
		return parentTableName;
	}


	public void setParentTableName(String parentTableName) {
		this.parentTableName = parentTableName;
	}

	

	public String getReferencedTableName() {
		return referencedTableName;
	}

	public void setReferencedTableName(String referencedTableName) {
		this.referencedTableName = referencedTableName;
	}

	/**
	 * @return The child column
	 */
	public String getReferencedColumnName() {
		return referencedColumnName;
	}

	public void setReferencedColumnName(String referencedColumnName) {
		this.referencedColumnName = referencedColumnName;
	}

}
