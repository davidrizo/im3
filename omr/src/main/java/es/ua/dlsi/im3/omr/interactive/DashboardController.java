package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.model.OMRModel;
import es.ua.dlsi.im3.omr.interactive.pages.PagesController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    MenuItem menuSave;

    @FXML
    ToggleGroup tgDashboardButtons;

    @FXML
    ToggleButton tbPages;

    @FXML
    BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tbPages.disableProperty().bind(OMRModel.getInstance().currentProjectProperty().isNull()
                .or(tbPages.selectedProperty()));

        menuSave.disableProperty().bind(OMRModel.getInstance().currentProjectProperty().isNull());
        //menuProject.disableProperty().bind(OMRModel.getInstance().currentProjectProperty().isNull());
        //TODO menuAddImage.disableProperty().bind(tbPages.selectedProperty().not());
        //TODO menuDeleteImage.disableProperty().bind(lvPages.getSelectionModel().selectedItemProperty().isNull());

    }

    @FXML
    public void handleNewProject(ActionEvent actionEvent) {
        NewOpenProjectDialogController dlg = new NewOpenProjectDialogController(OMRApp.getMainStage(), "New project");
        if (dlg.show()) {
            //borderPane.getScene().getRoot().setCursor(Cursor.WAIT);
            try {
                OMRModel.getInstance().createProject(dlg.getProjectFolder(), dlg.getTrainingFile());
                OMRApp.getMainStage().setTitle("MURET Project " + OMRModel.getInstance().getCurrentProject().getName());
                openPagesView();
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot create project", e);
            }
            //borderPane.getScene().getRoot().setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    public void handleOpenProject(ActionEvent actionEvent) {
        NewOpenProjectDialogController dlg = new NewOpenProjectDialogController(OMRApp.getMainStage(), "Open project");
        if (dlg.show()) {
            //borderPane.getScene().getRoot().setCursor(Cursor.WAIT);
            try {
                OMRModel.getInstance().openProject(dlg.getProjectFolder(), dlg.getTrainingFile());
                OMRApp.getMainStage().setTitle("MURET Project " + OMRModel.getInstance().getCurrentProject().getName());
                openPagesView();
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot open project", e);
            }
        }
    }

    private void openPagesView() {
        tgDashboardButtons.selectToggle(tbPages); // it opens the page
        try {
            Pair<PagesController, Parent> pair = ViewLoader.loadView("pages.fxml");
            borderPane.setCenter(pair.getY());
            /*pair.getX().setAddImageMenuItem(menuAddImage);
            pair.getX().setDeleteImageMenuItem(menuDeleteImage);
            pair.getX().initMenus();*/
        } catch (IOException e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot load pages screen", e);
        }
    }


    @FXML
    private void handleOpenPages() {
        openPagesView();
    }

    @FXML
    private void handleSave() {
        try {
            OMRModel.getInstance().save();
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot save project", e);
        }
    }

    @FXML
    private void handleClose() {
        borderPane.setCenter(null);
        tgDashboardButtons.selectToggle(null);
        OMRModel.getInstance().clearProject();
        OMRApp.getMainStage().setTitle("MURET");
        // TODO: 9/10/17 Cerrar ventanas....
    }


    @FXML
    private void handleQuit() {
        // TODO: 9/10/17 Preguntar si hay algo abierto
        OMRApp.getMainStage().close();
    }
}
