package es.ua.dlsi.im3.gui.javafx.dialogs;

import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.utils.FileUtils;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

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
        return openFile(null, title, filetypeDescription, extension);
	}

    public File openFile(Window ownerWindow, String title, String filetypeDescription,
                         String extension) {
        File lastFolder = getLastFolder();
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(lastFolder);
        if (extension != null && !extension.equals("*")) {
            ExtensionFilter filter = new ExtensionFilter(filetypeDescription, ASTERISK + extension);
            fc.getExtensionFilters().add(filter);
        }
        File file = fc.showOpenDialog(ownerWindow);
        if (file != null) {
            saveLastFolder(file.getParentFile());
        }
        return file;
    }

    public File openFile(Window ownerWindow, String title, String [] filetypeDescriptions,
                         String [] extensions) {
        if (filetypeDescriptions.length != extensions.length) {
            throw new IM3RuntimeException("The file description length (" + filetypeDescriptions.length + ") show be the same as the extensions length (" + extensions.length + ")");
        }
        File lastFolder = getLastFolder();
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(lastFolder);

        for (int i=0; i<filetypeDescriptions.length; i++) {
                ExtensionFilter filter = new ExtensionFilter(filetypeDescriptions[i], ASTERISK + extensions[i]);
                fc.getExtensionFilters().add(filter);
        }
        File file = fc.showOpenDialog(ownerWindow);
        if (file != null) {
            saveLastFolder(file.getParentFile());
        }
        return file;
    }

    public File openFile(String title, String [] filetypeDescriptions,
                         String [] extensions) {
        return openFile(null, title, filetypeDescriptions, extensions);
    }

    public List<File> openFiles(Window ownerWindow, String title, String [] filetypeDescriptions,
                         String [] extensions) {
        if (filetypeDescriptions.length != extensions.length) {
            throw new IM3RuntimeException("The file description length (" + filetypeDescriptions.length + ") show be the same as the extensions length (" + extensions.length + ")");
        }
        File lastFolder = getLastFolder();
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(lastFolder);

        for (int i=0; i<filetypeDescriptions.length; i++) {
            ExtensionFilter filter = new ExtensionFilter(filetypeDescriptions[i], ASTERISK + extensions[i]);
            fc.getExtensionFilters().add(filter);
        }
        List<File> files = fc.showOpenMultipleDialog(ownerWindow);
        if (files != null && !files.isEmpty()) {
            saveLastFolder(files.get(0).getParentFile());
        }
        return files;
    }

    public List<File> openFiles(String title, String [] filetypeDescriptions,
                                String [] extensions) {
        return openFiles(null, title, filetypeDescriptions, extensions);
    }

    public File saveFile(Window ownerWindow, String title,
			String filetypeDescription, String extension) {
		FileChooser fc = new FileChooser();
	    fc.setTitle(title);
	    File lastFolder = getLastFolder();
	    fc.setInitialDirectory(lastFolder);
		ExtensionFilter filter = new ExtensionFilter(filetypeDescription, ASTERISK + extension);
		fc.getExtensionFilters().add(filter);

		File file = fc.showSaveDialog(ownerWindow);
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

    public File saveFile(String title,
                         String filetypeDescription, String extension) {
        return saveFile(null, title, filetypeDescription, extension);
    }
}
