package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import es.ua.dlsi.im3.omr.muret.model.OMRProject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class OrderImagesController implements Initializable {
    @FXML
    Text textTitle;
    @FXML
    Text textComposer;
    @FXML
    ScrollPane scrollPane;
    @FXML
    FlowPane flowPaneOrderImages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        flowPaneOrderImages.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    public void loadOMRProject() {
        OMRProject omrProject = MuRET.getInstance().getModel().getCurrentProject();
        textTitle.setText(omrProject.getName());
        textComposer.setText(omrProject.getComposer());

        try {
            for (OMRImage omrImage : omrProject.imagesProperty()) {
                createImageButton(omrImage);
            }
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load image", e);
        }

        createAddButton();
    }

    private void createImageButton(OMRImage omrImage) throws IM3Exception {
        ImageThumbnailView imageThumbnailView = new ImageThumbnailView(omrImage);
        imageThumbnailView.getStyleClass().add("imagethumbnail");
        flowPaneOrderImages.getChildren().add(imageThumbnailView);

        imageThumbnailView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    openImage(omrImage);
                }
            }
        });
    }

    private void openImage(OMRImage omrImage) {
        DocumentAnalysisController controller = (DocumentAnalysisController) MuRET.getInstance().openWindow("/fxml/muret/documentanalysis.fxml", true, true);
        try {
            controller.loadOMRImage(omrImage);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot open image", e);
            ShowError.show(MuRET.getInstance().getMainStage(), "Cannot open image", e);
        }
    }

    private void createAddButton() {
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                OpenSaveFileDialog openSaveFileDialog = new OpenSaveFileDialog();
                //TODO
                //File file = openSaveFileDialog.openFile("Select a MuRET project file", "MuRET files", "mrt");
                //openProject(file);
            }
        };
        Button button = Utils.addOpenOtherProjectButton("/fxml/muret/images/orderimages_add.png", 188, 245, "buttonOrderImagesPortrait", eventHandler);
        flowPaneOrderImages.getChildren().add(button);
    }

    @FXML
    private void handleClose() {
        MuRET.getInstance().closeCurrentWindow();
    }

}
