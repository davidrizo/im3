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

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * We are using the mapping: millis = action millis, sourceClass = worksession, sourceMethod = category, message = targetItem, params = {action name, params}
 * It exports the format: <record millis>\t<worksession millis>\t<category>\t<target item>\t<params>
 * @author drizo
 */
public class ActionFormatter extends Formatter {
    public static final String SEPARATOR = "\t";
    public static final char SEPARATOR_CHAR = '\t';
    
    @Override
    public String format(LogRecord record) {
	StringBuilder sb = new StringBuilder();
	sb.append(record.getMillis());
	sb.append(SEPARATOR_CHAR);
	sb.append(record.getSourceClassName()); // worksession
	sb.append(SEPARATOR_CHAR);
	sb.append(record.getSourceMethodName()); // category
	sb.append(SEPARATOR_CHAR);
	sb.append(record.getMessage()); // target item
	if (record.getParameters() != null) {
	    for (Object parameter: record.getParameters()) {
		sb.append(SEPARATOR_CHAR);
		sb.append(parameter);	    
	    }
	}
	sb.append('\n');
	return sb.toString();
    }

}