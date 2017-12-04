package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowConfirmation;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.model.OMRModel;
import es.ua.dlsi.im3.omr.interactive.pages.PagesController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    MenuItem menuSave;

    @FXML
    MenuItem menuItemUndo;

    @FXML
    MenuItem menuItemRedo;

    @FXML
    ToggleGroup tgDashboardButtons;

    @FXML
    ToggleButton tbPages;

    @FXML
    BorderPane borderPane;

    StringProperty title;

    CommandManager commandManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tbPages.disableProperty().bind(OMRModel.getInstance().currentProjectProperty().isNull()
                .or(tbPages.selectedProperty()));

        menuSave.disableProperty().bind(OMRModel.getInstance().currentProjectProperty().isNull());
        //menuProject.disableProperty().bind(OMRModel.getInstance().currentProjectProperty().isNull());
        //TODO menuAddImage.disableProperty().bind(tbPages.selectedProperty().not());
        //TODO menuDeleteImage.disableProperty().bind(lvPages.getSelectionModel().selectedItemProperty().isNull());

        commandManager = new CommandManager();
        menuItemRedo.disableProperty().bind(commandManager.redoAvailableProperty().not());
        menuItemUndo.disableProperty().bind(commandManager.undoAvailableProperty().not());

        title = new SimpleStringProperty("");
        SimpleStringProperty changedProperty = new SimpleStringProperty("");
        OMRApp.getMainStage().titleProperty().bind(Bindings.concat("MURET ", title, " ", changedProperty));

        commandManager.commandAppliedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue.booleanValue()) {
                    changedProperty.set("(modified)");
                } else {
                    changedProperty.set("");
                }
            }
        });

    }

    @FXML
    public void handleNewProject(ActionEvent actionEvent) {
        NewOpenProjectDialogController dlg = new NewOpenProjectDialogController(OMRApp.getMainStage(), "New project");
        if (dlg.show()) {
            //borderPane.getScene().getRoot().setCursor(Cursor.WAIT);
            try {
                OMRModel.getInstance().createProject(dlg.getProjectFolder(), dlg.getTrainingFile());
                title.setValue(OMRModel.getInstance().getCurrentProject().getName());
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
                title.setValue(OMRModel.getInstance().getCurrentProject().getName());
                openPagesView();
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot open project", e);
            }
        }
    }

    @FXML
    public void handleUndo() {
        try {
            commandManager.undo();
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot undo", e);
        }
    }

    @FXML
    public void handleRedo() {
        try {
            commandManager.redo();
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot redo", e);
        }
    }

    private void openPagesView() {
        tgDashboardButtons.selectToggle(tbPages); // it opens the page
        try {
            Pair<PagesController, Parent> pair = ViewLoader.loadView("pages.fxml");
            borderPane.setCenter(pair.getY());
            pair.getX().setCommandManager(commandManager);
            pair.getX().setDashboard(this);
            //pair.getX().initMenus(menuItemUndo, menuItemRedo);
        } catch (IOException e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot load pages screen", e);
        }
    }


    @FXML
    private void handleOpenPages() {
        openPagesView();
    }

    public void save() {
        try {
            OMRModel.getInstance().save();
            commandManager.resetNeedsSave();
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot save project", e);
        }
    }
    @FXML
    private void handleSave() {
        save();
    }

    @FXML
    private void handleClose() {
        //TODO Preguntar si salvar
        if (commandManager.commandAppliedProperty().get()) {
            if (ShowConfirmation.show(OMRApp.getMainStage(), "This project has been modified, do you really want to close without save?")) {
                borderPane.setCenter(null);
                tgDashboardButtons.selectToggle(null);
                OMRModel.getInstance().clearProject();
                OMRApp.getMainStage().setTitle("MURET");
            }
        }
    }


    @FXML
    private void handleQuit() {
        //TODO Preguntar si salvar
        if (commandManager.commandAppliedProperty().get()) {
            if (ShowConfirmation.show(OMRApp.getMainStage(), "This project has been modified, do you really want to exit without save?")) {
                OMRApp.getMainStage().close();
            }
        }
    }
}
