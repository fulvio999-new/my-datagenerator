package mydatagenerator.gui.utils.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Custom file filter used in the JFileChooser. Used to pick-up only .xml files
 * containing DBunit dataset
 *
 */
public class XmlFileFilter extends FileFilter{
	
	public XmlFileFilter() {
		
	}

	@Override
	public boolean accept(File f) {
		return f.getName().toLowerCase().endsWith(".xml") || f.isDirectory() || f.getName().toLowerCase().endsWith(".XML");		
	}

	@Override
	public String getDescription() {
		return "XML files only";
	}

}
