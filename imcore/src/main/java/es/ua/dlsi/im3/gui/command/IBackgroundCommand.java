package es.ua.dlsi.im3.gui.command;

/**
 *
 * @author drizo
 */
public interface IBackgroundCommand extends ICommand {
    public void onException(Exception e);
    public void onEnd();
   
}
