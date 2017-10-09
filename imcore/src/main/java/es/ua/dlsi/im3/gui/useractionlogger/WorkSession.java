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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author drizo
 */
public class WorkSession {
    ArrayList<UserActionLogEntry> entries;
    long timeStamp;
    LocalDateTime dateTime;
    HashMap<String, ArrayList<UserActionLogEntry>> entriesGroupedByTargetItem;
    

    public WorkSession(long timeStamp) {
	entries = new ArrayList<>();
	entriesGroupedByTargetItem = new HashMap<>();
	this.timeStamp = timeStamp;
	dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
    }

    public ArrayList<UserActionLogEntry> getEntries() {
	return entries;
    }
    
    public boolean isEmpty() {
	return entries.isEmpty();
    }
    
    public void addEntry(UserActionLogEntry entry) throws UserActionLoggerException {
	if (entry.getWorkSessionTimeStamp() != timeStamp) {
	    throw new UserActionLoggerException("The timestamp of the session ("+timeStamp+") is not the same as the timestamp of the entry ("+entry.getWorkSessionTimeStamp()+")");
	}
	entries.add(entry);
	
	ArrayList<UserActionLogEntry> targetItemEntries = entriesGroupedByTargetItem.get(entry.getTargetItem());
	if (targetItemEntries == null) {
	    targetItemEntries = new ArrayList<>();
	    entriesGroupedByTargetItem.put(entry.getTargetItem(), targetItemEntries);
	}
	targetItemEntries.add(entry);
    }

    public long getTimeStamp() {
	return this.timeStamp;
    }
    
    public LocalDateTime getDateTime() {
	return this.dateTime;
    }

    public HashMap<String, ArrayList<UserActionLogEntry>> getEntriesGroupedByTargetItem() {
	return entriesGroupedByTargetItem;
    }

    public ArrayList<UserActionLogEntry> getEntriesOf(String targetItem) {
	return entriesGroupedByTargetItem.get(targetItem);
    }

    public int getInteractions() {
	return entries.size();
    }

    public Duration getTimeSpent() {
	long min = Long.MAX_VALUE;
	long max = Long.MIN_VALUE;
	for (UserActionLogEntry entry : entries) {
	    long t = entry.getTimestamp();
	    min = Math.min(min, t);
	    max = Math.max(max, t);
	}
	return Duration.ofMillis(max - min);
    }
}
