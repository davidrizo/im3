package es.ua.dlsi.im3.gui.javafx.dialogs;

import es.ua.dlsi.im3.gui.javafx.TaskJFX2;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.layout.Priority;

/**
 * @deprecated Use WorkIndicatorDialog
 * @author drizo
 * @date 07/11/2011
 *
 */
public class ProgressDialog {
    protected final Dialog dialog;
    private final BackgroundTask[] tasks;
    Button btnCancel;
    ProgressBar[] progressBars;
    Text[] currentActionText;
    VBox vbox;
    private Service<Void> service;
    private String title;

    /**
     * 
     * @param title
     * @param message
     * @param showIndeterminateProgress If true, instead of getting the progress from the task it leaves it indeterminate
     * @param tasks 
     */
    public ProgressDialog(String title, String message, boolean showIndeterminateProgress, BackgroundTask... tasks) {
        this.dialog = new Dialog();
        this.title = title;
        dialog.setTitle(title);
        dialog.setContentText(message);
	    progressBars = new ProgressBar[tasks.length];
	    currentActionText = new Text[tasks.length];
        this.tasks = tasks;

	    for (int i = 0; i < tasks.length; i++) {
	        currentActionText[i] = new Text();
	        progressBars[i] = new ProgressBar();
	    }

	    for (int i = 0; i < tasks.length; i++) {
	        if (!showIndeterminateProgress) {
		        progressBars[i].progressProperty().bind(tasks[i].progressProperty());
	        } else {
		        progressBars[i].setProgress(-1);
	        }
	        currentActionText[i].textProperty().bind(tasks[i].messageProperty());
	    }
	    buildContent();
        addButtons();
    }

    /**
     * Method invoked when the Cancel button has been pressed We have used this
     * callback instead of returning a boolean to showModal because javafx does
     * not block the showModal call
     */
    protected void onCancel() {
        service.cancel();
	    dialog.close();
    }

    protected void addButtons() {
	    btnCancel = new Button("Cancel");
	    btnCancel.setOnMouseClicked(new EventHandler<Event>() {
	        @Override
	            public void handle(Event event) {
                    onCancel();
		            dialog.close();
	            }
	    });
	    vbox.getChildren().add(btnCancel);
    }

    //TODO Tamanyo
    protected void buildContent() {
	    BorderPane bp = new BorderPane();
	    vbox = new VBox();
	    vbox.setSpacing(15);
	
        for (int i = 0; i < tasks.length; i++) {
            HBox hb = new HBox();
            hb.setSpacing(10);
            hb.setAlignment(Pos.CENTER);
            Label label = new Label();
            HBox.setHgrow(label, Priority.ALWAYS);
            label.setText(tasks[i].getTitle());
            hb.getChildren().add(label);
            hb.getChildren().add(progressBars[i]);
            HBox.setHgrow(progressBars[i], Priority.ALWAYS);
            hb.getChildren().add(currentActionText[i]);
            HBox.setHgrow(currentActionText[i], Priority.ALWAYS);
            vbox.getChildren().addAll(hb);
            VBox.setVgrow(hb, Priority.ALWAYS);
        }

	    bp.setCenter(vbox);
	    dialog.getDialogPane().getChildren().add(bp);
    }

    public void showModalAndRunBackgroundTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Logger.getLogger(ProgressDialog.class.getName()).log(Level.INFO, "Creating executor");
                ExecutorService executor = Executors.newFixedThreadPool(tasks.length);
                CompletionService compService = new ExecutorCompletionService<>(executor);
                for (int i = 0; i < tasks.length; i++) {
                    Logger.getLogger(ProgressDialog.class.getName()).log(Level.INFO, "Submiting task {0}", tasks[i].getTitle());
                    compService.submit(tasks[i]);
                }
                for (int i = 0; i < tasks.length; i++) {
                    Future future = compService.take();
                    future.get(); // in order to capture the exception
                    Logger.getLogger(ProgressDialog.class.getName()).log(Level.INFO, "IBackgroundTask {0}/{1} finished", new Object[]{i + 1, tasks.length});
                }
                return null;
            }
        };

        service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return task;
            }
        };

        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ShowMessage.show(null, "Task done!");
                        dialog.close();
                    }
                });
            }
        });

        service.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ShowError.show(null, "Error executing task", service.getException());
                        dialog.close();
                    }
                });

            }
        });

        service.setOnRunning(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.showAndWait();
                    }
                });
            }
        });
        service.start();
    }

}
