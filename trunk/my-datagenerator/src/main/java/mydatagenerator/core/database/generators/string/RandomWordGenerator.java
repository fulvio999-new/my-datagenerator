
package mydatagenerator.core.database.generators.string;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Generate a random word to insert in the Varchar column.
 * For the string generation is used RandomStringUtils  (org.apache.commons.lang)
 * The length of the word is decided using the right field type
 */
public class RandomWordGenerator {

	/**
	 * Constructor
	 */
	public RandomWordGenerator() {		
		
	}
	
	/** 
	 * @param count The number of character of the string to generate
	 * @return a new random string
	 */
	public String getNextString(int count){
		//randomAlphabetic use only literal character
		return RandomStringUtils.randomAlphabetic(count);
	}

}
