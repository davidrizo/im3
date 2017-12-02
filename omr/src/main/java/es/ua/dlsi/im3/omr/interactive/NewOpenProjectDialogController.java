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
    static final String PROPERTY_TRAINING = "OMRBIMODALTRAININGFILE";
    static final String PROPERTY_LASTPROJECT = "OMRLASTPROJECT";

    @FXML
    Label labelProjectFolder;
    @FXML
    Label labelTrainingSetFile;

    File projectFolder;

    File trainingFile;
    private Preferences prefs;

    public NewOpenProjectDialogController(Stage stage, String title) {
        super(stage, title, "/fxml/newopenproject.fxml");
        labelProjectFolder.setText("");
        labelTrainingSetFile.setText("");

        labelProjectFolder.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS); // ellipsis from the beginning
        labelTrainingSetFile.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);

        String currentDir = System.getProperty("user.home");
        prefs = Preferences.userNodeForPackage(NewOpenProjectDialogController.class);
        String lastTrainingFileStr = prefs.get(PROPERTY_TRAINING, null);
        if (lastTrainingFileStr != null) {
            trainingFile = new File(lastTrainingFileStr);
            labelTrainingSetFile.setText(lastTrainingFileStr);
        }

        String lastProjectStr = prefs.get(PROPERTY_LASTPROJECT, null);
        if (lastProjectStr != null) {
            projectFolder = new File(lastProjectStr);
            labelProjectFolder.setText(lastProjectStr);
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
            prefs.put(PROPERTY_LASTPROJECT, projectFolder.getAbsolutePath());
        }
    }
    @FXML
    public void handleSelectTrainingSetFile() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        trainingFile = dlg.openFile("Training set file", "Training dataset", "train");
        if (trainingFile != null) {
            labelTrainingSetFile.setText(trainingFile.getAbsolutePath());
            prefs.put(PROPERTY_TRAINING, trainingFile.getAbsolutePath());
        }

    }

    public File getProjectFolder() {
        return projectFolder;
    }

    public File getTrainingFile() {
        return trainingFile;
    }
}
