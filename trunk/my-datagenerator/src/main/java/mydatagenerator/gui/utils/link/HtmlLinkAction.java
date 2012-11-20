
package mydatagenerator.gui.utils.link;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;


 /**
  * Action that handle the click event on the html link produced with Swingx library. That link are placed in the Help panel 
  * 
  */
public class HtmlLinkAction  extends AbstractAction{

	private static final long serialVersionUID = 1L;

	/* Constructor */
	public HtmlLinkAction(String url) {			
		super.putValue(Action.NAME, url); //the url to open
		super.putValue(Action.SHORT_DESCRIPTION, ""); //the tooltip showed over the link
	}

	/**
	 * Action that open a browser to the provided url
	 */
	public void actionPerformed(ActionEvent e) {
		
		 //System.out.println("Clicked the link: "+super.getValue(Action.NAME));
		
		 if (Desktop.isDesktopSupported()) {
		      try {
		    	//open the default browser (if the running JRE support this features)
		        Desktop.getDesktop().browse(new URI((String)super.getValue(Action.NAME)));	
		        
		      } catch (Exception ex) {
		    	  
		    	 //---- If the "Desktop" functionality is not supported:using a workaround ---- 
		    	  
		    	 String osName = System.getProperty("os.name");
	             try {
	                     if (osName.startsWith("Windows"))
	                         Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + super.getValue(Action.NAME));
	                     
	                    else {
	                         String[] browsers = { "firefox", "opera", "konqueror","epiphany", "mozilla", "netscape" };
	                         String browser = null;
	                             for (int count = 0; count < browsers.length && browser == null; count++)
	                                  if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0)
	                                      browser = browsers[count];
	                             Runtime.getRuntime().exec(new String[] { browser, (String) super.getValue(Action.NAME) });
	                        }
	                } catch (Exception exc) {
	                        JOptionPane.showMessageDialog(null, "Error in opening browser"+ ":\n" + exc.getLocalizedMessage());
	                }			    	  
		    	  
		      }
		 } else { 
			 JOptionPane.showMessageDialog(null, "Error in opening browser"+ ":\n" + (String) super.getValue(Action.NAME)+ " Open manually");			       	
		 }			
	}
}


