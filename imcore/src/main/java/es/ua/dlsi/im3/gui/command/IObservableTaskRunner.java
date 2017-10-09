package es.ua.dlsi.im3.gui.command;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;

/**
 * @author drizo
 */
public interface IObservableTaskRunner {
    public void updateMessage(String text);
    public void updateTitle(String text);
    public void updateProgress(long workDone, long totalWork);
    public boolean isCancelled();
    public boolean cancelTask();    
    ReadOnlyDoubleProperty taskProgressProperty();
    ReadOnlyStringProperty taskMessageProperty();
}
