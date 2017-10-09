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

/**
 *
 * @author drizo
 */
public class UserActionLogEntry {
    long timestamp;
    long workSessionTimeStamp;
    LocalDateTime dateTime;
    String [] fields;
    String category;
    String targetItem;
    /**
     * Parses the string: <record millis>\t<worksession millis>\t<category>\t<target item>\t<params>
     * @param line
     * @throws UserActionLoggerException 
     */
    public UserActionLogEntry(String line) throws UserActionLoggerException {
	String [] cols = line.split(ActionFormatter.SEPARATOR);
	if (cols.length < 4) {
	    throw new UserActionLoggerException("Line '" + line + "' with less than 4 fields, it has " + cols.length);
	}
	try {
	    timestamp = Long.parseLong(cols[0]);
	    dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
	    workSessionTimeStamp = Long.parseLong(cols[1]);
	    category = cols[2];	    
	    targetItem = cols[3];
	    if (cols.length >=4) {
		fields = new String[cols.length - 4];
		for (int i=4; i<cols.length; i++) {
		    fields[i-4] = cols[i];
		}
	    } else {
		fields = null;
	    }
	} catch (Throwable t) {
	    throw new UserActionLoggerException(t);
	}
    }
    
    public Duration timeInterval(UserActionLogEntry previous) {
	return Duration.between(previous.dateTime, dateTime);
    }

    public LocalDateTime getDateTime() {
	return dateTime;
    }

    public long getTimestamp() {
	return timestamp;
    }

    public String[] getFields() {
	return fields;
    }

    public long getWorkSessionTimeStamp() {
	return workSessionTimeStamp;
    }

    public String getTargetItem() {
	return targetItem;
    }

    public String getCategory() {
	return category;
    }

    @Override
    public String toString() {
	return "UserActionLogEntry{" + "timestamp=" + timestamp + ", workSessionTimeStamp=" + workSessionTimeStamp + ", dateTime=" + dateTime + ", fields=" + fields + ", category=" + category + ", targetItem=" + targetItem + '}';
    }


    
    
}
