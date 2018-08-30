package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.javafx.ViewLoader;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowConfirmation;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowMessage;
import es.ua.dlsi.im3.omr.muret.imagesold.ImageThumbnailView;
import es.ua.dlsi.im3.omr.muret.imagesold.ImagesController;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import es.ua.dlsi.im3.omr.muret.model.OMRModel;
import es.ua.dlsi.im3.omr.muret.regions.DocumentAnalysisController;
import es.ua.dlsi.im3.omr.muret.symbols.SymbolCorrectionController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public class DashboardController implements Initializable {
    @FXML
    MenuItem menuItemUndo;

    @FXML
    MenuItem menuItemRedo;
    @FXML
    BorderPane mainBorderPane;
    @FXML
    private AnchorPane centerPane;
    @FXML
    private ProgressBar progressBar;

    /**
     * GUI model
     */
    OMRModel omrModel;
    /**
     * Caption title
     */
    StringProperty title;

    /**
     * Main command manager
     */
    CommandManager commandManager;
    /**
     * View and controller of imagesold view
     */
    private Pair<ImagesController,Parent> imagesViewPair;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        omrModel = new OMRModel();
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

        progressBar.prefWidthProperty().bind(mainBorderPane.widthProperty());
        progressBar.setVisible(false);
    }

    @FXML
    public void handleAbout() {
        ShowMessage.show(OMRApp.getMainStage(), "Music recognition, encoding, and transcription (MURET)\n@2018 David Rizo Valero (drizo@dlsi.ua.es)\nv"
        + Version.VERSION);
    }

    @FXML
    public void handleClose() {
        doCloseProject();
    }

    //TODO Poner barra progreso lectura training set
    @FXML
    public void handleOpenProject() {
        boolean open = true;
        if (omrModel.getCurrentProject() != null) {
            open = doCloseProject();
        }
        if (open) {
            try {
                NewOpenProjectDialogController dlg = new NewOpenProjectDialogController(OMRApp.getMainStage(), "Open project", false);
                if (dlg.show()) {
                    //borderPane.getScene().getRoot().setCursor(Cursor.WAIT);
                        omrModel.openProject(dlg.getProjectFolder(), dlg.getTrainingFolder());
                        title.setValue(omrModel.getCurrentProject().getName());
                        openImagesView();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot open project", e);
            }
        }
    }

    @FXML
    public void handleNewProject() {
        NewOpenProjectDialogController dlg = new NewOpenProjectDialogController(OMRApp.getMainStage(), "New project", true);
        if (dlg.show()) {
            //borderPane.getScene().getRoot().setCursor(Cursor.WAIT);
            try {
                if (dlg.getProjectFolder() == null) {
                    throw new IM3Exception("Must onSelect a project folder");
                }
                //if (dlg.getTrainingFile() == null) {
                //    throw new IM3Exception("Must onSelect a training file");
                //}
                omrModel.createProject(dlg.getProjectFolder(), dlg.getNotationType());
                title.setValue(omrModel.getCurrentProject().getName());
                //openImagesView();
            } catch (Throwable e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot create project", e);
            }
            //borderPane.getScene().getRoot().setCursor(Cursor.DEFAULT);
        }

    }

    @FXML
    public void handleSaveProject() {
        save();
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

    public void save() {
        try {
            omrModel.save();
            commandManager.resetNeedsSave();
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot save project", e);
        }
    }

    /**
     * @return True if closed
     */
    private boolean doCloseProject() {
        boolean close = false;
        //TODO Preguntar si salvar
        if (commandManager.commandAppliedProperty().get()) {
            if (ShowConfirmation.show(OMRApp.getMainStage(), "This project has been modified, do you really want to close without save?")) {
                close = true;
            }
        } else {
            close = true;
        }

        if (close) {
            imagesViewPair = null;
            centerPane.getChildren().clear();
            omrModel.clearProject();
            title.setValue("MURET");
        }
        return close;
    }

    private void setMainPane(Node node) {
        centerPane.getChildren().clear();
        if (node != null) {
            centerPane.getChildren().add(node);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        }
    }


    public OMRModel getModel() {
        return omrModel;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void openImagesView() {
        try {
            if (imagesViewPair == null) {
                imagesViewPair = ViewLoader.loadView("muret/imagesold.fxml");
                imagesViewPair.getX().setDashboard(this);
            }
            setMainPane(imagesViewPair.getY());
            //pair.getX().initMenus(menuItemUndo, menuItemRedo);
        } catch (IOException e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot load imagesold screen", e);
        }
    }


    public void openImageDocumentAnalysis(ImageThumbnailView pageView) {
        openImageDocumentAnalysis(pageView.getOMRImage());
    }

    public void openImageDocumentAnalysis(OMRImage omrImage) {
        try {
            Pair<DocumentAnalysisController, Parent> pair = ViewLoader.loadView("muret/documentanalysis.fxml");
            pair.getX().setDashboard(this);
            pair.getX().setOMRImage(omrImage);
            setMainPane(pair.getY());
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot load document analysis screen", e);
        }
    }

    public void openImageSymbolCorrection(OMRImage omrImage) {
        try {
            Pair<SymbolCorrectionController, Parent> pair = ViewLoader.loadView("muret/symbolcorrection.fxml");
            pair.getX().setDashboard(this);
            pair.getX().setOMRImage(omrImage);
            setMainPane(pair.getY());
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot load document analysis screen", e);
        }
    }
     @FXML
     public void handleCheckIntegrity() {
        String messages = omrModel.getCurrentProject().checkIntegrity();
        if (messages == null || messages.isEmpty()) {
            ShowMessage.show(OMRApp.getMainStage(), "Everything is correct");
        } else {
            IM3Exception errors = new IM3Exception(messages);
            ShowError.show(OMRApp.getMainStage(), "Errors found in the project", errors);
        }
     }

    public OMRModel getOmrModel() {
        return omrModel;
    }
}
