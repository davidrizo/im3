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
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It launches background processes
 * @autor drizo
 */
public class BackgroundProcesses {
    public void launch(Window ownerWindow, String message, String finishedMessage, String errorMessage, boolean waitForProcessesToFinish, Callable<Void> ... processes) {
        LinkedList<Callable<Void>> list = new LinkedList<>();
        for (Callable<Void> process: processes) {
            list.add(process);
        }
        launch(ownerWindow, message, finishedMessage, errorMessage, waitForProcessesToFinish, list);
    }

    public void launch(Window ownerWindow, String message, String finishedMessage, String errorMessage, boolean waitForProcessesToFinish, List<Callable<Void>> processes) {

        WorkIndicatorDialog workIndicatorDialog = new WorkIndicatorDialog(ownerWindow, message);
        workIndicatorDialog.addTaskEndNotification(result -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //TODO Mejor un flash
                    if (finishedMessage != null) {
                        ShowMessage.show(ownerWindow, finishedMessage);
                    }
                }
            });
        });

        workIndicatorDialog.exec("", inputParam -> {
            final ExecutorService executor = Executors.newFixedThreadPool (processes.size());
            try {
                List<Future<Void>> results = executor.invokeAll(processes);
                if (waitForProcessesToFinish) {
                    for (Future<Void> f : results) {
                        f.get(); // just wait for it to finish
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, errorMessage, e);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ShowError.show(ownerWindow, errorMessage, e);
                    }
                });
            }
            executor.shutdown();
            return new Integer(1);
        });
    }
}
