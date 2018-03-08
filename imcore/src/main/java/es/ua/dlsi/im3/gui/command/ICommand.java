package es.ua.dlsi.im3.gui.command;


/**
 *
 * @author drizo
 */
public interface ICommand {
    void execute(IObservableTaskRunner observer) throws Exception;
    boolean canBeUndone();

    void undo() throws Exception;
    void redo() throws Exception;
    /**
     * Used for menus...
     * @return 
     */
    @Override
    String toString();
    /**
     * Used for state machines
     * @return 
     */
    String getEventName();
}
