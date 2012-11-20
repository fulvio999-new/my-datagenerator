package mydatagenerator.gui.table.editor.custom;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Define a custom editor to edit a table cell editor
 * 
 *
 */
public class TableExporterEditor extends AbstractCellEditor implements TableCellEditor,ActionListener{
	
	
	public TableExporterEditor(){
		
	}

	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	

}
