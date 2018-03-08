package es.ua.dlsi.im3.gui.command;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;

/**
 * @author drizo
 */
public interface IObservableTaskRunner {
    void updateMessage(String text);
    void updateTitle(String text);
    void updateProgress(long workDone, long totalWork);
    boolean isCancelled();
    boolean cancelTask();
    ReadOnlyDoubleProperty taskProgressProperty();
    ReadOnlyStringProperty taskMessageProperty();
}
