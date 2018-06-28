package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.IM3Exception;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

/**
 * It executes a command line process and returns a string with the result of the cmmand line
@author drizo
@date 14/07/2008
 **/
public class CommandLine {
	public static String execShellCommand(File folder, String command) throws IM3Exception {
		try {
		    Process ls_proc = Runtime.getRuntime().exec(command, null, folder);
		    // get its output (your input) stream
		    DataInputStream ls_in = new DataInputStream(ls_proc.getInputStream());
		    DataInputStream ls_err = new DataInputStream(ls_proc.getErrorStream());
		    
		    try {
		    	StringBuilder sb = new StringBuilder();
		    	int car;
		    	while ((car = ls_in.read()) != -1) {
		    		sb.append((char)car);
		    	}

		    	StringBuilder sbe = new StringBuilder();
		    	while ((car = ls_err.read()) != -1) {
		    		sbe.append((char)car);
		    	}
		    	String err = sbe.toString().trim();
		    	if (err.length() > 0) {
		    		System.err.println(err);
		    	}

		    	return sb.toString();
		    } catch (IOException e) {
		    	throw new IM3Exception(e);
		    }
		} catch (IOException e1) {
			throw new IM3Exception(e1);
		}
		
	}
}
