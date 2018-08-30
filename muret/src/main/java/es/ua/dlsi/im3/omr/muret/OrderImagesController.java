package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.muret.images.ImageThumbnailView;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import es.ua.dlsi.im3.omr.muret.model.OMRProject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
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

    public void loadOMRProject(OMRProject omrProject) {
        textTitle.setText(omrProject.getName());
        textComposer.setText(omrProject.getComposer());

        try {
            for (OMRImage omrImage : omrProject.imagesProperty()) {
                createImageButton(omrImage);
            }
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load image", e);
        }
    }

    private void createImageButton(OMRImage omrImage) throws IM3Exception {
        ImageThumbnailView imageThumbnailView = new ImageThumbnailView(omrImage);
        flowPaneOrderImages.getChildren().add(imageThumbnailView);
    }
}
