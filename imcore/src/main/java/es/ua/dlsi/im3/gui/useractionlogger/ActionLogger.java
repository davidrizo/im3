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

import es.ua.dlsi.im3.gui.useractionlogger.actions.UserActionWorkSessionStarted;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It tries to find the actionlogs property (java -Dactionlogsfolder=<log file folder>)
 * @author drizo
 */
public class ActionLogger {
    private static final Logger logger = Logger.getLogger("UserActionLog");
    private static String workSessionTimeStamp;
    
    
    public static void init() throws IOException {
	String actionLogsFolderPath = System.getProperty("actionlogsfolder");
	File path;
	if (actionLogsFolderPath != null) {
	    File folder = new File(actionLogsFolderPath);
	    if (!folder.exists() && !folder.mkdirs()) {
		throw new IOException("Cannot create user action logs folder "  + folder.getAbsolutePath());
	    }
	    
	    path = new File(folder, "actions.log");
	    //path = Paths.get(actionLogsFolderPath, "actions.log");
	    //Logger.getLogger(ActionLogger.class.getName()).log(Level.INFO, "Using default ActionLogger path {0}", path.toFile().getAbsolutePath());
	    Logger.getLogger(ActionLogger.class.getName()).log(Level.INFO, "Using default ActionLogger path {0}", path.getAbsolutePath());
	} else {
	    File folder = new File("actionlogs");
	    if (!folder.exists() && !folder.mkdirs()) {
		throw new IOException("Cannot create user action logs folder "  + folder.getAbsolutePath());
	    }
	    
	    //path = Paths.get("actionlogs", "actions.log");
	    path = new File(folder, "actions.log");
	    //Logger.getLogger(ActionLogger.class.getName()).log(Level.INFO, "Using property provided ActionLogger path {0}", path.toFile().getAbsolutePath());
	    Logger.getLogger(ActionLogger.class.getName()).log(Level.INFO, "Using property provided ActionLogger path {0}", path.getAbsolutePath());
	}
	//Handler fileHandler = new FileHandler(path.toFile().getAbsolutePath(), 1024*5, 10000, true); //TODO ¿Suficientes ficheros?
	Handler fileHandler = new FileHandler(path.getAbsolutePath(), 1024000, 10000, true); //TODO ¿Suficientes ficheros?
	fileHandler.setFormatter(new ActionFormatter());
	Handler [] old = logger.getHandlers();
	for (Handler handler : old) {
	    logger.removeHandler(handler); 
	}
	logger.addHandler(fileHandler); // leave only file handler
	logger.setLevel(Level.FINEST); // avoid other loggers to use this log	
	workSessionTimeStamp = Long.toString(Instant.now().toEpochMilli());
	log(new UserActionWorkSessionStarted(), "");
    }
    
    /**
     * It uses the following mapping: sourceClass = worksession, sourceMethod = category, message = targetItem, params = {action name, params}
     * @param category
     * @param targetItem
     * @param params 
     */
    public static void log(String category, String targetItem, Object ... params) {
	logger.logp(Level.FINEST, workSessionTimeStamp, category, targetItem, params);
    }
    /**
     * It uses the following mapping: sourceClass = worksession, sourceMethod = category, message = targetItem, params = {action name, params}
     * @param category
     * @param targetItem
     * @param params 
     */
    public static void log(UserActionCategory category, String targetItem, Object ... params) {
	logger.logp(Level.FINEST, workSessionTimeStamp, category.getName(), targetItem, params);
    }
    
    /**
     * It uses the following mapping: sourceClass = worksession, sourceMethod = category, message = targetItem, params = {action name, params}
     * @param action
     * @param targetItem 
     */
    public static void log(IAction action, String targetItem) {
	logger.logp(Level.FINEST, workSessionTimeStamp, action.getCategory().getName(), targetItem, action.getFields());
    }
    
}
