package mydatagenerator.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * Sub-panel of the TargetDatabasePanel with the setting to enable/disable the logging features.
 * If the logging is enabled, the operations executed will be 
 *
 */
public class LogConfigurationPanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	/* The parent jpanel that contains this sub-panel */
	private TargetDatabasePanel parentPanel;
	
	private static String[] logLevelComboList = {"TRACE","DEBUG","INFO","WARN","ERROR","FATAL"};
	
	private JLabel titleMessage;
	private JLabel messageLabel; // to display messages to the user
	
	private JCheckBox enableLogCheckBox;
	private JLabel logLevelComboLabel;
	private JComboBox logLevelCombo;	
	private JLabel outLogFileLabel;
	private JTextField outLogFileField;
	private JButton chooseOutLogFileButton;
	private JButton confirmEnableLogButton;
	
	
	/**
	 * Constructor
	 */
	public LogConfigurationPanel(TargetDatabasePanel targetDatabasePanel) {
		
		super();
		
		this.parentPanel = targetDatabasePanel;
		
		this.setLayout(new MigLayout("wrap 5")); // 5 column
		this.setBorder(BorderFactory.createTitledBorder("Log configuration"));
		
		this.titleMessage = new JLabel("For detailed error messages enable the logging system");
				
		this.enableLogCheckBox = new JCheckBox("Enable/Disable Trace Log");		
		this.enableLogCheckBox.setActionCommand("Enable Disable Log");
		this.enableLogCheckBox.addActionListener(this);
		
		this.logLevelComboLabel = new JLabel("Log level:");
		this.logLevelCombo = new JComboBox(logLevelComboList);
		this.logLevelCombo.setEnabled(false); //enabled only if the use check the checkbox
		
		
		this.outLogFileLabel = new JLabel("Log file folder:");
		this.outLogFileField = new JTextField("");
		this.outLogFileField.setEnabled(false); //enabled only if the use check the checkbox
		
		chooseOutLogFileButton = new JButton("Browse");		
		chooseOutLogFileButton.setEnabled(false); //enabled only if the use check the checkbox
		chooseOutLogFileButton.addActionListener(this);
		
		this.confirmEnableLogButton = new JButton("Enable");
		confirmEnableLogButton.setEnabled(false); //enable when the user check the checkbox
		confirmEnableLogButton.addActionListener(this);
		
		this.messageLabel = new JLabel("");
		
		//---- Add the components to the panel
		this.add(titleMessage,"span 5,align center,gapbottom 20");		
		
		this.add(enableLogCheckBox,"wrap");
		
		this.add(logLevelComboLabel,"split 2");
		this.add(logLevelCombo);
		
		this.add(outLogFileLabel);
		this.add(outLogFileField,"span 2,width 700");
		this.add(chooseOutLogFileButton,"width 120");
		
		this.add(messageLabel,"span 5,align center,gapbottom 10");
		
		this.add(new JLabel(),"span 4");
		this.add(confirmEnableLogButton,"width 120");
	}

	/**
	 * Handle the actions and pass it at parent panel to manage them
	 */
	public void actionPerformed(ActionEvent e) {
		
		this.parentPanel.actionPerformed(e);
	}
	

	public TargetDatabasePanel getParentPanel() {
		return parentPanel;
	}

	public void setParentPanel(TargetDatabasePanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	public JCheckBox getEnableLogCheckBox() {
		return enableLogCheckBox;
	}

	public void setEnableLogCheckBox(JCheckBox enableLogCheckBox) {
		this.enableLogCheckBox = enableLogCheckBox;
	}

	public JTextField getOutLogFileField() {
		return outLogFileField;
	}

	public void setOutLogFileField(JTextField outLogFileField) {
		this.outLogFileField = outLogFileField;
	}

	public JButton getChooseOutLogFileButton() {
		return chooseOutLogFileButton;
	}

	public void setChooseOutLogFileButton(JButton chooseOutLogFileButton) {
		this.chooseOutLogFileButton = chooseOutLogFileButton;
	}

	public JComboBox getLogLevelCombo() {
		return logLevelCombo;
	}

	public JLabel getMessageLabel() {
		return messageLabel;
	}

	public void setMessageLabel(JLabel messageLabel) {
		this.messageLabel = messageLabel;
	}

	public JButton getConfirmEnableLogButton() {
		return confirmEnableLogButton;
	}

	public void setConfirmEnableLogButton(JButton confirmEnableLogButton) {
		this.confirmEnableLogButton = confirmEnableLogButton;
	}

	public void setLogLevelCombo(JComboBox logLevelCombo) {
		this.logLevelCombo = logLevelCombo;
	}

}
