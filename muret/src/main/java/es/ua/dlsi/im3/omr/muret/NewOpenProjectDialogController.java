package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.gui.javafx.dialogs.FXMLDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenFolderDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.prefs.Preferences;

public class NewOpenProjectDialogController extends FXMLDialog {
    static final String PROPERTY_TRAINING = "OMRBIMODALTRAININGFILE";
    static final String PROPERTY_LASTPROJECT = "OMRLASTPROJECT";

    @FXML
    Label labelProjectFolder;
    @FXML
    Label labelTrainingSetFolder;
    @FXML
    RadioButton rbNotationTypeMensural;
    @FXML
    RadioButton rbNotationTypeModern;
    @FXML
    HBox hboxNotationType;

    File projectFolder;

    File trainingFolder;
    private Preferences prefs;

    public NewOpenProjectDialogController(Stage stage, String title, boolean newProject) {
        super(stage, title, "/fxml/muret/newopenproject.fxml");
        labelProjectFolder.setText("");
        labelTrainingSetFolder.setText("");

        labelProjectFolder.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS); // ellipsis from the beginning
        labelTrainingSetFolder.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);

        String currentDir = System.getProperty("user.home");
        prefs = Preferences.userNodeForPackage(NewOpenProjectDialogController.class);
        String lastTrainingFolderStr = prefs.get(PROPERTY_TRAINING, null);
        if (lastTrainingFolderStr != null) {
            trainingFolder = new File(lastTrainingFolderStr);
            labelTrainingSetFolder.setText(lastTrainingFolderStr);
        }

        String lastProjectStr = prefs.get(PROPERTY_LASTPROJECT, null);
        if (lastProjectStr != null) {
            projectFolder = new File(lastProjectStr);
            labelProjectFolder.setText(lastProjectStr);
        }


        this.btnOKNode.disableProperty().bind(labelTrainingSetFolder.textProperty().isEmpty().or(
                labelProjectFolder.textProperty().isEmpty()
        ));

        hboxNotationType.setDisable(!newProject);
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
    public void handleSelectTrainingSetFolder() {
        OpenFolderDialog dlg = new OpenFolderDialog();
        String lastTrainingProjectFolder = null;
        if (trainingFolder != null) {
            lastTrainingProjectFolder = trainingFolder.getAbsolutePath();
        }
        File folder = dlg.openFolder("Training set folder", lastTrainingProjectFolder);
        if (folder != null) {
            trainingFolder = folder;
            labelTrainingSetFolder.setText(trainingFolder.getAbsolutePath());
            prefs.put(PROPERTY_TRAINING, trainingFolder.getAbsolutePath());
        }

    }

    public File getProjectFolder() {
        return projectFolder;
    }

    public File getTrainingFolder() {
        return trainingFolder;
    }

    public NotationType getNotationType() throws IM3Exception {
        if (rbNotationTypeMensural.isSelected()) {
            return NotationType.eMensural;
        } else if (rbNotationTypeModern.isSelected()) {
            return NotationType.eModern;
        } else {
            throw new IM3Exception("Notation type not selected");
        }
    }
}
