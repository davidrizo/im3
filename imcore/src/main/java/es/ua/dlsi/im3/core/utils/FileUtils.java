/*
 * Created on 19-may-2005
 */
package es.ua.dlsi.im3.core.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * File utilities class
 * @author drizo
 */
public class FileUtils {
    private static final String DOT_STR = ".";
	/**
	 * Empty constants
	 */
	private static final String EMPTY = "empty";
    /**
     * Empty constants
     */
    private static final String EMPTY_STR = "";
	/**
	 * DOT
	 */
	public static final String DOT = DOT_STR;
	/**
	 * Used for the fileExtensions in readMidiFiles
	 */
	public static final String[] MIDS = {"mid"};
	public static final String[] MIDS_KAR = {"mid", "kar"};
	public static final String[] XMLS = {"xml"};

    /**
	 * Recursive read files
	 * @param folder Input folder
	 * @param fileList Output list of file objects
	 * @param fileExtensions Array of accepted file extensions
	 * @throws IOException 
	 */
	public static void readFiles(File folder, ArrayList<File> fileList, final String [] fileExtensions) throws IOException {
		readFiles(folder, fileList, fileExtensions, true);
	}
	/**
	 * Recursive read files
	 * @param folder Input folder
	 * @param fileList Output list of file objects
	 * @param fileExtensions Array of accepted file extensions
	 * @param recursive 
	 * @throws IOException 
	 */
	public static void readFiles(File folder, ArrayList<File> fileList, final String [] fileExtensions, boolean recursive) throws IOException {
	    File [] midiFiles;
	    if (!folder.exists()) {
	    		throw new IOException("The folder '" + folder.toString() + "' does not exist while reading files"); 
	    }
	    if (folder.isDirectory()) {
	    		// read all MIDI files from directory
		    // use only those files whose key can be got from the file name
		    FilenameFilter filter = new FilenameFilter() {
		        public boolean accept(File dir, String name) {
		        		File file = new File(dir, name);
		        		if (name.startsWith(DOT_STR)) { // hidden
		        			return false;
		        		} else if (file.isDirectory()) {
		        			return true;
		        		} else {
		        			for (int i=0; i<fileExtensions.length; i++) {
		        				if (name.toLowerCase().endsWith(fileExtensions[i])) {
		        					return true;
		        				} 
		        			}
		        			return false;
		        		}
		        }
		    };
		    midiFiles = folder.listFiles(filter);
		    for (int i=0; i<midiFiles.length; i++) {
		    		if (midiFiles[i].isDirectory() && recursive) {
		    			readFiles(midiFiles[i], fileList, fileExtensions);
		    		} else {
		    			if (!midiFiles[i].isDirectory()) {
							fileList.add(midiFiles[i]);
						}
		    		}
		    }
	    } else {
	    		fileList.add(folder); // actually is a file
	    }
	}	
	/**
	 * Recursive read files
	 * @param folder Input folder
	 * @param fileList Output list of file objects
	 * @param fileExtension Accepted file extension
	 * @throws IOException 
	 * @deprecated Use readFiles instead
	 */
	public static void readMidiFiles(File folder, ArrayList<File> fileList, final String fileExtension) throws IOException {
		String [] fileExtensions = {fileExtension};
		readFiles(folder, fileList, fileExtensions);
	}
	/**
	 * Recursive read files
	 * @param folder Input folder
	 * @param fileList Output list of file objects
	 * @param fileExtension Accepted file extension
	 * @throws IOException 
	 */
	public static void readFiles(File folder, ArrayList<File> fileList, final String fileExtension) throws IOException {
		String [] fileExtensions = {fileExtension};
		readFiles(folder, fileList, fileExtensions);
	}
	/**
	 *  read files
	 * @param folder Input folder
	 * @param fileList Output list of file objects
	 * @param fileExtension Accepted file extension
	 * @throws IOException 
	 */
	public static void readFiles(File folder, ArrayList<File> fileList, final String fileExtension, boolean recursive) throws IOException {
		String [] fileExtensions = {fileExtension};
		readFiles(folder, fileList, fileExtensions, recursive);
	}	
	/**
	 * @param absolutePath
	 * @return
	 * @throws IOException
	 */
	public static String getFileNameWithoutExtension(String absolutePath) throws IOException {
		int pos = absolutePath.lastIndexOf(DOT);
		if (pos < 0) { // if not found 
			throw new IOException("Cannot find a dot in file name '" + absolutePath + "'");
		}
		return absolutePath.substring(0, pos);
	}
	/**
	 * @param absolutePath
	 * @return
	 * @throws IOException
	 */
	public static String getFileNameExtension(String absolutePath) throws IOException {
		int pos = absolutePath.lastIndexOf(DOT);
		if (pos < 0) { // if not found 
			throw new IOException("Cannot find a dot in file name " + absolutePath);
		}
		return absolutePath.substring(pos+1).toLowerCase();
	}
	/**
	 * Get the file name without the path
	 * @param fileName File with absolute path
	 * @return
	 */
	public static String getFileWithoutPath(String fileName) {
		int pos = fileName.lastIndexOf("/");
		if (pos < 0) {
			pos = -1;
		} 
		
		return fileName.substring(pos + 1);
	}
	/**
	 * Leave only alphanumeric, - and _ character
	 * @param fct
	 * @return
	 */
	public static String leaveValidCaracters(String fct) {
		if (fct == null || fct.length() == 0) {
			return EMPTY;
		}
		StringBuffer sb = new StringBuffer(fct);
		for (int i=sb.length()-1; i>=0; i--) {
			char car = sb.charAt(i);
			if (car == '_' || !Character.isJavaIdentifierPart(car)) { // '_' to avoid problems with latex
				sb.setCharAt(i, '-');
			}
		}
		return sb.toString();
	}
	
	/**
	 * Leave only alphanumeric, - and _ character. Remove those different
	 * @param fct
	 * @return
	 */
	public static String leaveValidCaractersRemoving(String fct) {
		if (fct == null || fct.length() == 0) {
			return EMPTY;
		}
		StringBuffer sb = new StringBuffer(fct);
		for (int i=sb.length()-1; i>=0; i--) {
			char car = sb.charAt(i);
			if (!Character.isJavaIdentifierPart(car)) { 
				sb.deleteCharAt(i);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Leave only aphanumeric
	 * @param fct
	 * @return
	 */
	public static String leaveAlphabeticCaracters(String fct) {
		if (fct == null || fct.length() == 0) {
			return EMPTY;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(fct);
		StringBuffer result = new StringBuffer();
		for (int i=0; i<sb.length(); i++) {
			char car = sb.charAt(i);
			if (Character.isLetter(car)) {
				result.append(car);
			}
		}
		return result.toString();
	}
	/**
	 * Return the list of folders that are children of the specified parent folder
	 * @param parent
	 * @return
	 */
	public static File[] listFolders(File parent) {
	    FileFilter filter = new FileFilter() {
			public boolean accept(File arg0) {
				return arg0.isDirectory();
			}
	    };
		return parent.listFiles(filter);		
	}
	/**
	 * Return the file without the extension and without the path
	 * @param file
     * @return
	 * @throws IOException 
	 */
	public static String getFileWithoutPathOrExtension(File file) throws IOException {
		return getFileWithoutPath(getFileNameWithoutExtension(file.getName()));
	}
	/**
	 * Return the file without the extension and without the path
	 * @return
	 * @throws IOException 
	 */
	public static String getFileWithoutPathOrExtension(String fileName) throws IOException {
		return getFileWithoutPath(getFileNameWithoutExtension(fileName));
	}
	
	/**
	 * It computes fast the number of lines of a text file
	 * @param file
	 * @return
	 */
	public static int computeNumLines(File file) throws IOException {
		LineNumberReader lnr = null;
		int result=0;
		try {
			lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);
			result = lnr.getLineNumber();
			lnr.close();		
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (lnr != null) {
				try {
					lnr.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new IOException(e);
				}
			}
		}
		return result;		
	}

    public static void copy(File fromFile, File targetFile) throws IOException {
	    if (!fromFile.equals(targetFile)) {
            Path from = Paths.get(fromFile.toURI());
            Path to = Paths.get(targetFile.toURI());
            if (targetFile.exists()) {
                targetFile.delete();
            }
            // does not work Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(from, to);
        } else {
            Logger.getLogger(FileUtils.class.getName()).log(Level.INFO, "Skipping copy because both files are the same: " + fromFile.getAbsolutePath() + " and " + targetFile.getAbsolutePath());
        }
    }

    public static String getExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(FileUtils.DOT) != -1 && fileName.lastIndexOf(DOT) != 0)
            return fileName.substring(fileName.lastIndexOf(DOT)+1);
        else return EMPTY_STR;
    }

    static Comparator<File> fileNameComparator = null;
    public static Comparator<File> getFileNameComparator() {
        if (fileNameComparator == null) {
            fileNameComparator = new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
        }
        return fileNameComparator;

    }
}
