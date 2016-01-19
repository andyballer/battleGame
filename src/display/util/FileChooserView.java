package display.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooserView {
	
	private FileNameExtensionFilter filter = null;
	private File defaultPath = null;
	private String defaultExtension = null;
	
	public void setFileFilter(FileNameExtensionFilter f, String extension) {
		filter = f;
		defaultExtension = extension;
	}
	
	public void setDefaultDirectory(File f) {
		defaultPath = f;
	}
	
	public String loadFile() {
		JFileChooser c = new JFileChooser();
		if (filter != null) c.setFileFilter(filter);
		if (defaultPath != null) c.setCurrentDirectory(defaultPath);
		int rVal = c.showOpenDialog(null);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			return c.getCurrentDirectory().toString() + "/" + c.getSelectedFile().getName();
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {

		}
		return null;
	}
	
	public String saveFile() {
		JFileChooser c = new JFileChooser();
		if (filter != null) c.setFileFilter(filter);
		if (defaultPath != null) c.setCurrentDirectory(defaultPath);
		int rVal = c.showSaveDialog(null);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String name = c.getSelectedFile().getName();
			if (name != null && !name.endsWith(defaultExtension)) name = name.concat(defaultExtension);
			return c.getCurrentDirectory().toString() + "/" + name;
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {

		}	
		return null;
	}
}
