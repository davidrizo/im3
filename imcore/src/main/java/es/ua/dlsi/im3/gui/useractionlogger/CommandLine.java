/*
 * Copyright (C) 2014 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ua.dlsi.im3.gui.useractionlogger;

import java.io.File;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 *
 * @author drizo
 */
public class CommandLine {
    public static final void main(String [] args) throws Exception {
	if (args.length != 1 && args.length != 2) {
	    System.err.println("Use: es.ua.dlsi.im2.useractionlogger.CommandLine <folder - recursive - looks for actionlog subfolders> [-interactions]");
	} else {
	    new CommandLine().run(args[0], args.length==1?null:args[1]);
	}
    }
    
    private boolean isActionLogFolder(File file) {
	return file.isDirectory() && "actionlogs".equals(Reader.getFileName(file));
    }
    private File findActionsFolder(File folder) {
	if (isActionLogFolder(folder)) {
	    return folder;
	} else {
	    File[] fList = folder.listFiles();
	    for (File file : fList) {	    
		if (isActionLogFolder(file)) {
		    //System.err.println("Found: " + file.getAbsolutePath());
		    return file;
		}
	    }	    
	    return null;
	}
    }

    private void run(String folder, String option) throws Exception {
	File ffolder = new File(folder);
	if (!ffolder.exists()) {
	    throw new Exception("The folder " + folder + " does not exist");
	}
	if (!ffolder.isDirectory()) {
	    throw new Exception("The folder " + folder + " is not a directory");
	}
	
	File[] fList = ffolder.listFiles();

	SummaryStatistics ssInteractions = new SummaryStatistics();
	SummaryStatistics ssSeconds = new SummaryStatistics();
	SummaryStatistics ssSessions = new SummaryStatistics();
	for (File file : fList) {
	    if (file.isDirectory()) {
		File actionsLogFolder = findActionsFolder(file);
		if (actionsLogFolder != null) {
		    Reader reader = new Reader(actionsLogFolder);
		    ssInteractions.addValue(reader.getAcctInteractions());
		    ssSessions.addValue(reader.getWorkSessions().size());
		    ssSeconds.addValue(reader.getAccTimeSpent().getSeconds());		
		}
		//PrintStream ps = new PrintStream(output);
	    }
	}
	if (option != null && option.equals("-interactions")) {
	    System.out.print("Interactions\t"+((int)ssInteractions.getSum()));
	} else {
	    System.out.println("Number of users analysed:\t" + ssSessions.getN());
	    System.out.println("Work sessions: \t" + ssSessions.getMean() + "\t+-\t" + ssSessions.getStandardDeviation() + "\t[" + ssSessions.getMin() + ", " + ssSessions.getMax() + "]");
	    System.out.println("Interactions: \t" + ssInteractions.getMean() + "\t+-\t" + ssInteractions.getStandardDeviation() + "\t[" + ssInteractions.getMin() + ", " + ssInteractions.getMax() + "]");
	    System.out.println("Seconds: \t" + ssSeconds.getMean() + "\t+-\t" + ssSeconds.getStandardDeviation() + "\t[" + ssSeconds.getMin() + ", " + ssSeconds.getMax() + "]");
	    
	}
    }
}
