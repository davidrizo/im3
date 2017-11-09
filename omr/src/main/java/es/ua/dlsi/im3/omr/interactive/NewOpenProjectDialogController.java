package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.gui.javafx.dialogs.FXMLDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenFolderDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.stage.Stage;

import java.io.File;
import java.util.prefs.Preferences;

public class NewOpenProjectDialogController extends FXMLDialog {
    static final String PROPERTY = "OMRBIMODALTRAININGFILE";

    @FXML
    Label labelProjectFolder;
    @FXML
    Label labelTrainingSetFile;

    File projectFolder;

    File trainingFile;
    private Preferences prefs;

    public NewOpenProjectDialogController(Stage stage) {
        super(stage, "New project", "/fxml/newopenproject.fxml");
        labelProjectFolder.setText("");
        labelTrainingSetFile.setText("");

        labelProjectFolder.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS); // ellipsis from the beginning
        labelTrainingSetFile.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);

        String currentDir = System.getProperty("user.home");
        prefs = Preferences.userNodeForPackage(NewOpenProjectDialogController.class);
        String lastTrainingFileStr = prefs.get(PROPERTY, null);
        if (lastTrainingFileStr != null) {
            trainingFile = new File(lastTrainingFileStr);
            labelTrainingSetFile.setText(lastTrainingFileStr);
        }

        this.btnOKNode.disableProperty().bind(labelTrainingSetFile.textProperty().isEmpty().or(
                labelProjectFolder.textProperty().isEmpty()
        ));
    }

    @FXML
    public void handleSelectFolder() {
        OpenFolderDialog dlg = new OpenFolderDialog();
        projectFolder = dlg.openFolder("Create a new folder for the project");
        if (projectFolder != null) {
            labelProjectFolder.setText(projectFolder.getAbsolutePath());
        }
    }
    @FXML
    public void handleSelectTrainingSetFile() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        trainingFile = dlg.openFile("Training set file", "Training dataset", "train");
        if (trainingFile != null) {
            labelTrainingSetFile.setText(trainingFile.getAbsolutePath());
            prefs.put(PROPERTY, trainingFile.getAbsolutePath());
        }

    }

    public File getProjectFolder() {
        return projectFolder;
    }

    public File getTrainingFile() {
        return trainingFile;
    }
}
