package es.ua.dlsi.im3.gui.command;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.Task;

/**
 *
 * @author drizo
 */
public abstract class TaskJFX2<T> extends Task<T> implements IObservableTaskRunner { 
    @Override
    public void updateMessage(String text) {
	super.updateMessage(text);
    }
    @Override
    public void updateTitle(String text) {
	super.updateTitle(text);
    }
    @Override
    public void updateProgress(long workDone, long totalWork) {
	super.updateProgress(workDone, totalWork);
    }
    @Override
    public boolean cancelTask() {
	return super.cancel();
    }

    @Override
    public ReadOnlyDoubleProperty taskProgressProperty() {
	return super.progressProperty();
    }

    @Override
    public ReadOnlyStringProperty taskMessageProperty() {
	return super.messageProperty();
    }
}
