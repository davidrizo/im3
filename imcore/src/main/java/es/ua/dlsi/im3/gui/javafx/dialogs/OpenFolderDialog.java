package es.ua.dlsi.im3.gui.javafx.dialogs;

import java.io.File;

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
		fc.setInitialDirectory(new File(lastFolderPath));
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
