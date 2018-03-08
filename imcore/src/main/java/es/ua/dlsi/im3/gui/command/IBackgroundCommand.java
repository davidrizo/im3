package es.ua.dlsi.im3.gui.command;

/**
 *
 * @author drizo
 */
public interface IBackgroundCommand extends ICommand {
    void onException(Exception e);
    void onEnd();
   
}
