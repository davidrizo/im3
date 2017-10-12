package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.OMRApp;
import es.ua.dlsi.im3.omr.model.Symbol;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;


import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It contains the trace interaction of the symbols to copy from the source score
 */
public class StaffSymbolsInteraction<SymbolType> {
    private final OMRStaff<SymbolType> omrStaff;
    private final DoubleProperty symbolCompleteTimerValue;
    private Timer timer;
    private final Pane marksPane;
    private boolean enabled;
    private boolean drawing;

    public StaffSymbolsInteraction(OMRStaff<SymbolType> omrStaff, DoubleProperty symbolCompleteTimerValue) {
        this.omrStaff = omrStaff;
        this.marksPane = omrStaff.getOmrController().getMarksPane();
        enabled = true;
        drawing = false;
        this.symbolCompleteTimerValue = symbolCompleteTimerValue;
        init();
    }

    private void init() {
        marksPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (enabled && omrStaff.contains(t.getX(), t.getY())) {
                    drawing = true;
                    startStroke();
                    System.out.println("DRAW start " + t.getX());
                    beginDrawing(t.getX(), t.getY());
                }
            }
        });

        marksPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (enabled && drawing && omrStaff.contains(t.getX(), t.getY())) {
                    if (t.isPrimaryButtonDown()) {
                        System.out.println("DRAW go " + t.getX());
                        continueStroke(t.getX(), t.getY());
                    }
                }
            }

        });

        marksPane.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (enabled && drawing && omrStaff.contains(t.getX(), t.getY())) {
                    try {
                        drawing = false;
                        endStroke();
                    } catch (IM3Exception ex) {
                        Logger.getLogger(StaffSymbolsInteraction.class.getName()).log(Level.SEVERE, null, ex);
                        ShowError.show(OMRApp.getMainStage(), "Cannot end stroke", ex);
                    }
                }
            }
        });
    }

    private void continueStroke(double x, double y) {
        continueDrawing(x, y);
    }

    private void startStroke() {
        cancelTimer();
        if (omrStaff.getCurrentSymbolView() == null) {
            try {
                omrStaff.createNewSymbol();
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot paint symbol", e);
            }
        }
        omrStaff.getCurrentSymbolView().addNewStroke();
    }

    private void endStroke() throws IM3Exception {
        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
        TimerTask completeSymbolTask = new TimerTask() {
            @Override
            public void run() {
                Logger.getLogger(StaffSymbolsInteraction.class.getName()).log(Level.INFO, "Time expired, symbol complete");
                // the timer runs in other thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        doSymbolComplete();
                    }
                });

            }
        };
        // FIXME: 12/10/17 Que se muera cuando se cierre la aplicación
        timer.schedule(completeSymbolTask, (long) (symbolCompleteTimerValue.getValue() * 1000.0));
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void beginDrawing(double x, double y) {
        omrStaff.getCurrentSymbolView().getCurrentStrokeView().addPoint(x, y);
    }

    private void continueDrawing(double x, double y) {
        omrStaff.getCurrentSymbolView().getCurrentStrokeView().addPoint(x, y);
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    private void doSymbolComplete() {
        ICommand cmd = new ICommand() {
            Symbol<SymbolType> symbol;

            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                symbol = omrStaff.newSymbolComplete();
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                omrStaff.removeSymbol(symbol);
            }

            @Override
            public void redo() throws Exception {
                omrStaff.addSymbol(symbol);
            }

            @Override
            public String getEventName() {
                return "SYMBOL_COMPLETE";
            }
        };
        try {
            omrStaff.getOmrController().getCommandManager().executeCommand(cmd);
        } catch (IM3Exception ex) {
            Logger.getLogger(StaffSymbolsInteraction.class.getName()).log(Level.SEVERE, null, ex);
            ShowError.show(OMRApp.getMainStage(), "Cannot complete symbol", ex);
        }
        cancelTimer();
    }
}
