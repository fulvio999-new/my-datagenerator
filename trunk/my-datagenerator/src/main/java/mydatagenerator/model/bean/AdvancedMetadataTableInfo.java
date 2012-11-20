
package mydatagenerator.model.bean;

/**
 * Bean that represents ADVANCED info (metadata) about a DB table
 * That fields are retrieved with a query like: show table status where Name ='my-table'
 *
 */
public class AdvancedMetadataTableInfo {
	
	private String createOptions; //eg "Partitioned if the table is partitioned"
	
	private String engine; //eg innoDB
	
	private String dataFree; //The number of allocated but unused bytes. 

	/**
	 * Constructor
	 */
	public AdvancedMetadataTableInfo() {
		
	}
	

	public String getCreateOptions() {
		return createOptions;
	}

	public void setCreateOptions(String createOptions) {
		this.createOptions = createOptions;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getDataFree() {
		return dataFree;
	}

	public void setDataFree(String dataFree) {
		this.dataFree = dataFree;
	}

}
