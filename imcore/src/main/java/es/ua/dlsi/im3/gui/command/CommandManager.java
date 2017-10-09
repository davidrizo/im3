package es.ua.dlsi.im3.gui.command;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;


/**
@author drizo
@date 18/11/2011
 @date Changed 08/10/2017 - Remove State machine
 **/
public class CommandManager {
	static final Logger log = Logger.getLogger(CommandManager.class.getName());
	private final ObservableList<ICommand> undosProperty;
	private final ObservableList<ICommand> redosProperty;
	/*private final Stack<ICommand> undos;
	private final Stack<ICommand> redos;*/
	private final StringProperty nextUndoTitle;
	private final StringProperty nextRedoTitle;
	private final StringExpression redoMenuTitleProperty;
	private final StringExpression undoMenuTitleProperty;
	private final String EMPTYSTR = "";
	private BooleanProperty associatedDocumentNeedsSave = new SimpleBooleanProperty(false);
	

	public CommandManager() {
		Stack<ICommand> undos = new Stack<>();
		Stack<ICommand> redos = new Stack<>();
		undosProperty = FXCollections.observableList(undos);
		redosProperty = FXCollections.observableList(redos);
		nextUndoTitle = new SimpleStringProperty();
		nextRedoTitle = new SimpleStringProperty();
		undoMenuTitleProperty = Bindings.concat("Undo ", new When(nextUndoTitle.isEmpty()).then(EMPTYSTR).otherwise(nextUndoTitle));
		redoMenuTitleProperty = Bindings.concat("Redo ", new When(nextRedoTitle.isEmpty()).then(EMPTYSTR).otherwise(nextRedoTitle));
		//Bindings.concat("Undo ", )
		//undoMenuTitleProperty = new When(Bindings.isEmpty(undosProperty)).then("Undo").otherwise("Undo otra cosa");//Bindings.stringValueAt(undosProperty.get(), 0);
		//redoMenuTitleProperty = new When(Bindings.isEmpty(redosProperty)).then("Redo").otherwise("Undo otra cosa");//Bindings.stringValueAt(undosProperty.get(), 0);
	}

	private void pushUndoableCommand(ICommand command) {
	    //undos.push(command);
	    undosProperty.add(0, command);
	    nextUndoTitle.set(command.toString());

	}
	public void undo() throws IM3Exception {
		if (!undosProperty.isEmpty()) {
			//undos.pop();
			ICommand cmd = undosProperty.get(0); // pop
			undosProperty.remove(0);
			log.log(Level.INFO, "Undoing {0}", cmd.toString());

			if (undosProperty.isEmpty()) {
			    nextUndoTitle.set(EMPTYSTR);
			} else {
			    nextUndoTitle.set(undosProperty.get(0).toString());
			}
			
			
			try {			
			    cmd.undo();
			} catch (Exception ex) {
			    Logger.getLogger(CommandManager.class.getName()).log(Level.SEVERE, null, ex);
			    throw new IM3Exception(ex);
			}
			//redos.push(cmd);
			redosProperty.add(0, cmd);
			nextRedoTitle.set(cmd.toString());
			log.log(Level.INFO, "Current size of undo stack {0}", undosProperty.size());
			log.log(Level.INFO, "Current size of redo stack {0}", redosProperty.size());
		} else {
			log.info("Cannot undo, stack empty");
		}
	}
	
	public void redo() throws IM3Exception {
	    if (!redosProperty.isEmpty()) {
		try {
		    //ICommand command = redos.pop();
		    ICommand command = redosProperty.get(0);
		    redosProperty.remove(0);
		    log.log(Level.INFO, "Redoing {0}", command.toString());
		    
		    if (redosProperty.isEmpty()) {
			nextRedoTitle.set(EMPTYSTR);
		    } else {
			nextRedoTitle.set(redosProperty.get(0).toString());
		    }
		    
		    /*if (command instanceof IBackgroundCommand) {
		    executeBackgroundCommand((IBackgroundCommand) command);
		    } else {
		    cmd.redo();
		    }*/
		    command.redo();
		    
		    undosProperty.add(0, command);
		    nextUndoTitle.set(command.toString());

		    
		    log.log(Level.INFO, "Current size of undo stack {0}", undosProperty.size());
		    log.log(Level.INFO, "Current size of redo stack {0}", redosProperty.size());
		    
		} catch (Exception ex) {
		    Logger.getLogger(CommandManager.class.getName()).log(Level.SEVERE, null, ex);
		    throw new IM3Exception(ex);
		}
	    } else {
		log.info("Cannot redo, stack empty");
	    }
	}	
	
	private TaskJFX2 createCommandTask(final IBackgroundCommand command) {
	    log.log(Level.INFO, "Scheduled command task {0}", command.toString());
		TaskJFX2<Void> t = new TaskJFX2() { //TODO Independizar de JFX2 (como state machine)
		    @Override
		    protected Void call() throws Exception {
			try {
			    log.log(Level.INFO, "Executing command {0}", command.toString());
			    command.execute(this);
			    Platform.runLater(new Runnable() { // because it may interact with the GUI
				@Override
				public void run() { 
				    command.onEnd(); 

				}
			    });		    			    
			} catch (final IM3Exception e) {
			    log.log(Level.INFO, "Exception generated from command {0}: {1}", new Object[]{command.toString(), e});
			    Platform.runLater(new Runnable() { // because it may interact with the GUI
				@Override
				public void run() { 
				    command.onException(e); 
				}
			    });
			    throw e;
			}
			return null;
		    }
		};
		return t;
	}
	
	/**
	 * On exception, an ERROR event is fired to the state machine and after that, the onError es invoked
	 * If no error is done, the onEnd is invoked in the end
	 * The command is run in a new thread, if an interaction with the GUI is required, use Platform.runLater
	 * Let the event name of command be "EVENT", the following events are launched to the state machine:
	 * EVENT@RUN when it is started, EVENT@CANCEL when it is cancelled by the user, EVENT@ERROR when an exception is launched,
	 * EVENT (not EVENT@DONE), when it is finished
	 * @param command
	 * @return A task runner that can be bound to any progress property, and that can cancel this task
	 */
	public IObservableTaskRunner executeBackgroundCommand(final IBackgroundCommand command) {
	    //try {
		log.log(Level.INFO, "Executing {0}", command.toString());
		associatedDocumentNeedsSave.set(true);
		TaskJFX2<Void> t = createCommandTask(command);
		t.setOnRunning(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    }
		});
		t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
			if (command.canBeUndone()) {
				pushUndoableCommand(command);
			}

		    }
		});


		new Thread(t).start();
		return t;
	}
	
	/**
	 * @param command
	 * @throws IM3Exception Before the exception is thrown, an ERROR event is fired to the state machine
	 */
	public void executeCommand(final ICommand command) throws IM3Exception {
	    //try {
		log.log(Level.INFO, "Executing {0}", command.toString());
		associatedDocumentNeedsSave.set(true);
		//Cursor currentCursor = FXUtils.getCurrentCursor(); //TODO Independizar de JFX
		try {
		    try {
			//FXUtils.changeCursor(Cursor.WAIT);
			command.execute(null);
		    } catch (Exception ex) {
			Logger.getLogger(CommandManager.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3Exception(ex);
		    }
		    if (command.canBeUndone()) {
			pushUndoableCommand(command);
		    }
		    //FXUtils.changeCursor(currentCursor);
		} catch (IM3Exception e) {
		    //FXUtils.changeCursor(currentCursor);
			Logger.getLogger(CommandManager.class.getName()).log(Level.WARNING, null, e);
			throw e;
		}
	}	
	public BooleanBinding undoAvailableProperty() {
	    return Bindings.isNotEmpty(undosProperty);
	}
	
	public BooleanBinding redoAvailableProperty() {
	    return Bindings.isNotEmpty(redosProperty);
	}

	/*public ObjectProperty<ObservableList<ICommand>> undosProperty() {
	    return undosProperty;
	}

	public ObjectProperty<ObservableList<ICommand>> redosProperty() {
	    return redosProperty;
	}*/

	public StringExpression undoMenuTitleProperty() {
	    return undoMenuTitleProperty;
	}
	public StringExpression redoMenuTitleProperty() {
	    return redoMenuTitleProperty;
	}	
	
	public BooleanProperty commandAppliedProperty() {
	    return associatedDocumentNeedsSave;
	}
	
	public void resetNeedsSave() {
	    associatedDocumentNeedsSave.set(false);
	}

}
