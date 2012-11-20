
package mydatagenerator.core.database.generators.numeric;

import java.util.Random;

/**
 * 
 *
 */
public class RandomNumberGenerator {
	
	private Random randomGenerator;	
	

	/**
	 * Constructor
	 */
	public RandomNumberGenerator() {
		this.randomGenerator = new Random();			
	}

	/** 
	 * @return Return a int value between 0 and 'recordToInsert' chosen by the user with a dedicated spinner widget
	 */
	public int getNexIntValue(int recordToInsert){
		return randomGenerator.nextInt(recordToInsert);
	}
	
	/** 
	 * @param offset  the offset to sum at the generated double, because 'nextDouble' api return a number between 0 and 1
	 * @return Return a new random double value
	 */
	public double getNexDoubleValue(int offset){
		return randomGenerator.nextDouble()+(double)offset;
	}
	
	public long getNexLongValue(){
		return randomGenerator.nextLong();
	}
	

}
