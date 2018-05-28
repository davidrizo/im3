/*
 * Copyright (C) 2015 David Rizo Valero
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
package es.ua.dlsi.im3.gui.javafx.dialogs;

import java.util.concurrent.Callable;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IProgressObserver;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @deprecated
 * @author drizo
 */
public abstract class BackgroundTask implements Callable {
    String title;

    DoubleProperty progressProperty = new SimpleDoubleProperty();
    StringProperty messageProperty = new SimpleStringProperty();
    
    public BackgroundTask(String title) {
	this.title = title;
    }
	    
    public abstract void run(IProgressObserver po) throws IM3Exception;
    public String getTitle() {
	return title;
    }

    @Override
    public Object call() throws Exception {
	run(new IProgressObserver() {

	    @Override
	    public void logText(final String text) {
		Platform.runLater(new Runnable() {

		    @Override
		    public void run() {
			messageProperty.set(text);
		    }
		});
	    }

	    @Override
	    public void setCurrentProgress(final long workDone, final long totalWork) {
		Platform.runLater(new Runnable() {

		    @Override
		    public void run() {
			progressProperty.set((double)workDone / (double) totalWork);
		    }
		});
	    }

	    @Override
	    public void onEnd() {		
	    }
	});
	return null;
    }
    
    public DoubleProperty progressProperty() {
	return this.progressProperty;
    }
    
    public StringProperty messageProperty() {
	return this.messageProperty;
    }
}
