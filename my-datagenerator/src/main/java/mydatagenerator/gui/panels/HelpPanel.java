
package mydatagenerator.gui.panels;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * Create the help panel
 *
 */
public class HelpPanel extends JPanel {	
	
	private static final long serialVersionUID = 1L;
	
	/* The frame that own this panel */
	private JFrame mainFrame;

	/**
	 * Constructor
	 */
	public HelpPanel(JFrame mainFrame) {
		
		this.mainFrame = mainFrame;
			
		this.setBorder(BorderFactory.createTitledBorder("Help"));		       
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		//------------- 1st section ----------
		String whatIsMessage = "<html>  MyDatagenerator is a set of utilty for MySql 5.x databases build using Apache DBunit (http://www.dbunit.org/ ) <br/> <br/>  <html>";
		 
		this.add(new JLabel(whatIsMessage));
		this.add(new JSeparator(SwingConstants.HORIZONTAL));		
		
		//------------- 2nd section ----------		
		String howtoUse ="<html> <b>How use it:</b> <br/> The first step to use any functionality is set a valid Database connection ('Target Database' panel) <br/>" +
				"then chose the panel with the operation that you want perform <br/>" +
				"<b>NOTE: Some operations can last some time, depending on the database features (ie. table amount and size) </b> <br/><br/> </html>";
		
		this.add(new JLabel(howtoUse));
		this.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		//------------- 3rd section (Cleaning service Panel) ----------		
		String cleaningPanel ="<html> <b>Cleaning Panel</b><br/> Clean all the table content of the target database." +
							  " At the end of the operation ALL the tables (with a supported store engine) will be empty. <b>NOTE:</b> " +
							  " the tables with 'ARCHIVE' store engine will'not be clean because the don't support delete operation. <br/><br/>  </html>";
				
		this.add(new JLabel(cleaningPanel));
		this.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		//------------- 4th section (Export service Panel) ----------		
		String exportPanel ="<html> <b>Export Panel</b><br/> Export the full database content as Apache DBunit dataset (ie an xml file). <br/>" +
				            "Also is possible export only some selected tables (ie 'Partial Export' option). For each table is also possible export only a subset of his content by adding a 'sql where condition'. <br/>" +
				            "For example: <br/> the default query for each table is like this: <br/> select * from mytable <br/> to export only a subset modify it (double click on the cell) adding a 'where' condition: <br/> " +
				            "select * from mytable where tableField ='a value' <br/>" +
				            "(Press 'Return' to confirm each modified query) <br/>"+
				            "The content will be placed in the chosen destination folder <br/><br/> </html>";		
						
		this.add(new JLabel(exportPanel));
		this.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		//------------- 5th section (Import service Panel) ----------		
		String importPanel ="<html> <b>Import Panel</b><br/> Import a valid DBunit dataset in the target database. <br/><br/>  </html>";
						
		this.add(new JLabel(importPanel));
		this.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		//------------- 6th section (Import service Panel) ----------		
		String tableUtilsPanel ="<html> <b>Table Utils Panel</b><br/> Show some details about the tables in the target database. <br/>" +
				                "Load the tables list and select one to get additional information. Note: the tables list is ordered by FK <br/><br/>  </html>";
								
		this.add(new JLabel(tableUtilsPanel));
		this.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		//------------- 7th section (Pump configuration Panel) ----------		
		String pumpConfigurationPanel ="<html> <b>Data Generator Panel</b><br/> Configure the data generator to insert random data in all tables of the target database (that must be EMPTY) <br/><br/><br/> </html>";
										
		this.add(new JLabel(pumpConfigurationPanel));			
		//-------------------------		
	}

}
