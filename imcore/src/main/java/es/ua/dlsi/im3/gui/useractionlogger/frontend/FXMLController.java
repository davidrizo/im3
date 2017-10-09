package es.ua.dlsi.im3.gui.useractionlogger.frontend;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.gui.useractionlogger.Reader;
import es.ua.dlsi.im3.gui.useractionlogger.UserActionLogEntry;
import es.ua.dlsi.im3.gui.useractionlogger.UserActionLoggerException;
import es.ua.dlsi.im3.gui.useractionlogger.WorkSession;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;

public class FXMLController implements Initializable {
    static File lastFolder;
    
    @FXML
    private TextArea textAreaLogs;

    @FXML
    private ListView<WorkSession> lvWorkSessions;
    
    @FXML
    private ListView<String> lvTargetItems;
    
    @FXML
    private Label labelInteractions;

    @FXML
    private Label labelTime;

    @FXML
    private Label labelAccInteractions;

    @FXML
    private Label labelAccTime;
    
    @FXML
    private Label sessionStarted;
    
    private Reader reader;
    
    DateTimeFormatter dateTimeFormatter;
    
    public FXMLController() {
	dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    }
    
    @FXML
    private void handleOpenLogsFolder(ActionEvent event) {
	    DirectoryChooser fc = new DirectoryChooser();
		fc.setTitle("Select the folder with the logs to be parsed");
		if (lastFolder != null) {
		    fc.setInitialDirectory(lastFolder);		    
		}

		File file = fc.showDialog(null);//FXUtils.getActiveWindow()); 
		if (file != null) {
			//lastFolder = file.getParentFile();
			lastFolder = file;			
			
			readFolder(file);
		}		
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	lvWorkSessions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WorkSession>() {

	    @Override
	    public void changed(ObservableValue<? extends WorkSession> observable, WorkSession oldValue, WorkSession newValue) {
		showWorkSessionEntries(newValue);
	    }
	});
	
	lvTargetItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

	    @Override
	    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		showWorkTargetItemEntries(newValue);
	    }
	});	
    }

    private void readFolder(File file) {
	try {
	    reader = new Reader(file);
	    
	    lvWorkSessions.getItems().clear();
	    lvWorkSessions.getItems().addAll(reader.getWorkSessions());
	    
	    this.labelAccInteractions.setText(Integer.toString(reader.getAcctInteractions()));
	    this.labelAccTime.setText(reader.getAccTimeSpent().toString());
	} catch (UserActionLoggerException ex) {
	    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
	    showError("Cannot read the logs folder " + file.getAbsolutePath(), ex);
	    
	}
    }
    
    public static void showError(String message, Throwable e) {
	Alert alert = new Alert(Alert.AlertType.ERROR);
	alert.setTitle("User action logs");
	alert.setHeaderText(message);
	alert.setContentText(e.getLocalizedMessage());

// Create expandable Exception.
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	e.printStackTrace(pw);
	String exceptionText = sw.toString();

	Label label = new Label("+ Info:");

	TextArea textArea = new TextArea(exceptionText);
	textArea.setEditable(false);
	textArea.setWrapText(true);

	textArea.setMaxWidth(Double.MAX_VALUE);
	textArea.setMaxHeight(Double.MAX_VALUE);
	GridPane.setVgrow(textArea, Priority.ALWAYS);
	GridPane.setHgrow(textArea, Priority.ALWAYS);

	GridPane expContent = new GridPane();
	expContent.setMaxWidth(Double.MAX_VALUE);
	expContent.add(label, 0, 0);
	expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
	alert.getDialogPane().setExpandableContent(expContent);

	alert.showAndWait();
    }    
    
    private void showWorkSessionEntries(WorkSession ws) {	
	lvTargetItems.getItems().clear();
	if (ws != null) {
	    lvTargetItems.getItems().addAll(ws.getEntriesGroupedByTargetItem().keySet());
	    labelInteractions.setText(Integer.toString(ws.getInteractions()));
	    labelTime.setText(ws.getTimeSpent().toString());
	    sessionStarted.setText(ws.getDateTime().format(dateTimeFormatter));
	}
	
    }    
    
    private void showWorkTargetItemEntries(String targetItem) {
	textAreaLogs.clear();
	StringBuilder sb = new StringBuilder();
	
	WorkSession workSession = lvWorkSessions.getSelectionModel().selectedItemProperty().get();
	
	ArrayList<UserActionLogEntry> entries = workSession.getEntriesOf(targetItem);
	if (entries != null) {
	    UserActionLogEntry prev = null;
	    for (UserActionLogEntry userActionLogEntry : entries) {
		//sb.append(userActionLogEntry.getDateTime().format(dateTimeFormatter));
		if (prev == null) {
		    sb.append(0);
		} else {
		    sb.append(userActionLogEntry.timeInterval(prev));
		}
		prev = userActionLogEntry;
		sb.append('\t');
		sb.append(userActionLogEntry.toString());
		sb.append('\n');
	    }
	    textAreaLogs.setText(sb.toString());
	}
    }
    
}
