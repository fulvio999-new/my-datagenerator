
package mydatagenerator.model.bean;


/**
 * Bean that represents info (metadata) about the fields of a DB table
 * That fields are retrieved with a query like: SHOW FIELDS FROM 'my-table' 
 *
 */
public class MetadataTableInfoBean {
	
	private String tableName;
	
	private String fieldName;
	private String fieldType; //eg varchar(10)  bigint(10)
	private String fieldNullable;
	private String isPK;
	private String defaultValue;
	private String extraInfo; //ie auto_increment if the field is PK

	/**
	 * Constructor
	 */
	public MetadataTableInfoBean() {
		
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldNullable() {
		return fieldNullable;
	}

	public void setFieldNullable(String fieldNullable) {
		this.fieldNullable = fieldNullable;
	}

	public String getIsPK() {
		return isPK;
	}

	public void setIsPK(String isPK) {
		this.isPK = isPK;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

}
