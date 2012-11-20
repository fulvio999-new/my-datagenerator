
package mydatagenerator.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * Sub panel of the Cleaning Service panel that offers features to clean the tables with engine of type ARCHIVE.
 * NOTE: the ARCHIVE engine don't support the truncate, delete operations (See MySql official documentation)
 * So that the deletion of that tables is performed with a workaround trying to change temporary the engine type to MyIsam or InnoDB,
 * after the successful deletion the engine type is restored to ARCHIVE.
 * 
 * The user must explicit declare the use of this workaround
 * 
 * IMPORTANT: this method can fail if the user has no the permission to change the engine type or for other reason 
 *
 */
public class CleanArchiveTableSubPanel extends JPanel{		
	
	private static final long serialVersionUID = 1L;	
	
	/* The panel that own this subpanel */
	private CleaningServicePanel parentPanel;
	
	private JLabel headerMessageLabel;
	
	/* The checkbox to enable the ARCHIVE table cleaning */
	private JCheckBox enableArchiveCleaningCheckBox;

	/**
	 * constructor
	 */
	public CleanArchiveTableSubPanel(CleaningServicePanel cleaningServicePanel) {
		
		this.setLayout(new MigLayout("wrap 5")); //we want 5 column
		this.setBorder(BorderFactory.createTitledBorder("ARCHIVE Table Cleaning"));		
		this.parentPanel = cleaningServicePanel;	
		
		headerMessageLabel = new JLabel("<html> With this option the engine type is temporary changed to InnoDB o MyIsam. <b>NOTE</b>: the user must have the necessary permission ! <br/>  </html> ");
		
		enableArchiveCleaningCheckBox = new JCheckBox("Clean ARCHIVE table");
		enableArchiveCleaningCheckBox.setActionCommand("cleanArchive");
		//enableArchiveCleaningCheckBox.addActionListener(this);		
		
		//---- Add the components to the panel
		this.add(headerMessageLabel,"span 5");
		this.add(enableArchiveCleaningCheckBox);
	}
	
	
	public JCheckBox getEnableArchiveCleaningCheckBox() {
		return enableArchiveCleaningCheckBox;
	}

	public void setEnableArchiveCleaningCheckBox(JCheckBox enableArchiveCleaningCheckBox) {
		this.enableArchiveCleaningCheckBox = enableArchiveCleaningCheckBox;
	}

}
