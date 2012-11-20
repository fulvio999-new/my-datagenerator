
package mydatagenerator.gui.utils.validator;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;


/**
 * Validator for JTextField. Check if the provided value is empty
 *
 * MYJcomponent.setInputVerifier(textFieldValidator);
 *  
 * when the component lose the focus is called the verifier associated at this component
 * Focus is transfered only if the validate method returns true
 *		
 */
public class TextFieldValidator extends InputVerifier {

	/**
	 * Constructor
	 */
	public TextFieldValidator() {
		
	}

	@Override
	public boolean verify(JComponent input) {
		
		if (input instanceof JTextField)
		{			 		 
				 JTextField f = (JTextField) input;
				 f.setForeground(Color.BLACK);
				 
				 if(f.getText().equalsIgnoreCase(""))
				 {	
					 f.setText("REQUIRED FIELD");
					 f.setBorder(BorderFactory.createLineBorder(Color.RED));				 
					 return false;
					 
				 }else{
					 f.setBorder(BorderFactory.createLineBorder(Color.GRAY));
					 return true; 
				 }
					
		}
		return false;		
	}
	
	
//	@Override
//	public boolean verify(JComponent input) {
//		
//		 if (input instanceof JTextField)
//		 {			 		 
//			 JTextField f = (JTextField) input;
//			 f.setForeground(Color.BLACK);
//			 
//			 if(StringUtils.isEmpty(f.getText()))
//			 {	
//				 f.setText("REQUIRED FIELD");
//				 f.setBorder(BorderFactory.createLineBorder(Color.RED));				 
//				 return false;
//				 
//			 }else{
//				 f.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//				 return true; 
//			 }
//				
//		 }
//		
//		return false;
//	}

}
