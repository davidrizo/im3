package es.ua.dlsi.im3.core;


/**
 * Used to monitorize time consuming tasks
 * @author drizo
 */
public interface IProgressObserver {
    void logText(String text);
    void setCurrentProgress(long workDone, long totalWork);
    void onEnd();
    
}
