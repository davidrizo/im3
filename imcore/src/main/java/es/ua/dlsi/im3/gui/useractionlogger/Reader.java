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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is able to read and parse user action log files
 *
 * @author drizo
 */
public class Reader {

    ArrayList<WorkSession> workSessions;
    WorkSession lastWorkSession;
    UserActionLogEntry lastEntry;

    
    public static String getFileName(File file) {
	return file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(File.separator)+1);
    }
    
    
    public Reader(File logsFolder) throws UserActionLoggerException {
	workSessions = new ArrayList<>();
	if (!logsFolder.exists()) {
	    throw new UserActionLoggerException("Logs folder " + logsFolder.getAbsolutePath() + " does not exist");
	}
	if (!logsFolder.isDirectory()) {
	    throw new UserActionLoggerException("Logs folder " + logsFolder.getAbsolutePath() + " is not a directory");
	}

	try {
	    readLogs(logsFolder);
	} catch (IOException ex) {
	    Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
	    throw new UserActionLoggerException(ex);
	}

    }

    private void readLogs(File logsFolder) throws IOException, UserActionLoggerException {
	Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Reading from logs folder {0}", logsFolder.getAbsolutePath());
	File[] logs = logsFolder.listFiles();
	for (File file : logs) {
	    //System.out.println(">>>" + getFileName(file));
	    if (getFileName(file).startsWith("actions")) {
		readLog(file);
	    }
	}
    }

    private void readLog(File logFile) throws IOException, UserActionLoggerException {
	Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Parsing log {0}", logFile.getName());
	try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
	    for (String line; (line = br.readLine()) != null;) {
		// process the line.
		UserActionLogEntry entry = new UserActionLogEntry(line);
		if (lastEntry == null || entry.getWorkSessionTimeStamp() != lastWorkSession.getTimeStamp()) {
		    lastWorkSession = new WorkSession(entry.getWorkSessionTimeStamp());
		    workSessions.add(lastWorkSession);		    
		}
		lastWorkSession.addEntry(entry);
		lastEntry = entry;
	    }
	    br.close();
    // line is not visible here.
	}
    }

    public ArrayList<WorkSession> getWorkSessions() {
	return workSessions;
    }
    
    public int getAcctInteractions() {
	int sum = 0;
	for (WorkSession workSession : workSessions) {
	    sum += workSession.getInteractions();
	}
	return sum;
    }
    
    public Duration getAccTimeSpent() {
	Duration dur = Duration.ofMillis(0);
	for (WorkSession workSession : workSessions) {
	    dur = dur.plus(workSession.getTimeSpent());
	}
	return dur;
    }    

    
}
