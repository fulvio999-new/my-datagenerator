package mydatagenerator.gui.utils.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Custom file filter used in the JFileChooser. Used to pick-up only folder where save the exported DB content
 *
 */
public class FolderFileFilter extends FileFilter{
	
	public FolderFileFilter() {
		
	}

	@Override
	public boolean accept(File f) {
		return f.isDirectory();
		
	}

	@Override
	public String getDescription() {
		return "Folder only";
	}

}
