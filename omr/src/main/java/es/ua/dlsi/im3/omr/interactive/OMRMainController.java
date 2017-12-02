package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowConfirmation;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.model.*;
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

public class OMRMainController implements Initializable {
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
    Slider sliderTimer;

    @FXML
    ScrollPane scrollPane;

    @FXML
    ToggleButton btnIdentifyStaves;

    @FXML
    KeyboardInteraction keyboardInteraction;

    CommandManager commandManager;
    PageInteraction interaction;

    public OMRMainController() {
        commandManager = new CommandManager();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        keyboardInteraction = new KeyboardInteraction(OMRApp.getMainStage().getScene());
        toolBar.disableProperty().bind(lvPages.getSelectionModel().selectedItemProperty().isNull());

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

        interaction = new PageInteraction(this, imageView, marksPane, btnIdentifyStaves.selectedProperty());
        btnIdentifyStaves.setTooltip(new Tooltip("Draw a rectangle surrounding each staff"));
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

    public void onStaffIdentified(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) throws IM3Exception {
        OMRPage page = getSelectedPage();
        // we use the same pane for all marks, symbols, etc... to use absolute coordinates in all cases
        OMRStaff staff = new OMRStaff(OMRModel.getInstance().getCurrentProject(), page, topLeftX, topLeftY, bottomRightX, bottomRightY);
        page.addStaff(staff);
        marksPane.getChildren().add(staff.getRoot());
        btnIdentifyStaves.setSelected(false);
    }

    private OMRPage getSelectedPage() {
        return lvPages.getSelectionModel().getSelectedItem();
    }

    public Pane getMarksPane() {
        return marksPane;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Slider getSliderTimer() {
        return sliderTimer;
    }

}
