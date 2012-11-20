package mydatagenerator.init;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import mydatagenerator.gui.panels.CleaningServicePanel;
import mydatagenerator.gui.panels.DataGeneratorPanel;
import mydatagenerator.gui.panels.ExportPanel;
import mydatagenerator.gui.panels.HelpPanel;
import mydatagenerator.gui.panels.ImportDatasetPanel;
import mydatagenerator.gui.panels.TableUtilPanel;
import mydatagenerator.gui.panels.TargetDatabasePanel;
import mydatagenerator.gui.panels.menu.about.AboutMenuPopUp;



/**
 * Main class that create all the GUI 
 *
 */
public class MyDataGenerator  {
	
   private JFrame mainFrame;	
   private JMenuBar menuBar;
   
   /* The tab panel that contains all the others one */
   private  JTabbedPane mainTabPanel;
   
   private TargetDatabasePanel targetDatabasePanel;
   
   private CleaningServicePanel dataBaseCleanerPanel;
   
   private DataGeneratorPanel dataGeneratorPanel;
   
   private ExportPanel importExportServicePanel;
   
   private ImportDatasetPanel importDatasetPanel;
   
   private TableUtilPanel databaseTableUtilsPanel;
   
   private HelpPanel helpPanel;

  /**
   * Constructor	
   */
  public MyDataGenerator() {	  
	  
	  try {
		 UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
	 
	  } catch (Exception e) {	
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		
		} catch (Exception ee) {			
		
		}
	}
	  
	mainFrame = new JFrame("my-datagenerator 1.0");    
	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	mainFrame.setPreferredSize(new Dimension(1000,700));		
	    
	//------------ Creates a menu bar for the JFrame 
	menuBar = new JMenuBar();
	
	//-------- Menu Bar: Item 2: About -------- 
    JMenu aboutMenu = new JMenu("About");    
    menuBar.add(aboutMenu);    
   
    JMenuItem aboutEntry = new JMenuItem("About");
    
    aboutMenu.add(aboutEntry);    
    aboutEntry.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent event) {        	
        	AboutMenuPopUp aboutMenuPopUp = new AboutMenuPopUp();
        }
    });
	    
	//---------- Add the menubar to the frame
	mainFrame.setJMenuBar(menuBar);	
	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
    
    mainTabPanel = new JTabbedPane(JTabbedPane.LEFT);

    /* The single vertical panel to add  */
    targetDatabasePanel = new TargetDatabasePanel(mainFrame); targetDatabasePanel.setPreferredSize(new Dimension(1000,700));
    dataBaseCleanerPanel = new CleaningServicePanel(mainFrame);
    dataGeneratorPanel = new DataGeneratorPanel(mainFrame);  
    importExportServicePanel = new ExportPanel(mainFrame);
    importDatasetPanel = new ImportDatasetPanel(mainFrame);
    databaseTableUtilsPanel = new TableUtilPanel(mainFrame);
    helpPanel = new HelpPanel(mainFrame);
    
    
    /* Add the single vertical panel at the main panel */
    mainTabPanel.addTab("Target Database", targetDatabasePanel);
    mainTabPanel.addTab("Cleaning Service", dataBaseCleanerPanel);
    mainTabPanel.addTab("Export Service", importExportServicePanel);
    mainTabPanel.addTab("Import Service", importDatasetPanel);
    mainTabPanel.addTab("Table Utils", databaseTableUtilsPanel);
    mainTabPanel.addTab("Data Generator", dataGeneratorPanel);    
    mainTabPanel.addTab("Help", helpPanel);
     
    
    //mainFrame.add(new JScrollPane(mainTabPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));   
    mainFrame.add(mainTabPanel);
    mainFrame.pack();
    mainFrame.setVisible(true);
    
  }

  public static void main(String args[]) {
    new MyDataGenerator();
  }
}