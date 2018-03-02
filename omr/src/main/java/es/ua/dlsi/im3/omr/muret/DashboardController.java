package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.javafx.ViewLoader;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowConfirmation;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.editpage.IPagesController;
import es.ua.dlsi.im3.omr.muret.model.OMRInstrument;
import es.ua.dlsi.im3.omr.muret.model.OMRModel;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.pages.PageThumbnailView;
import es.ua.dlsi.im3.omr.muret.pages.PagesController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    MenuItem menuSave;

    @FXML
    MenuItem menuItemUndo;

    @FXML
    MenuItem menuItemRedo;

    @FXML
    ToolBar toolbarPages;

    @FXML
    ToolBar toolbarPageEditStep;

    @FXML
    ToggleGroup tgDashboardButtons;

    @FXML
    ToggleButton tbPages;

    ToggleButton tbSelectedPage;

    @FXML
    BorderPane borderPane;

    @FXML
    AnchorPane centerPane;

    @FXML
    ToggleGroup tgPageStep;
    @FXML
    ToggleButton tgRegions;

    @FXML
    ToggleButton tgSymbols;

    @FXML
    ToggleButton tgMusic;

    StringProperty title;

    OMRModel omrModel;
    CommandManager commandManager;
    private List<OMRPage> openedPages;
    private OMRPage selectedPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toolbarPageEditStep.setDisable(true);
        omrModel = new OMRModel();
        toolbarPages.disableProperty().bind(omrModel.currentProjectProperty().isNull());

        tbPages.disableProperty().bind(tbPages.selectedProperty());

        menuSave.disableProperty().bind(omrModel.currentProjectProperty().isNull());
        //menuProject.disableProperty().bind(omrModel.currentProjectProperty().isNull());
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
        initPageEditStepButtons();

    }

    private void initPageEditStepButtons() {
        tgPageStep.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                try {
                    if (newValue == tgRegions) {
                        editPage("editregions.fxml");
                    } else if (newValue == tgSymbols) {
                        editPage("editsymbols.fxml");
                    } else if (newValue == tgMusic) {
                        editPage("editmusic.fxml");
                    } else if (newValue == null) {
                        closePage();
                    } else {
                        throw new IM3Exception("No step identified: " + newValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ShowError.show(OMRApp.getMainStage(), "Cannot edit page", e);
                }
            }
        });
    }

    @FXML
    public void handleNewProject(ActionEvent actionEvent) {
        NewOpenProjectDialogController dlg = new NewOpenProjectDialogController(OMRApp.getMainStage(), "New project", true);
        if (dlg.show()) {
            //borderPane.getScene().getRoot().setCursor(Cursor.WAIT);
            try {
                if (dlg.getProjectFolder() == null) {
                    throw new IM3Exception("Must select a project folder");
                }
                if (dlg.getTrainingFile() == null) {
                    throw new IM3Exception("Must select a training file");
                }
                omrModel.createProject(dlg.getProjectFolder(), dlg.getTrainingFile(), dlg.getNotationType());
                title.setValue(omrModel.getCurrentProject().getName());
                openPagesView();
            } catch (Throwable e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot create project", e);
            }
            //borderPane.getScene().getRoot().setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    public void handleOpenProject(ActionEvent actionEvent) {
        boolean open = true;
        if (omrModel.getCurrentProject() != null) {
            open = doClose();
        }
        if (open) {
            NewOpenProjectDialogController dlg = new NewOpenProjectDialogController(OMRApp.getMainStage(), "Open project", false);
            if (dlg.show()) {
                //borderPane.getScene().getRoot().setCursor(Cursor.WAIT);
                try {
                    omrModel.openProject(dlg.getProjectFolder(), dlg.getTrainingFile());
                    title.setValue(omrModel.getCurrentProject().getName());
                    openPagesView();
                } catch (Throwable e) {
                    e.printStackTrace();
                    ShowError.show(OMRApp.getMainStage(), "Cannot open project", e);
                }
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
        tgDashboardButtons.selectToggle(tbPages);
        try {
            Pair<PagesController, Parent> pair = ViewLoader.loadView("pages.fxml");
            pair.getX().setDashboard(this);
            setMainPane(pair.getY());
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
            omrModel.save();
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
        doClose();
    }

    /**
     * @return True if closed
     */
    private boolean doClose() {
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
            borderPane.setCenter(null);
            tgDashboardButtons.selectToggle(null);
            omrModel.clearProject();
            OMRApp.getMainStage().setTitle("MURET");

            if (openedPages != null) {
                openedPages.clear();
            }
            selectedPage = null;
            toolbarPages.getItems().clear();
            toolbarPages.getItems().add(tbPages);
        }
        return close;
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

    public void openPage(PageThumbnailView pageView) {
        try {
            if (pageView.getOmrPage().getInstrumentList().isEmpty()) {
                throw new IM3Exception("No instrument assigned yet to the page");
            }

            // ask for all pages containing the same instruments in this page view
            List<OMRPage> pagesToOpen = new ArrayList<>();
            for (OMRPage page: omrModel.getCurrentProject().pagesProperty()) {
                for (OMRInstrument instrument: pageView.getOmrPage().getInstrumentList()) {
                    if (page.containsInstrument(instrument)) {
                        pagesToOpen.add(page);
                        break;
                    }
                }
            }

            if (pagesToOpen.isEmpty()) {
                throw new IM3Exception("At least this page (" + pageView.getOmrPage() + ") should be opened");
            }


            // look if we have opened it first
            Toggle found = null;
            for (Toggle toggle: tgDashboardButtons.getToggles()) {
                if (toggle.getUserData() == pageView) {
                    found = toggle;
                    break;
                }
            }

            if (found != null) {
                tbSelectedPage = (ToggleButton) found;
            } else {
                tbSelectedPage = new ToggleButton(pageView.getOmrPage().toString());
                tbSelectedPage.setUserData(pageView);
                tgDashboardButtons.getToggles().add(tbSelectedPage);
                toolbarPages.getItems().add(tbSelectedPage);
                tbSelectedPage.setOnAction(event -> {
                    openPage(pageView);
                });
            }
            tgDashboardButtons.selectToggle(tbSelectedPage);

            openedPages = pagesToOpen;
            selectedPage = pageView.getOmrPage();
            tgRegions.setSelected(true); // it will open the regions page
            // editRegions();

            toolbarPageEditStep.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot load pages screen", e);
        }
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

    public CommandManager getCommandManager() {
        return commandManager;
    }

    private <ControllerType extends IPagesController> void editPage(String fxml) throws IOException, IM3Exception {
        Pair<ControllerType, Parent> pair = ViewLoader.loadView(fxml);
        setMainPane(pair.getY());
        pair.getX().setDashboard(this);
        pair.getX().setPages(selectedPage, openedPages);
    }

    private void closePage() {
        setMainPane(null);
    }


    public OMRModel getModel() {
        return omrModel;
    }
}
