
package mydatagenerator.gui.panels.menu.about;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import mydatagenerator.gui.utils.link.HtmlLinkAction;

import org.jdesktop.swingx.JXHyperlink;

/**
 * Create a panel with the informations about the third part library used
 *
 */
public class ThirdPartLicense extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
    private static String apacheDbUnitUrl = "http://www.dbunit.org/";    
     
    private static String swingXurl = "http://swingx.java.net/";
     
    private static String migLayouturl = "http://www.miglayout.com/";
    
    private static String log4jUrl = "http://www.apache.org.licenses/";
    
    private static String toolTipMessage = "Open with the browser";
    

	/**
	 * Constructor
	 */
	public ThirdPartLicense() {		
		
		 this.setBorder(BorderFactory.createTitledBorder("Third Part Licenses"));		       
		 this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS)); 
		 
		 //------------ License for DBUNIT ------------
		 String smsHelpMsg = "<html><b><br/>For Apache DBunit license see:</b> <br/> </html>";
		 
		 this.add(new JLabel(smsHelpMsg));		
		 this.add(createLink(apacheDbUnitUrl, toolTipMessage));		 
		 this.add(new JLabel("<html><br/></html>"));
		
		 this.add(new JSeparator(SwingConstants.HORIZONTAL));	
		 
		 //----------- License for swingX library -------------
		 String swingxMsg = "<html><b><br/>For the Swingx library license see: </b> <br/> </html>";
		 
		 this.add(new JLabel(swingxMsg));		 
		 this.add(createLink(swingXurl, toolTipMessage));
		 this.add(new JLabel("<html><br/></html>"));
		 
		 this.add(new JSeparator(SwingConstants.HORIZONTAL));
		 
		 //--------- License for MigLayout  ---------------		 
         String migLayoutMsg = "<html><b><br/>For the Miglayout library license see: </b> <br/> </html>";
		 
		 this.add(new JLabel(migLayoutMsg));		 
		 this.add(createLink(migLayouturl, toolTipMessage));
		 this.add(new JLabel("<html><br/></html>"));
		 
		 this.add(new JSeparator(SwingConstants.HORIZONTAL));
		 
		 //---------- License for Log4J and Apache Commons --------------------
		 String log4jMsg = "<html><b><br/>For the Log4j and Apache Commons library license see: </b> <br/> </html>";
		 
		 this.add(new JLabel(log4jMsg));		 
		 this.add(createLink(log4jUrl, toolTipMessage));
		 this.add(new JLabel("<html><br/></html>"));
		 
		 this.add(new JSeparator(SwingConstants.HORIZONTAL));
				
		 // dummy placeholder for layout adjausting
		 this.add(new JLabel("<html><br/><br/><br/></html>"));
		 this.add(new JLabel("<html><br/><br/><br/></html>"));		
	}
	
	/**
	 * utility method to create html link with a tooltip message associated
	 * @param url
	 * @param toolTip
	 * @return
	 */
	private JXHyperlink createLink(String url, String toolTip){
		
		HtmlLinkAction action = new HtmlLinkAction(url);		
		//JXHyperlink is special Component offered by Swingx library
		JXHyperlink link = new JXHyperlink(action);
		link.setToolTipText(toolTip);
		
		return link;
	}


}
