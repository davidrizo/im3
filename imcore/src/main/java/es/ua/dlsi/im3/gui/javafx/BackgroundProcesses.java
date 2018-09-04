package es.ua.dlsi.im3.gui.javafx;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowMessage;
import es.ua.dlsi.im3.gui.javafx.dialogs.WorkIndicatorDialog;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It launches background processes
 * @autor drizo
 */
public class BackgroundProcesses {
    public void launch(Window ownerWindow, String message, String finishedMessage, String errorMessage, Callable<Void> ... processes) {
        LinkedList<Callable<Void>> list = new LinkedList<>();
        for (Callable<Void> process: processes) {
            list.add(process);
        }
        launch(ownerWindow, message, finishedMessage, errorMessage, list);
    }

    public void launch(Window ownerWindow, String message, String finishedMessage, String errorMessage, List<Callable<Void>> processes) {

        WorkIndicatorDialog workIndicatorDialog = new WorkIndicatorDialog(ownerWindow, message);
        workIndicatorDialog.addTaskEndNotification(result -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //TODO Mejor un flash
                    ShowMessage.show(ownerWindow, finishedMessage);
                }
            });
        });

        workIndicatorDialog.exec("", inputParam -> {
            try {
                final ExecutorService executor = Executors.newFixedThreadPool (processes.size());
                executor.invokeAll(processes);
                executor.shutdown();
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, errorMessage, e);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ShowError.show(ownerWindow, errorMessage, e);
                    }
                });
            }
            return new Integer(1);
        });

    }
}
