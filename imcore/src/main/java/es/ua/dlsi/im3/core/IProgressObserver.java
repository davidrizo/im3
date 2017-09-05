package es.ua.dlsi.im3.core;


/**
 * Used to monitorize time consuming tasks
 * @author drizo
 */
public interface IProgressObserver {
    public void logText(String text);
    public void setCurrentProgress(long workDone, long totalWork);
    public void onEnd();
    
}
