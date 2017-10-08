package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.interactive.components.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class OMRMainController implements Initializable{
    @FXML
    Button btnAddImage;

    @FXML
    Button btnRemoveImage;

    @FXML
    ListView<ScoreImageFile> lvImages;

    @FXML
    ImageView imageView;

    @FXML
    Pane marksPane;

    @FXML
    Slider sliderScale;

    @FXML
    ToolBar toolBar;

    @FXML
    ListView<SymbolView> lvSymbols;

    @FXML
    Label labelTrainingModelSymbols;
    @FXML
    ScrollPane scrollPane;

    @FXML
    AnchorPane anchorPaneScore;



    ScoreImageTagsView currentScoreImageTags;

    ObjectProperty<OMRProject> model;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnRemoveImage.disableProperty().bind(lvImages.getSelectionModel().selectedItemProperty().isNull());
        model = new SimpleObjectProperty<>();
        btnAddImage.disableProperty().bind(model.isNull());
        lvImages.setCellFactory(new Callback<ListView<ScoreImageFile>, ListCell<ScoreImageFile>>() {
            @Override
            public ListCell<ScoreImageFile> call(ListView<ScoreImageFile> param) {
                return new ScoreImageFileListCell();
            }
        });

        lvImages.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ScoreImageFile>() {
            @Override
            public void changed(ObservableValue<? extends ScoreImageFile> observable, ScoreImageFile oldValue,
                                ScoreImageFile newValue) {
                try {
                    changeSelectedImageScore(newValue);
                } catch (IM3Exception e) {
                    ShowError.show(OMRApp.getMainStage(), "Cannot show selected image score", e);
                }
            }
        });

        lvSymbols.setCellFactory(new Callback<ListView<SymbolView>, ListCell<SymbolView>>() {
            @Override
            public ListCell<SymbolView> call(ListView<SymbolView> param) {
                return new SymbolListCell();
            }
        });


        toolBar.disableProperty().bind(lvImages.getSelectionModel().selectedItemProperty().isNull());

        //initInteraction(); // FIXME: 7/10/17
        initScaleSlider();
    }

    private void initScaleSlider() {
        sliderScale.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if (currentScoreImageTags != null) {
                    // FIXME: 7/10/17 TO-DO ActionLogger
                    /*if (t == null || t.doubleValue() < t1.doubleValue()) {
                        ActionLogger.log(UserActionsPool.zoomIn, currentScoreImageTags.getName());
                    } else {
                        ActionLogger.log(UserActionsPool.zoomOut, currentScoreImageTags.getName());
                    }*/
                /*
                 * stackPane.getTransforms().clear();
                 * stackPane.getTransforms().add(new Scale(t1.doubleValue(),
                 * t1.doubleValue(), 0, 0));
                 */
                }

                imageView.getTransforms().clear();
                marksPane.getTransforms().clear();
                imageView.getTransforms().add(new Scale(t1.doubleValue(), t1.doubleValue(), 0, 0));
                marksPane.getTransforms().add(new Scale(t1.doubleValue(), t1.doubleValue(), 0, 0));

            }
        });
    }

    private void changeSelectedImageScore(ScoreImageFile newValue) throws IM3Exception {
        //resetLoggingSession();
        imageView.imageProperty().unbind();
        if (newValue != null) {
            imageView.setPreserveRatio(false); // avoid the scaling of original
            // file
            imageView.setFitHeight(0);
            imageView.setFitWidth(0);
            imageView.imageProperty().bind(newValue.imageProperty());

            changeSelectedTagsFile(newValue.tagsFileProperty().get());

            marksPane.getChildren().clear();
            marksPane.prefHeightProperty().unbind();
            marksPane.prefWidthProperty().unbind();
            marksPane.prefHeightProperty().bind(imageView.getImage().heightProperty());
            marksPane.prefWidthProperty().bind(imageView.getImage().widthProperty());

            fitToWindow();

            createScoreView();
        }
    }

    private void changeSelectedTagsFile(ScoreImageTags sit) throws IM3Exception {
        marksPane.getChildren().clear();
        lvSymbols.getItems().clear();
        //btnSave.disableProperty().unbind();
        if (sit != null) {
            currentScoreImageTags = new ScoreImageTagsView(sit);
            //btnSave.disableProperty().bind(sit.changedProperty().not());
            marksPane.getChildren().add(currentScoreImageTags); // ScoreImageTagsView
            // is a group
            // that contains
            // all symbols
            lvSymbols.setItems(currentScoreImageTags.symbolViewsProperty());
            // for (Symbol s: sit.symbolsProperty()) {
            // drawSymbol(s);
            // }
        } else {
            currentScoreImageTags = null;
            /// lvSymbols.itemsProperty().unbind();
        }
    }

    private void fitToWindow() {
        // TODO: 7/10/17 SLIDER
        if (imageView.getLayoutBounds().getWidth() > imageView.getLayoutBounds().getHeight()) {
            sliderScale.setValue((scrollPane.getViewportBounds().getWidth()) / imageView.getLayoutBounds().getWidth());
        } else {
            sliderScale.setValue((scrollPane.getViewportBounds().getHeight()) / imageView.getLayoutBounds().getHeight());
        }
    }

    @FXML
    private void handleTest() {
        // TODO: 8/10/17 Quitar
        createProject(new File("/Users/drizo/cmg/investigacion/training_sets/sources/tonalanalysis/TMP/A_IM3_MENSURAL_NEWTAGGER/data.train"));
        try {
            model.get().addImage(new File("/Users/drizo/cmg/investigacion/training_sets/sources/tonalanalysis/TMP/A_IM3_MENSURAL_NEWTAGGER/12633.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot add test image", e);
        }
    }

    private void createScoreView() throws IM3Exception {
        HashMap<Staff, LayoutFonts> layoutFontsHashMap = new HashMap<>();
        layoutFontsHashMap.put(model.get().getStaff(), LayoutFonts.capitan);
        layoutFontsHashMap.put(model.get().getModernStaff(), LayoutFonts.bravura);

        ScoreSongView scoreSongView = new ScoreSongView(model.get().getSong(),
                layoutFontsHashMap, anchorPaneScore.widthProperty(), anchorPaneScore.heightProperty());
        anchorPaneScore.getChildren().add(scoreSongView.getMainPanel());
        AnchorPane.setLeftAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setRightAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setTopAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setBottomAnchor(scoreSongView.getMainPanel(), 0.0);
    }


    @FXML
    private void handleSaveProject() {
        // TODO: 7/10/17
    }
    @FXML
    private void handleAddImage() {
        doAddImage();
    }

    @FXML
    private void handleRemoveImage() {

    }

    @FXML
    private void handleFitToWindow() {
        fitToWindow();
    }


    @FXML
    private void handleNewProject() {
        doNewProject();
    }

    @FXML
    private void handleQuit() {
        // TODO: 7/10/17 Comprobar que está todo guardado
        OMRApp.getMainStage().close();
    }

    private void doNewProject() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File trainingFile = dlg.openFile("Select the training dataset", "Train dataset", "train");
        if (trainingFile == null) {
            ShowError.show(OMRApp.getMainStage(), "Cannot build an empty model");
            return;
        } else {
            createProject(trainingFile);
        }

        // TODO: 7/10/17 Nombre proyecto y mostrarlo en el caption
    }

    private void createProject(File trainingFile) {
        try {
            OMRProject project = new OMRProject(trainingFile);
            model.setValue(project);
            lvImages.setItems(project.filesProperty());
            labelTrainingModelSymbols.setText("Training symbols: " + project.getTrainingModelSymbolCount());
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot create project", e);
        }
    }

    private void doAddImage() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File imageFile = dlg.openFile("Select a score image", "Images", "jpg");

        if (imageFile != null) {
            try {
                model.get().addImage(imageFile);
            } catch (Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot add image", e);
            }
        }

    }


}
