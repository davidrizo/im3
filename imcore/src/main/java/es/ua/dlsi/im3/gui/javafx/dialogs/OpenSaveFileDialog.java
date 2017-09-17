package es.ua.dlsi.im3.gui.javafx.dialogs;

import java.io.File;
import java.util.prefs.Preferences;

import es.ua.dlsi.im3.core.utils.FileUtils;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
@author drizo
@date 17/02/2012
 **/
public class OpenSaveFileDialog  {
	static final Logger logger = Logger.getLogger(OpenSaveFileDialog.class.getName());
	static final String PROPERTY = "lastFolder";
	
	private static final String ASTERISK = "*.";

    static File getLastFolder() {
	String currentDir = System.getProperty("user.home");
	Preferences prefs = Preferences.userNodeForPackage(OpenSaveFileDialog.class);
	String lastFolderStr = prefs.get(PROPERTY, null);
	if (lastFolderStr == null || lastFolderStr.trim().length() == 0 || !new File(lastFolderStr).canRead()) {
	    logger.log(Level.INFO, "Using last folder ''{0}''", currentDir);		    
	    return new File(currentDir);
	} else {
	    logger.log(Level.INFO, "Using last folder ''{0}''", lastFolderStr);
	    return new File(lastFolderStr);
	}
    }

    static void saveLastFolder(File folder) {
	String lastFolderStr = folder.getAbsolutePath();
	Preferences prefs = Preferences.userNodeForPackage(OpenSaveFileDialog.class);
	logger.log(Level.INFO, "Saving preferences last folder ''{0}''", lastFolderStr);
	prefs.put(PROPERTY, lastFolderStr);
    }

	public File openFile(String title, String filetypeDescription,
			String extension) {
	    File lastFolder = getLastFolder();
	    FileChooser fc = new FileChooser();
	    fc.setTitle(title);
	    fc.setInitialDirectory(lastFolder);
	    if (extension != null && !extension.equals("*")) {
		ExtensionFilter filter = new ExtensionFilter(filetypeDescription, ASTERISK + extension);
		fc.getExtensionFilters().add(filter);
	    }
	    File file = fc.showOpenDialog(null);//FXUtils.getActiveWindow());
	    if (file != null) {
		saveLastFolder(file.getParentFile());
	    }
	    return file;
	}
	public File saveFile(String title,
			String filetypeDescription, String extension) {
		FileChooser fc = new FileChooser();
	    fc.setTitle(title);
	    File lastFolder = getLastFolder();
	    fc.setInitialDirectory(lastFolder);
		ExtensionFilter filter = new ExtensionFilter(filetypeDescription, ASTERISK + extension);
		fc.getExtensionFilters().add(filter);
		
		File file = fc.showSaveDialog(null);//FXUtils.getActiveWindow()); 
		if (file != null) {
		    saveLastFolder(file.getParentFile());
		    // windows does not return the extension
		    StringBuilder ewd = new StringBuilder();
		    ewd.append('.');
		    ewd.append(extension.toLowerCase());
		    if (file.getName() != null 
			    && !file.getName().toLowerCase().endsWith(ewd.toString())) {
			ewd.insert(0, FileUtils.getFileWithoutPath(file.getName()));
			File newFile = new File(file.getParentFile(), ewd.toString());
			logger.info("Changing due to missing extension: " + file.getName() + " for " + newFile.getName());
			file = newFile;
		    }
		}
		return file;
	}
}
