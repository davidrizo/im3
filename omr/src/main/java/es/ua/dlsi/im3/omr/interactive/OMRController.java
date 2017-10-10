package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenFolderDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowConfirmation;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.model.InputOutput;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRProject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
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
    MenuItem menuDeleteImage;

    @FXML
    MenuItem menuShowHideProjectImages;

    @FXML
    ListView<OMRPage> lvPages;

    @FXML
    ImageView imageView;

    @FXML
    Pane marksPane;

    @FXML
    ToolBar toolBar;

    @FXML
    Slider sliderScale;

    @FXML
    ScrollPane scrollPane;

    @FXML
    ToggleButton btnIdentifyStaves;

    ObjectProperty<OMRProject> project;
    Interaction interaction;

    public OMRController() {
        project = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuSave.disableProperty().bind(project.isNull());
        toolBar.disableProperty().bind(lvPages.getSelectionModel().selectedItemProperty().isNull());
        menuAddImage.disableProperty().bind(project.isNull());
        menuDeleteImage.disableProperty().bind(lvPages.getSelectionModel().selectedItemProperty().isNull());
        lvPages.setCellFactory(new Callback<ListView<OMRPage>, ListCell<OMRPage>>() {
            @Override
            public ListCell<OMRPage> call(ListView<OMRPage> param) {
                return new OMRPageListCell();
            }
        });
        lvPages.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OMRPage>() {
            @Override
            public void changed(ObservableValue<? extends OMRPage> observable, OMRPage oldValue,
                                OMRPage newValue) {
                changeSelectedPage(newValue);
            }
        });

        initScaleSlider();

        interaction = new Interaction(imageView, marksPane, btnIdentifyStaves.selectedProperty());
        btnIdentifyStaves.setTooltip(new Tooltip("Draw a rectangle surrounding each staff"));
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
                InputOutput io = new InputOutput();
                save();
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
                InputOutput io = new InputOutput();
                project.set(io.load(mrt.getParentFile()));
                loadProject();
            } catch (IM3Exception e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot open project", e);
            }
        }
    }

    private void loadProject() throws IM3Exception {
        OMRApp.getMainStage().setTitle("MURET - " + project.get().getName());
        lvPages.setItems(project.get().pagesProperty());
    }

    @FXML
    private void handleSave() {
        if (!project.isNull().get()) {
            try {
                save();
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot save project", e);
            }
        }
    }

    private void save() throws ExportException {
        InputOutput io = new InputOutput();
        io.save(project.get());
    }

    @FXML
    private void handleClose() {
        project.setValue(null);
        // TODO: 9/10/17 Cerrar ventanas....
    }

    @FXML
    private void handleAddImage() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File jpg = dlg.openFile("Select an image", "JPG", "jpg");

        if (jpg != null) {
            try {
                project.get().addPage(jpg);
                save();
            } catch (IM3Exception e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot add image", e);
            }
        }
    }

    @FXML
    private void handleDeleteImage() {
        if (ShowConfirmation.show(OMRApp.getMainStage(), "Do you want to delete the image?")) {
            try {
                project.get().deletePage(lvPages.getSelectionModel().getSelectedItem());
                save();
                lvPages.getSelectionModel().clearSelection();
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot delete image", e);
            }
        }
    }

    @FXML
    private void handleShowHideProjectImages() {
        lvPages.setVisible(!lvPages.isVisible());

        if (lvPages.isVisible()) {
            menuShowHideProjectImages.setText("Hide project images");
        } else {
            menuShowHideProjectImages.setText("Show project images");
        }
    }

    @FXML
    private void handleFitToWindow() {
        fitToWindow();
    }

    private void fitToWindow() {
        if (imageView.getLayoutBounds().getWidth() > imageView.getLayoutBounds().getHeight()) {
            sliderScale.setValue((scrollPane.getViewportBounds().getWidth()) / imageView.getLayoutBounds().getWidth());
        } else {
            sliderScale.setValue((scrollPane.getViewportBounds().getHeight()) / imageView.getLayoutBounds().getHeight());
        }
    }

    private void initScaleSlider() {
        sliderScale.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                imageView.getTransforms().clear();
                marksPane.getTransforms().clear();
                imageView.getTransforms().add(new Scale(t1.doubleValue(), t1.doubleValue(), 0, 0));
                marksPane.getTransforms().add(new Scale(t1.doubleValue(), t1.doubleValue(), 0, 0));

            }
        });
    }

    private void changeSelectedPage(OMRPage newValue) {
        imageView.imageProperty().unbind();
        if (newValue != null) {
            imageView.setPreserveRatio(false); // avoid the scaling of original
            // file
            imageView.setFitHeight(0);
            imageView.setFitWidth(0);
            imageView.imageProperty().bind(newValue.imageProperty());

            ///changeSelectedTagsFile(newValue.tagsFileProperty().get()); // FIXME: 10/10/17

            //marksPane.getChildren().clear();
            marksPane.prefHeightProperty().unbind();
            marksPane.prefWidthProperty().unbind();
            marksPane.prefHeightProperty().bind(imageView.getImage().heightProperty());
            marksPane.prefWidthProperty().bind(imageView.getImage().widthProperty());

            fitToWindow();

            ///createScoreView(); // FIXME: 10/10/17
        }
    }
}
