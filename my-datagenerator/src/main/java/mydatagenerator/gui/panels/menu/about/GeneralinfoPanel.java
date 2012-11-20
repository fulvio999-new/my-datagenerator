
package mydatagenerator.gui.panels.menu.about;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A sub-panel that compose the AboutMenuPopUp.
 * Contains some general information about Application
 *
 */
public class GeneralinfoPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private JLabel generalInfoLabel;

	/**
	 * Constructor
	 */
	public GeneralinfoPanel() {
		
		this.setBorder(BorderFactory.createTitledBorder(""));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS)); 
		
		generalInfoLabel = new JLabel("<html> <br/><b> What is</b> <br/> MyDatagenerator is a set of utilty for MySql databases build using some Apache DBunit features (http://www.dbunit.org/ )  <br/><br/> <b>Author: fulvio999@gmail.com</b> </html>");
		
		this.add(generalInfoLabel);		
	}

}
