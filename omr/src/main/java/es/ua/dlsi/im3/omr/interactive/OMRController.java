package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenFolderDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRProject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class OMRController implements Initializable {
    @FXML
    MenuItem menuSave;

    @FXML
    MenuItem menuAddImage;

    @FXML
    MenuItem menuShowHideProjectImages;

    @FXML
    ListView lvImages;

    ObjectProperty<OMRProject> project;

    public OMRController() {
        project = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuSave.disableProperty().bind(project.isNull());
        menuAddImage.disableProperty().bind(project.isNull());

        lvImages.setCellFactory(new Callback<ListView<OMRPage>, ListCell<OMRPage>>() {
            @Override
            public ListCell<OMRPage> call(ListView<OMRPage> param) {
                return new OMRPageListCell();
            }
        });

    }

    @FXML
    private void handleQuit() {
        // TODO: 9/10/17 Preguntar si hay algo abierto 
        OMRApp.getMainStage().close();
    }

    @FXML
    private void handleNewProject() {
        OpenFolderDialog dlg = new OpenFolderDialog();
        File folder = dlg.openFolder("Create a new folder for the project");
        if (folder != null) {
            try {
                project.set(new OMRProject(folder));
                loadProject();
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot create project", e);
            }
        }
    }

    @FXML
    private void handleOpenProject() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File mrt = dlg.openFile("Select the main file of the project", "MURET files", "mrt");

        if (mrt != null) {
            try {
                project.set(OMRProject.load(mrt.getParentFile()));
                loadProject();
            } catch (IM3Exception e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot open project", e);
            }
        }
    }

    private void loadProject() throws IM3Exception {
        OMRApp.getMainStage().setTitle("MURET - " + project.get().getName());
        lvImages.setItems(project.get().pagesProperty());
    }

    @FXML
    private void handleSave() {
        if (!project.isNull().get()) {
            try {
                project.get().save();
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot save project", e);
            }
        }
    }

    @FXML
    private void handleClose() {
        project.setValue(null);
        // TODO: 9/10/17 Cerrar ventanas....
    }

    @FXML
    private void handleImage() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File jpg = dlg.openFile("Select an image", "JPG", "jpg");

        if (jpg != null) {
            try {
                project.get().addPage(jpg);
            } catch (IM3Exception e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot add image", e);
            }
        }
    }

    @FXML
    private void handleShowHideProjectImages() {
        lvImages.setVisible(!lvImages.isVisible());

        if (lvImages.isVisible()) {
            menuShowHideProjectImages.setText("Hide project images");
        } else {
            menuShowHideProjectImages.setText("Show project images");
        }
    }
}
