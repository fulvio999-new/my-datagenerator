
package mydatagenerator.core.database.generators.numeric;


/**
 * 
 *
 */
public class FixRandomNumberGenerator {
	
	
	private int numberToInsert;

	/**
	 * Constructor
	 */
	public FixRandomNumberGenerator(int numberToInsert) {		
		numberToInsert = numberToInsert;
	}
	
	
	public int getValue(){
		return this.numberToInsert;
	}	

}
