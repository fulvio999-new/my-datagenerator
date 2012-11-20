
package mydatagenerator.init;


/**
 * Entry point
 *
 */
public class StartApp {

	/**
	 * Start the application
	 */
	public static void main(String[] args) {
		
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				
	            public void run() {
	            	MyDataGenerator dataPump = new MyDataGenerator();
	            }
	        });
	}

}
