package es.ua.dlsi.im3.gui.javafx.dialogs;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.stage.DirectoryChooser;


/**
@author drizo
@date 17/02/2012
 **/
public class OpenFolderDialog {
	
	public OpenFolderDialog() {
	}
	public File openFolder(String title, String lastFolderPath) {
		//FileChooser fc = new FileChooser();
		DirectoryChooser fc = new DirectoryChooser();
		fc.setTitle(title);
		if (lastFolderPath != null) {
		    File initialDirectory = new File(lastFolderPath);
		    if (initialDirectory.exists() && initialDirectory.isDirectory()) {
                fc.setInitialDirectory(new File(lastFolderPath));
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot set initial directory {0}", lastFolderPath);
            }
        }
		File file = fc.showDialog(null);
		File lastFolder = null;
		if (file != null) {
			//lastFolder = file.getParentFile();
			lastFolder = file;
			OpenSaveFileDialog.saveLastFolder(file);
		}
		return lastFolder;
	}
	public File openFolder(String title) {
		return openFolder(title, OpenSaveFileDialog.getLastFolder().getAbsolutePath());
	}
}
