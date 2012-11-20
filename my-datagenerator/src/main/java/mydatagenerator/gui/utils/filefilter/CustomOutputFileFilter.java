package mydatagenerator.gui.utils.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * FileFiter for sql or txt files
 *
 */
public class CustomOutputFileFilter extends FileFilter{

	
	public CustomOutputFileFilter() {
		
	}

	
	public boolean accept(File f) {
		return f.getName().toLowerCase().endsWith(".sql") || f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");		
	}

	
	public String getDescription() {
		return "sql or txt files only";
	}

}
