package es.ua.dlsi.im3.gui.command;


/**
 *
 * @author drizo
 */
public interface ICommand {
    void execute(IObservableTaskRunner observer) throws Exception;
    boolean canBeUndone();

    public void undo() throws Exception;
    public void redo() throws Exception;
    /**
     * Used for menus...
     * @return 
     */
    @Override
    public String toString();
    /**
     * Used for state machines
     * @return 
     */
    public String getEventName();
}
