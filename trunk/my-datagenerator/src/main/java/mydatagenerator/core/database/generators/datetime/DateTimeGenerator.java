
package mydatagenerator.core.database.generators.datetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Generator for the data type "DATETIME" or similar
 *
 */
public class DateTimeGenerator {	
	
	// formatter used for date/datetime fields type
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	
	// formatter used for timestamp fields type
	private static SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static Random RANDOM_GENERATOR = new Random();

	/**
	 * Constructor
	 */
	public DateTimeGenerator() {
		
	}
	
	/**
	 * Return the next value
	 * @return
	 */
	public String getNextDateRandomValue(){		
		
		Calendar c = Calendar.getInstance();				
		c.add(Calendar.DAY_OF_YEAR, RANDOM_GENERATOR.nextInt(6)); //add an offset
		
		return SDF.format(c.getTime());		
	}
	
	/**
	 * Return the next value
	 * @return
	 */
	public String getNextTimeStampRandomValue(){		
		
		Calendar c = Calendar.getInstance();				
		c.add(Calendar.SECOND, RANDOM_GENERATOR.nextInt(2000)); //add an offset
		
		return SDF2.format(c.getTime());		
	}

	
}
